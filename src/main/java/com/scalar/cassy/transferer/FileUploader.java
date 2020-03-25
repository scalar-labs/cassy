package com.scalar.cassy.transferer;

import com.scalar.cassy.config.BackupConfig;
import java.nio.file.Path;
import java.util.List;

public interface FileUploader extends AutoCloseable {

  void upload(List<Path> files, BackupConfig config);
}
