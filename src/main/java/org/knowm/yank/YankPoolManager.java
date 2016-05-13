/**
 * Copyright 2015-2016 Knowm Inc. (http://knowm.org) and contributors.
 * Copyright 2011-2015 Xeiam LLC (http://xeiam.com) and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.knowm.yank;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * This class is a Singleton that provides access to one or more connection pools defined in a Property file. When the client shuts down it should
 * call the release() method to close all open connections and do other clean up.
 * <p>
 * This class should not be directly accessed by client code.
 *
 * @author timmolter
 */
public final class YankPoolManager {

  private final Logger logger = LoggerFactory.getLogger(YankPoolManager.class);

  private final Properties mergedSqlProperties = new Properties();

  /** The singleton instance */
  protected static final YankPoolManager INSTANCE = new YankPoolManager();

  protected static final String DEFAULT_POOL_NAME = "yank-default";

  private final Map<String, HikariDataSource> pools = new ConcurrentHashMap<String, HikariDataSource>(2);

  /**
   * A private constructor since this is a Singleton
   */
  private YankPoolManager() {

  }

  /**
   * Add properties for a DataSource (connection pool). Yank uses a Hikari DataSource (connection pool) under the hood, so you have to provide the
   * minimal essential properties and the optional properties as defined here: https://github.com/brettwooldridge/HikariCP
   * <p>
   * This convenience method will create a connection pool in the default pool.
   * <p>
   * Note that if you call this method repeatedly, the existing default pool will be first shutdown each time.
   *
   * @param dataSourceProperties
   */
  protected void addDefaultConnectionPool(Properties dataSourceProperties) {

    createPool(DEFAULT_POOL_NAME, dataSourceProperties);
  }

  /**
   * Add properties for a DataSource (connection pool). Yank uses a Hikari DataSource (connection pool) under the hood, so you have to provide the
   * minimal essential properties and the optional properties as defined here: https://github.com/brettwooldridge/HikariCP
   * <p>
   * Note that if you call this method providing a poolName corresponding to an existing connection pool, the existing pool will be first shutdown.
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

  /**
   * Closes the default connection pool
   */
  @Deprecated
  protected synchronized void releaseDataSource() {

    releaseDefaultConnectionPool();
  }

  /**
   * Closes the default connection pool
   */
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

  /**
   * Closes a connection pool
   *
   * @param poolName
   */
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
   * @return
   */
  protected HikariDataSource getConnectionPool(String poolName) {

    return pools.get(poolName);
  }

  /**
   * Get the default connection pool
   *
   * @return
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
