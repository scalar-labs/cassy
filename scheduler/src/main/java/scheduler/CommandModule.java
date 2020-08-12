package scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CommandModule extends AbstractModule {
  @Provides
  @Singleton
  CassyClient provideCassyClient() {
    return new CassyClient();
  }
}
