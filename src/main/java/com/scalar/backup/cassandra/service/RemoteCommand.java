package com.scalar.backup.cassandra.service;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.Path;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class RemoteCommand {
  private final String ip;
  private final String username;
  private final Path privateKeyFile;
  private final String command;
  private final List<String> arguments;
  private final String name;

  private RemoteCommand(Builder builder) {
    this.ip = builder.ip;
    this.username = builder.username;
    this.privateKeyFile = builder.privateKeyFile;
    this.command = builder.command;
    this.arguments = builder.arguments;
    this.name = builder.name;
  }

  public String getIp() {
    return ip;
  }

  public String getUsername() {
    return username;
  }

  public Path getPrivateKeyFile() {
    return privateKeyFile;
  }

  public String getCommand() {
    return command;
  }

  public List<String> getArguments() {
    return arguments;
  }

  public String getName() {
    return name;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String ip;
    private String username;
    private Path privateKeyFile;
    private String command;
    private List<String> arguments;
    private String name;

    public Builder ip(String ip) {
      checkArgument(ip != null);
      this.ip = ip;
      return this;
    }

    public Builder username(String username) {
      checkArgument(username != null);
      this.username = username;
      return this;
    }

    public Builder privateKeyFile(Path privateKeyFile) {
      checkArgument(privateKeyFile != null);
      this.privateKeyFile = privateKeyFile;
      return this;
    }

    public Builder command(String command) {
      checkArgument(command != null);
      this.command = command;
      return this;
    }

    public Builder arguments(List<String> arguments) {
      checkArgument(arguments != null);
      this.arguments = arguments;
      return this;
    }

    public Builder name(String name) {
      checkArgument(name != null);
      this.name = name;
      return this;
    }

    public RemoteCommand build() {
      if (ip == null
          || username == null
          || privateKeyFile == null
          || command == null
          || arguments == null
          || name == null) {
        throw new IllegalArgumentException("Required fields are not given.");
      }
      return new RemoteCommand(this);
    }
  }
}
