package com.scalar.cassy.transferer;

import com.scalar.cassy.config.RestoreConfig;

public interface FileDownloader extends AutoCloseable {

  void download(RestoreConfig config);
}
