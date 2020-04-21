package com.scalar.cassy.transferer;

import com.google.common.annotations.VisibleForTesting;
import com.palantir.giraffe.file.MoreFiles;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FileSystemTransfererBase {
  /**
   * Wrapper used to copy a folder
   *
   * @param sourceDir
   * @param destDir
   * @throws IOException
   */
  @VisibleForTesting
  void copyFolder(Path sourceDir, Path destDir) throws IOException {
    MoreFiles.copyRecursive(sourceDir, destDir);
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
