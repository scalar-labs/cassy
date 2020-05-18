# Getting Started with Cassy

## Prerequisite

Cassy requires the following software to run: 
* Oracle JDK 8 (or OpenJDK 8) (it should work with 9, but probably not with 10+)
* SQLite 3 (or JDBC-supported relational DBMS)
* Golang (if you use HTTP/1.1 interface)
  * Cassy supports HTTP/1.1 interface with gRPC gateway that is implemented by golang


Of-course, you need a well-configured Cassandra cluster that you can manage. The following Cassandra configurations are required to be updated to run Cassy properly.
* Set `LOCAL_JMX=no` for enabling remote JMX and set `com.sun.management.jmxremote.authenticate=false` to disable authentication in cassandra-env.sh since the current version does not support JMX authentication. (Please do not expose the JMX port to the internet.)

Furthermore, the following configurations are recommended (but not required) to be updated.
* Set `incremental_backups=true` in cassandra.yaml if you want to use incremental backups.

From here, it assumes there is a multi-node Cassandra cluster (192.168.0.10, ...) and a node for running the Cassy master daemon (192.168.0.254).

Lastly, the following actions are also required.
* Place a SSH private key without passphrase in the master node and a corresponding public key in each Cassandra node. 
* Permit SSH user to access Cassandra data directory. (Use cassandra user for SSH might be the easiest way)

## Install

Cassy is not currently available on Maven, so you will need to install from the source.

```
# At each node of Cassandra cluster and the Cassy master node
$ git clone https://github.com/scalar-labs/cassy
$ cd cassy
$ ./gradlew installDist

# If you want to use HTTP/1.0, you can do it with gRPC gateway
$ go build entrypoint.go
```

## Configure

To run Cassy properly, it is required to create a property file.
Here is a sample configuration file. Please see the comments above each entry for the meaning of the configuration.
```  
# Port number of Cassy master. If not specified port 20051 is used by default.
#scalar.cassy.server.port=20051

# Port number of JMX. If not specified port 7199 is used by default.
#scalar.cassy.server.jmx_port=7199

# Username for SSH.  
scalar.cassy.server.ssh_user=foo

# Location of the private key file for SSH.
scalar.cassy.server.ssh_private_key_path=/path/to/.ssh/id_rsa

# Path to the bin directory of Cassy in each Cassandra nodes (Cassy assumes all Cassandra nodes install Cassy in the same directory)
scalar.cassy.server.slave_command_path=/path/to/cassy/build/install/cassy/bin

# URI of a blob store to manage backup files.
scalar.cassy.server.storage_base_uri=s3://your-bucket

# Type of storage that Cassy uses to store backup files (aws_s3, azure_blob)
scalar.cassy.server.storage_type=aws_s3

# URL of JDBC for managing some metadata such as backup and restore histories
scalar.cassy.server.metadata_db_url=jdbc:sqlite:cassy.db

# URL of SRV record. It is used to know and pause Cassandra application nodes to take a cluster-wide snapshot. If you don't use the feature, it can be omitted or the value can be left blank.
scalar.cassy.server.srv_service_url=_app._tcp.your-service.com
```
### Storage type configuration
To change the storage that Cassy uses to store backup files, the following properties of the configuration file will need to be modified:
1. `storage_base_uri`: the URI of your AWS S3 bucket or Azure Blob Storage container.
2. `storage_type`: an Enum that tells Cassy which service to use. You can select `aws_s3` for Amazon S3, or `azure_blob` for Azure Blob Storage.

#### AWS-specific configuration
To use AWS S3, you also need to configure the master node and the Cassandra nodes with one of the following ways:
- Place your credentials in a proper location
- Assign proper IAM roles to the instances (it is applicable only when you run them in AWS)

#### Azure-specific configuration
To use Azure Blob Storage, you also need to configure the master node and the Cassandra nodes with one of the following ways:
- Specify a connection string with the environment variable ` AZURE_STORAGE_CONNECTION_STRING` (see [this](https://docs.microsoft.com/en-us/azure/storage/common/storage-configure-connection-string) for more detail)
- Specify service principle in environment variables (Cassy uses `DefaultAzureCredential` internally. Please see [this](https://github.com/Azure/azure-sdk-for-java/blob/master/sdk/identity/azure-identity/README.md#credentials) for more detail)
- Assign proper managed identity to the instances (Cassy uses `DefaultAzureCredential` internally. Please see [this](https://github.com/Azure/azure-sdk-for-java/blob/master/sdk/identity/azure-identity/README.md#credentials) for more detail)

## Use

First of all, let's create a metadata database with the following command.
```
$ sqlite3 cassy.db < scripts/db.schema
```

Let's start a Cassy master with the configuration file `backup-server.properties`.

```
$ build/install/cassy/bin/cassy-server --config ./conf/cassy.properties
```

Now you can run backup and restore through gRPC APIs or HTTP/1.1 REST APIs. For gRPC APIs, you can do it easily with [grpcurl](https://github.com/fullstorydev/grpcurl).
Here we will use grpcurl for the sake of simplicity.

### Register your cluster

You can register your cluster with `RegisterCluster`

```
$ grpcurl -plaintext -d '{"cassandra_host": "192.168.0.10"}' 192.168.0.254:20051 rpc.Cassy.RegisterCluster
```
You need to specify an address of one of Cassandra nodes to register your cluster. All the other cluster information such as cluster name, nodes' IP addresses, keyspaces and the data directory are retrieved by JMX and are saved in the metadata database for later use.

### List your clusters

You can also view your cluster information that you registered with `ListClusters`.

```
# List all registered clusters.
$ grpcurl -plaintext 192.168.0.254:20051 rpc.Cassy.ListClusters

# Show only the specified cluster.
$ grpcurl -plaintext -d '{"cluster_id": "CLUSTER-ID"}' 192.168.0.254:20051 rpc.Cassy.ListClusters

# List three recently registered clusters.
$ grpcurl -plaintext -d '{"limit": 3}' 192.168.0.254:20051 rpc.Cassy.ListClusters
```

### Take backups

You can take a snapshot with `TakeBackup` with `"backup_type": 2`. It will take a snapshot for each node in the specified cluster and upload the snapshots to the blob store.

```
# It asks each node to clean up old snapshots and take a new snapshot (with flush), and upload the snapshot to the specified location. It also asks each node to remove old incremental backups.
$ grpcurl -plaintext -d '{"cluster_id": "CLUSTER-ID", "backup_type": 2}' 192.168.0.254:20051 rpc.Cassy.TakeBackup 

# You can also specify nodes.
$ grpcurl -plaintext -d '{"cluster_id": "CLUSTER-ID", "target_ips": ["192.168.0.2"], "backup_type": 2}' 192.168.0.254:20051 rpc.Cassy.TakeBackup 
```

When you want to upload incremental backups, please specify `"backup_type": 3` instead. 
You will also need to specify a `snapshot_id` because incremental backups are differences from a previous snapshot.

```
# It asks each node to upload incremental backups to the specified location.
$ grpcurl -plaintext -d '{"cluster_id": "CLUSTER-ID", "snapshot_id": "SNAPSHOT-ID", "backup_type": 3}' 192.168.0.254:20051 rpc.Cassy.TakeBackup 
```

(NOTE: Cassandra incremental backups are created per memtable flush and it is not controllable from outside of system. So what Cassy does is just upload incremental backups.)

### Take cluster-wide consistent backups

You can take cluster-wide consistent backups with `TakeBackup` with `"backup_type": 1`. 

```
$ grpcurl -plaintext -d '{"cluster_id": "CLUSTER-ID", "backup_type": 1}' 192.168.0.254:20051 rpc.Cassy.TakeBackup 
```

Cassy master asks a DNS to resolve the service URL to get a list of IPs and ports of Cassandra applications, and ask those nodes to stop processing. The node that is asked to stop will finish already received requests and will be in a pause mode afterward and return a response to Cassy master. The master waits for all responses, and takes snapshots of all Cassandra nodes. Then the master unpauses the application nodes and uploads all the snapshots.

Note that applications must implement a pause feature to do this. Cassy currently supports gRPC endpoints for pause. For more detail, please take a look at the source code.

If the cluster-wide backup is taken during a non-busy time, the paused duration will be very short like one second, and it won't drop any requests if applications properly queue requests during pause.


### List backups and their statues

You can view available backups with `ListBackups`.

```
$ grpcurl -plaintext -d '{"limit": 3, "cluster_id": "CLUSTER-ID"}' 192.168.0.254:20051  rpc.Cassy.ListBackups
```

### Restore backups to a node

You can restore a node with `RestoreBackup` with `"restore_type": 2`.
If `target_ips` is omitted, it will restore the specified backups to all the nodes.
Note that there should be no data, commitlogs, hints in a node that is being recovered, and the Cassandra daemon should be stopped.

```
$ grpcurl -plaintext -d '{"snapshot_id": "SNAPSHOT-ID", "restore_type": 2, "cluster_id": "CLUSTER-ID", "target_ips": ["192.168.0.11"]}' 192.168.0.254:20051 rpc.Cassy.RestoreBackup
// Start up Cassandra and do repair
```

It will download the specified snapshot and the incremental backups from the blob store into Cassandra data directory of "192.168.0.11" with a directory named `node-backup` and place the files properly for Cassandra to start up with the files. Then start up Cassandra, and do repair if replica consistency is important. 

You can also specify `"snapshot-only": true` to make it restore only snapshots without any incremental backups. This option is valid when incremental backups are broken or corrupted for some reason.

### Restore backups to a cluster

You can restore a cluster with `RestoreBackup` with `"restore_type": 1`.

```
$ grpcurl -plaintext -d '{"snapshot_id": "SNAPSHOT-ID", "restore_type": 1, "cluster_id": "CLUSTER-ID"}' 192.168.0.254:20051 rpc.Cassy.RestoreBackup
```

### List restore statues

You can view restore statues with `ListRestoreStatuses`.

```
$ grpcurl -plaintext -d '{"limit": 3, "cluster_id": "CLUSTER-ID"}' 192.168.0.254:20051 rpc.Cassy.ListRestoreStatuses
```

## Future Work

The following features are planned to be implemented in the near future.
* GUI component to make backup and restore operations even easier. 
