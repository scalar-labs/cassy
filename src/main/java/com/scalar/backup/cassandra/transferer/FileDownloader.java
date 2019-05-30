package com.scalar.backup.cassandra.transferer;

import com.scalar.backup.cassandra.config.RestoreConfig;

public interface FileDownloader {

  void download(RestoreConfig config);
}
