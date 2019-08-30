package com.scalar.cassy.placer;

import com.scalar.cassy.config.RestoreConfig;
import com.scalar.cassy.exception.PlacementException;
import com.scalar.cassy.transferer.BackupPath;
import com.scalar.cassy.traverser.IncrementalBackupTraverser;
import com.scalar.cassy.traverser.SnapshotTraverser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Placer {

  public void place(RestoreConfig config) {
    String key = BackupPath.create(config, "");
    Path fromDir = Paths.get(config.getDataDir(), key);
    Path toDir = Paths.get(config.getDataDir());

    place(
        new SnapshotTraverser(fromDir, config.getSnapshotId()).traverse(config.getKeyspace()),
        fromDir,
        toDir,
        SnapshotTraverser.DIR_TO_FILE_DISTANCE);

    if (config.isSnapshotOnly()) {
      return;
    }

    place(
        new IncrementalBackupTraverser(fromDir).traverse(config.getKeyspace()),
        fromDir,
        toDir,
        IncrementalBackupTraverser.DIR_TO_FILE_DISTANCE);
  }

  private void place(List<Path> files, Path fromDir, Path toDir, int dirToFileDistance) {
    files.forEach(
        f -> {
          try {
            Path from = Paths.get(fromDir.toString(), f.toString());
            Path tmp = Paths.get(toDir.toString(), f.getParent().toString());
            for (int i = 0; i < dirToFileDistance; ++i) {
              tmp = tmp.getParent();
            }
            Path to = Paths.get(tmp.toString(), f.getFileName().toString());
            Files.createDirectories(to.getParent());
            Files.move(from, to);
          } catch (IOException e) {
            throw new PlacementException(e);
          }
        });
  }
}
