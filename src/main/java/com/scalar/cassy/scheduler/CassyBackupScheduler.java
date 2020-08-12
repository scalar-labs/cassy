package com.scalar.cassy.scheduler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import picocli.CommandLine;

@CommandLine.Command(
    name = "cassy-schedule",
    mixinStandardHelpOptions = true,
    description = "calls Cassy's backup command")
public class CassyBackupScheduler implements Runnable {
  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  @CommandLine.Option(names = "-t", scope = CommandLine.ScopeType.INHERIT) // option is shared with subcommands
  int timeout;

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new CommandModule());
    CommandLine commandLine = new CommandLine(new CassyBackupScheduler())
        .addSubcommand(injector.getInstance(ClusterSnapshot.class))
        .addSubcommand(injector.getInstance(NodeIncremental.class))
        .addSubcommand(injector.getInstance(NodeSnapshot.class));
    System.exit(commandLine.execute(args));
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
