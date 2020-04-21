package com.scalar.cassy.transferer;

import com.scalar.cassy.config.BackupConfig;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

public interface FileUploader extends AutoCloseable {

  Future<Void> upload(Path file, String key, String storageBaseUri);

  void upload(List<Path> files, BackupConfig config);
}
