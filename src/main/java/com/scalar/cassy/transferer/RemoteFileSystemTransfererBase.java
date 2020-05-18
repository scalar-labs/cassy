package com.scalar.cassy.transferer;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.cassy.util.RemoteFileSystemConnection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class RemoteFileSystemTransfererBase {
  final RemoteFileSystemConnection hostConnection;

  RemoteFileSystemTransfererBase(RemoteFileSystemConnection hostConnection) {
    this.hostConnection = hostConnection;
  }

  /**
   * Wrapper used to create directories. It was created to facilitate testing.
   *
   * @param dir
   * @throws IOException
   */
  @VisibleForTesting
  void createDirectories(Path dir) throws IOException {
    Files.createDirectories(dir);
  }
}
