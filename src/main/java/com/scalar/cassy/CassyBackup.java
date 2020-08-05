package com.scalar.cassy;

import picocli.CommandLine;

@CommandLine.Command(
    name = "cassy-backup",
    mixinStandardHelpOptions = true,
    description = "calls Cassy's backup command",
    subcommands = {ClusterSnapshot.class, NodeSnapshot.class, NodeIncremental.class})
public class CassyBackup implements Runnable {

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  public static void main(String[] args) {
    System.exit(new CommandLine(new CassyBackup()).execute(args));
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's <code>run</code> method to be called in that separately
   * executing thread.
   *
   * <p>The general contract of the method <code>run</code> is that it may take any action
   * whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    spec.commandLine().usage(System.err);
  }
}
