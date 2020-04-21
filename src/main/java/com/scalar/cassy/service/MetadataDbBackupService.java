package com.scalar.cassy.service;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.palantir.giraffe.command.Command;
import com.palantir.giraffe.command.CommandResult;
import com.palantir.giraffe.command.Commands;
import com.palantir.giraffe.file.MoreFiles;
import com.scalar.cassy.config.CassyServerConfig;
import com.scalar.cassy.exception.ExecutionException;
import com.scalar.cassy.transferer.FileUploader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import javax.annotation.concurrent.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
public class MetadataDbBackupService {
  private static final Logger logger = LoggerFactory.getLogger(MetadataDbBackupService.class);
  private static final String METADATA_BACKUP_KEY = "metadata-backup";
  // assumes that sqlite3 is in PATH
  private static final String SQLITE_COMMAND = "sqlite3";
  private static final String BACKUP_TMP_DIR = "/tmp/";
  private final CassyServerConfig config;
  private final FileUploader uploader;

  @Inject
  public MetadataDbBackupService(CassyServerConfig config, FileUploader uploader) {
    this.config = config;
    this.uploader = uploader;
  }

  public Future<Void> backup() {
    List<String> urls = Splitter.on(':').splitToList(config.getMetadataDbUrl());
    if (urls.get(1).equalsIgnoreCase("sqlite")) {
      Path dumpFile = sqliteBackup(Paths.get(urls.get(2)));
      return uploader.upload(dumpFile, getKey(dumpFile).toString(), config.getStorageBaseUri());
    } else {
      throw new IllegalArgumentException(
          "metadata backup for " + urls.get(1) + " is not supported.");
    }
  }

  private Path sqliteBackup(Path file) {
    // uses SQLite .dump command
    Path dumpFile = Paths.get(BACKUP_TMP_DIR + file.getFileName() + ".dump");
    Command removeDumpFile = Commands.get("rm", "-f", dumpFile);
    Command sqliteDump = Commands.get(SQLITE_COMMAND, file, ".dump");
    try {
      Commands.execute(removeDumpFile);
      CommandResult result = Commands.execute(sqliteDump);
      MoreFiles.write(dumpFile, result.getStdOut(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new ExecutionException(e);
    }
    return dumpFile;
  }

  private Path getKey(Path file) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    String datetime = simpleDateFormat.format(new Date());
    return Paths.get(METADATA_BACKUP_KEY, datetime, file.getFileName().toString());
  }
}
