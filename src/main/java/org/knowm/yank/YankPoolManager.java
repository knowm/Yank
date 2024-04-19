package org.knowm.yank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a Singleton that provides access to one or more connection pools defined in a
 * Property file. When the client shuts down it should call the release() method to close all open
 * connections and do other clean up.
 *
 * <p>This class should not be directly accessed by client code.
 *
 * @author timmolter
 */
public final class YankPoolManager {

  private final Logger logger = LoggerFactory.getLogger(YankPoolManager.class);

  private final Properties mergedSqlProperties = new Properties();

  /** The singleton instance */
  protected static final YankPoolManager INSTANCE = new YankPoolManager();

  protected static final String DEFAULT_POOL_NAME = "yank-default";

  private final Map<String, HikariDataSource> pools =
      new ConcurrentHashMap<String, HikariDataSource>(2);

  /** A private constructor since this is a Singleton */
  private YankPoolManager() {}

  /**
   * Add properties for a DataSource (connection pool). Yank uses a Hikari DataSource (connection
   * pool) under the hood, so you have to provide the minimal essential properties and the optional
   * properties as defined here: https://github.com/brettwooldridge/HikariCP
   *
   * <p>This convenience method will create a connection pool in the default pool.
   *
   * <p>Note that if you call this method repeatedly, the existing default pool will be first
   * shutdown each time.
   *
   * @param dataSourceProperties
   */
  protected void addDefaultConnectionPool(Properties dataSourceProperties) {

    createPool(DEFAULT_POOL_NAME, dataSourceProperties);
  }

  /**
   * Add properties for a DataSource (connection pool). Yank uses a Hikari DataSource (connection
   * pool) under the hood, so you have to provide the minimal essential properties and the optional
   * properties as defined here: https://github.com/brettwooldridge/HikariCP
   *
   * <p>Note that if you call this method providing a poolName corresponding to an existing
   * connection pool, the existing pool will be first shutdown.
   *
   * @param poolName
   * @param connectionPoolProperties
   */
  protected void addConnectionPool(String poolName, Properties connectionPoolProperties) {

    createPool(poolName, connectionPoolProperties);
  }

  /**
   * Creates a Hikari connection pool and puts it in the pools map.
   *
   * @param poolName
   * @param connectionPoolProperties
   */
  private void createPool(String poolName, Properties connectionPoolProperties) {

    releaseConnectionPool(poolName);

    // DBUtils execute methods require autoCommit to be true.
    connectionPoolProperties.put("autoCommit", true);

    HikariConfig config = new HikariConfig(connectionPoolProperties);
    config.setPoolName(poolName);
    HikariDataSource ds = new HikariDataSource(config);
    pools.put(poolName, ds);
    logger.info("Initialized pool '{}'", poolName);
  }

  /** Closes the default connection pool */
  protected synchronized void releaseDefaultConnectionPool() {

    releaseConnectionPool(DEFAULT_POOL_NAME);
  }

  /**
   * Closes a connection pool
   *
   * @param poolName
   */
  protected synchronized void releaseConnectionPool(String poolName) {

    HikariDataSource pool = pools.get(poolName);

    if (pool != null) {
      logger.info("Releasing pool: {}...", pool.getPoolName());
      pool.close();
    }
  }

  /** Closes all connection pools */
  protected synchronized void releaseAllConnectionPools() {

    for (HikariDataSource pool : pools.values()) {

      if (pool != null) {
        logger.info("Releasing pool: {}...", pool.getPoolName());
        pool.close();
      }
    }
  }

  /**
   * Get a connection pool
   *
   * @return a connection pool by name
   */
  protected HikariDataSource getConnectionPool(String poolName) {

    return pools.get(poolName);
  }

  /**
   * Get the default connection pool
   *
   * @return the default connection pool
   */
  protected HikariDataSource getDefaultConnectionPool() {

    return getConnectionPool(DEFAULT_POOL_NAME);
  }

  protected void addSQLStatements(Properties sqlProperties) {

    this.mergedSqlProperties.putAll(sqlProperties);
  }

  /**
   * @return the mergedSqlProperties
   */
  protected Properties getMergedSqlProperties() {

    return mergedSqlProperties;
  }
}
