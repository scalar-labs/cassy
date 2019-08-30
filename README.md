# Cassy: A simple and integrated backup tool for Apache Cassandra

You can do the following things with Cassy from an easy to use gRPC APIs or HTTP/1.1 REST APIs:
* Take snapshots, and upload snapshots and incremental backups to a blob store or filesystem of your choice from your cluster
  * AWS S3 is the only supported blob store at the current version, but other blob stores or filesystems will be supported shortly
* Download backups, and restore a node or a cluster from the backups
* Manage backups and restore histories

You can NOT do the followings things with the current version of Cassy:
* Backup commitlogs 
* Select keyspaces to take/restore backups 
* Operate easily with GUI

## Background

The existing Cassandra backup features such as snapshot and incremental backups are great building blocks for doing backup and restore for Cassandra data. However, they are not necessarily easy to use because they are not fully integrated. Moreover, the current backup feature and the existing backup tools are problematic when used with [Scalar DB](https://github.com/scalar-labs/scalardb/) transactions that update multiple records in a transactional (atomic) way as they do not handle transactional consistency of multiple records. 
In order to overcome these problems we created a new backup tool, which makes it easy to do backup and restore operations, and makes it possible to do cluster-wide transactionally consistent backups.

## System Overview

TODO

## How to Use

This section briefly describes how to install and use Cassy.

### Prerequisite

Cassy requires the following software to run: 
* Oracle JDK 8 (or OpenJDK 8) (it should work with 9, but probably not with 10+)
* SQLite 3 (or JDBC-supported relational DBMS)

Of-course, you need a well-configured Cassandra cluster that you can manage. The following Cassandra configurations are required to be updated to run Cassy properly.
* Set `LOCAL_JMX=no` for enabling remote JMX and set `com.sun.management.jmxremote.authenticate=false` to disable authentication in cassandra-env.sh. (Please do not expose the JMX port to the internet.)

Furthermore, the following configurations are recommended (but not required) to be updated.
* Set `incremental_backups=true` in casssandra.yaml if you want to use incremental backups.

From here, it assumes there is a multi-node Cassandra cluster (192.168.0.2, ...) and a node for running the Cassy master daemon (192.168.0.254).

### Install

As of writing this, Cassy is not uploaded to Maven so you will need to install from the source.

```
# At each node of Cassandra cluster and the Cassy master node
$ git clone https://github.com/scalar-labs/cassy
$ cd cassy
$ ./gradlew installDist
$ // TODO: grpc-gateway for HTTP/1.0
```

The following actions are also required:
* Place a SSH private key without passphrase in the master node and a corresponding public key in each Cassandra node. 
* Configure AWS config and credentials properly. (Note that the first version only supports AWS S3 as a blob store.) 
* Permit SSH user to access Cassandra data directory. (Use cassandra user for SSH might be the easiest way)

### Configure

To run Casssy properly, it is required to create a property file.
Here is a sample configuration file. Please see the comments above each entry for the meaning of the configuration.
```  
# Port number of Cassy master. If not specified port 20051 is used by default.
#scalar.backup.cassandra.server.port=20051

# Port number of JMX. If not specified port 7199 is used by default.
#scalar.backup.cassandra.server.jmx_port=7199

# Username for SSH.  
scalar.backup.cassandra.server.ssh_user=foo

# Location of the private key file for SSH.
scalar.backup.cassandra.server.ssh_private_key_path=/path/to/.ssh/id_rsa

# Path to the bin directory of Cassy in each Cassandra nodes (Cassy assumes all Cassandra nodes install Cassy in the same directory)
scalar.backup.cassandra.server.slave_command_path=/path/to/cassy/build/install/cassy/bin

# URI of a blob store to manage backup files 
scalar.backup.cassandra.server.storage_base_uri=s3://your-bucket

# URL of JDBC for managing some metadata such as backup and restore histories
scalar.backup.cassandra.server.metadata_db_url=jdbc:sqlite:cassy.db

# URL of SRV record. It is used to know and pause Cassandra application nodes to take a cluster-wide snapshot. If you don't use the feature, it can be omitted or the value can be left blank.
scalar.backup.cassandra.server.srv_service_url=_app._tcp.your-service.com
```

### Use

First of all, let's create a metadata database with the following command.
```
$ sqlite3 cassy.db < scripts/db.schema
```

Let's start a Cassy master with the configuration file `cassy-server.properties`.

```
$ build/install/cassy/bin/cassy-server --config ./cassy-server.properties
```

Now you can run backup and restore through gRPC APIs or HTTP/1.1 REST APIs. For gRPC APIs, you can do it easily with [grpcurl](https://github.com/fullstorydev/grpcurl).
Here we will use grpcurl for the sake of simplicity.

#### Register your cluster

You can register your cluster with `RegisterCluster`

```
$ grpcurl -plaintext -d '{"cassandra_host": "192.168.0.2"}' 192.168.0.254:20051 rpc.CassandraBackup.RegisterCluster
```
You need to specify an address of one of Cassandra nodes to register your cluster. All the other cluster information such as cluster name, nodes' IP addresses, keyspaces and the data directory are retrieved by JMX and are saved in the metadata database for later use.

Note that you need to re-register your cluster if you update some of the above cluster information. Backup files with outdated cluster information are invalid (but in the current version nothing stops you from taking a backup, changing your cluster, and trying to perform a restore).

#### List your clusters

You can also view your cluster information that you registered with `ListClusters`.

```
# List all registered clusters.
$ grpcurl -plaintext 192.168.0.254:20051 rpc.CassandraBackup.ListClusters

# Show only the specified cluster.
$ grpcurl -plaintext -d '{"cluster_id": ""}' 192.168.0.254:20051 rpc.CassandraBackup.registerCluster

# List three recently registered clusters.
$ grpcurl -plaintext -d '{"limit": 3}' 192.168.0.254:20051 rpc.CassandraBackup.registerCluster
```

### Take backups

You can take a snapshot with `TakeBackup` with `"backup_type": 2`. It will take a snapshot for each node in the specified cluster and upload the snapshots to the blob store.

```
# It asks each node to clean up old snapshots and take a new snapshot (with flush), and upload the snapshot to the specified location. It also asks each node to remove old incremental backups.
$ grpcurl -plaintext -d '{"cluster_id": "", "backup_type": 2}' 192.168.0.254:20051 rpc.CassandraBackup.TakeBackup 

# You can also specify nodes.
$ grpcurl -plaintext -d '{"cluster_id": "", "target_ips": ["192.168.0.2"], "backup_type": 2}' 192.168.0.254:20051 rpc.CassandraBackup.TakeBackup 
```

When you want to upload incremental backups, please specify `"backup_type": 3` instead. 
You will also need to specify a `snapshot_id` because incremental backups are differences from a previous snapshot.

```
# It asks each node to upload incremental backups to the specified location.
$ grpcurl -plaintext -d '{"cluster_id": "", "snapshot_id": "", "backup_type": 3}' 192.168.0.254:20051 rpc.CassandraBackup.TakeBackup 
```

(NOTE: Cassandra incremental backups are created per memtable flush and it is not controllable from outside of system. So what Cassy does is just upload incremental backups.)

### Take cluster-wide consistent backups

You can take cluster-wide consistent backups with `TakeBackup` with `"backup_type": 1`. 

```
$ grpcurl -plaintext -d '{"cluster_id": "", "backup_type": 1}' 192.168.0.254:20051 rpc.CassandraBackup.TakeBackup 
```

Cassy master asks a DNS to resolve the service URL to get a list of IPs and ports of Cassandra applications, and ask those nodes to stop processing. The node that is asked to stop will finish already received requests and will be in a pause mode afterward and return a response to Cassy master. The master waits for all responses, and takes snapshots of all Cassandra nodes. Then the master unpauses the application nodes and uploads all the snapshots.

Note that applications must implement a pause feature to do this. Cassy currently supports gRPC endpoints for pause. For more detail, please take a look at the source code.

If the cluster-wide backup is taken during a non-busy time, the paused duration will be very short like one second, and it won't drop any requests if applications properly queue requests during pause.


### List backups and their statues

You can view available backups with `ListBackups`.

```
$ grpcurl -plaintext -d '{"limit": 3, "cluster_id": ""}' 192.168.0.254:20051  rpc.CassandraBackup.ListBackups
```

### Restore backups to a node

You can restore a node with `RestoreBackup` with `"restore_type": 2`.
If `target_ips` is omitted, it will restore the specified backups to all the nodes.
Note that there should be no data, commitlogs, hints in a node that is being recovered, and the Cassandra daemon should be stopped.

```
$ grpcurl -plaintext -d '{"snapshot_id": "", "restore_type": 2, "cluster_id": "", "target_ips": ["192.168.0.3]}' 192.168.0.254:20051 rpc.CassandraBackup.RestoreBackup
// Start up Cassandra and do repair
```

It will download the specified snapshot and the incremental backups from the blob store into Cassandra data directory of "192.168.0.3" with a directory named `node-backup` and place the files properly for Cassandra to start up with the files. Then start up Cassandra, and do repair if replica consistency is important.

### Restore backups to a cluster

You can restore a cluster with `RestoreBackup` with `"restore_type": 1`.

```
$ grpcurl -plaintext -d '{"snapshot_id": "", "restore_type": 1, "cluster_id": ""}' 192.168.0.254:20051 rpc.CassandraBackup.RestoreBackup
```

### List restore statues

You can view restore statues with `ListRestoreStatuses`.

```
$ grpcurl -plaintext -d '{"limit": 3, "cluster_id": ""}' 192.168.0.254:20051 rpc.CassandraBackup.ListRestoreStatuses
```


## Future Work

The following features are planned to be implemented in the near future.
* Upload backup files to a specified remote filesystem or other cloud blob stores than AWS S3.
* GUI component to make backup and restore operations even easier.

