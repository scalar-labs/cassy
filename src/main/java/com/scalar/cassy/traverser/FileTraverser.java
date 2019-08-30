package com.scalar.cassy.traverser;

import com.scalar.cassy.exception.FileTraversalException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public abstract class FileTraverser {
  private final Path dataDir;

  public FileTraverser(Path dataDir) {
    this.dataDir = dataDir;
  }

  public abstract List<Path> traverse(String keyspace);

  public abstract List<Path> traverse(String keyspace, @Nullable String table);

  protected List<Path> traverse(
      String keyspace, @Nullable String table, Function<Stream<Path>, List<Path>> traverser) {
    Path keyspacePath = Paths.get(dataDir.toString(), keyspace);

    List<Path> tablePaths = traverseTable(keyspacePath, table);

    List<Path> filePaths = new ArrayList<>();
    tablePaths.forEach(
        t -> {
          try (Stream<Path> walk = Files.walk(t, 1)) {
            filePaths.addAll(traverser.apply(walk));
          } catch (IOException e) {
            throw new FileTraversalException(e.getMessage(), e);
          }
        });

    return filePaths.stream().map(f -> dataDir.relativize(f)).collect(Collectors.toList());
  }

  private List<Path> traverseTable(Path keyspace, String table) {
    List<Path> tables = new ArrayList<>();
    try (Stream<Path> walk = Files.walk(keyspace, 1)) {
      tables.addAll(filterTable(walk.filter(dir -> !dir.endsWith(keyspace)), table));
    } catch (IOException e) {
      throw new FileTraversalException(e.getMessage(), e);
    }
    return tables;
  }

  private List<Path> filterTable(Stream<Path> stream, String table) {
    return stream
        .filter(
            dir -> {
              if (table != null) {
                return dir.getFileName().toString().startsWith(table);
              }
              return true;
            })
        .collect(Collectors.toList());
  }

  protected List<Path> traverseFile(Stream<Path> stream, String directory, int depth) {
    List<Path> paths = new ArrayList<>();
    stream
        .filter(dir -> dir.endsWith(directory))
        .forEach(
            dir -> {
              try (Stream<Path> walk = Files.walk(dir, depth)) {
                paths.addAll(walk.filter(Files::isRegularFile).collect(Collectors.toList()));
              } catch (IOException e) {
                throw new FileTraversalException(e.getMessage(), e);
              }
            });
    return paths;
  }
}
