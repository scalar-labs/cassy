# Cassy: A simple and integrated backup tool for Apache Cassandra

[![CircleCI](https://circleci.com/gh/scalar-labs/cassy.svg?style=svg&circle-token=4f293f3061b353a7f5bd8c7d9544bae8817449af)](https://circleci.com/gh/scalar-labs/cassy)

Cassy is a simple and integrated backup tool for Apache Cassandra.

You can do the following things with Cassy from an easy to use gRPC APIs or HTTP/1.1 REST APIs:
* Take snapshots, and upload snapshots and incremental backups to a blob store or filesystem of your choice from your cluster (AWS S3 is the only supported blob store at the current version, but other blob stores or filesystems will be supported shortly)
* Download backups, and restore a node or a cluster from the backups
* Manage statuses and histories of backups and restore 

You can NOT do the followings things with the current version of Cassy:
* Backup commitlogs 
* Select keyspaces to take/restore backups 
* Operate easily with GUI

## Background

The existing Cassandra backup features such as snapshot and incremental backups are great building blocks for doing backup and restore for Cassandra data. However, they are not necessarily easy to use because they are not fully integrated. Moreover, the current backup feature and the existing backup tools are problematic when used with [Scalar DB](https://github.com/scalar-labs/scalardb/) transactions that update multiple records in a transactional (atomic) way as they do not handle transactional consistency of multiple records. 
In order to overcome these problems we created a new backup tool, which makes it easy to do backup and restore operations, and makes it possible to do cluster-wide transactionally consistent backups.

## System Overview

<p align="center">
<img src="https://github.com/scalar-labs/cassy/raw/master/docs/images/cassy.png" width="640" />
</p>

## Docs
* [Getting started](docs/getting-started.md)

## Contributing 
This library is mainly maintained by the Scalar Engineering Team, but of course we appreciate any help.

* For asking questions, finding answers and helping other users, please go to [cassy-user](https://groups.google.com/forum/#!forum/cassy-user).
* For filing bugs, suggesting improvements, or requesting new features, help us out by opening an issue.

## License
Cassy is dual-licensed under both the Apache 2.0 License (found in the LICENSE file in the root directory) and a commercial license. You may select, at your option, one of the above-listed licenses. Regarding the commercial license, please [contact us](https://scalar-labs.com/contact_us/) for more information. 

