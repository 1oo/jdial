package al.jdi.dao.beans;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;

class DefaultSessionHandler implements SessionHandler {

  private final Map<String, Configuration> configurations = new HashMap<String, Configuration>();
  private final Map<String, SessionFactory> sessionFactories =
      new HashMap<String, SessionFactory>();

  DefaultSessionHandler() {
    Configuration configuration = new Configuration().configure();
    configurations.put(null, configuration);
    sessionFactories.put(
        null,
        configurations.get(null).buildSessionFactory(
            new StandardServiceRegistryBuilder().applySettings(configuration.getProperties())
                .build()));
  }

  @Override
  public synchronized void close() {
    configurations.clear();
    for (SessionFactory sessionFactory : sessionFactories.values())
      sessionFactory.close();
    sessionFactories.clear();
  }

  @Override
  public Configuration getConfiguration() {
    return getConfiguration(null);
  }

  @Override
  public synchronized Configuration getConfiguration(String identifier) {
    if (!configurations.containsKey(identifier))
      configurations.put(identifier, new Configuration().configure(identifier));
    return configurations.get(identifier);
  }

  @Override
  public Session getSession() {
    return getSession(null);
  }

  @Override
  public synchronized Session getSession(String identifier) {
    if (!sessionFactories.containsKey(identifier)) {
      sessionFactories.put(
          identifier,
          getConfiguration(identifier).buildSessionFactory(
              new StandardServiceRegistryBuilder().applySettings(
                  getConfiguration(identifier).getProperties()).build()));
    }
    return sessionFactories.get(identifier).openSession();
  }

  @Override
  public Statistics getStatistics() {
    return getStatistics(null);
  }

  @Override
  public Statistics getStatistics(String identifier) {
    return sessionFactories.get(identifier).getStatistics();
  }
}