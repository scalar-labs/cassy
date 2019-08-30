# Cassy: A simple and integrated backup tool for Apache Cassandra

Cassy is a simple and integrated backup tool for Apache Cassandra.

You can do the following things with Cassy from an easy to use gRPC APIs or HTTP/1.1 REST APIs:
* Take snapshots, and upload snapshots and incremental backups to a blob store or filesystem of your choice from your cluster
  * AWS S3 is the only supported blob store at the current version, but other blob stores or filesystems will be supported shortly
* Download backups, and restore a node or a cluster from the backups
* Manage backups and restore histories

You can NOT do the followings things with the current version of Cassy:
* Backup commitlogs 
* Select keyspaces to take/restore backups 
* Operate easily with GUI

## Docs
* [Getting started](docs/getting-started.md)

## Contributing 
This library is mainly maintained by the Scalar Engineering Team, but of course we appreciate any help.

* For asking questions, finding answers and helping other users, please go to [cassy-user](https://groups.google.com/forum/#!forum/cassy-user).
* For filing bugs, suggesting improvements, or requesting new features, help us out by opening an issue.

## License
Cassy is dual-licensed under both the Apache 2.0 License (found in the LICENSE file in the root directory) and a commercial license. You may select, at your option, one of the above-listed licenses. Regarding the commercial license, please [contact us](https://scalar-labs.com/contact_us/) for more information. 

