package com.scalar.backup.cassandra.transferer;

import com.scalar.backup.cassandra.config.RestoreConfig;

public interface FileDownloader extends AutoCloseable {

  void download(RestoreConfig config);
}
