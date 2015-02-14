/**
 * Copyright 2011 - 2015 Xeiam LLC.
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
package com.xeiam.yank;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * This class is a Singleton that provides access to one or many connection pools defined in a Property file. A client gets access to the single
 * instance and can then check-out and check-in connections from a pool. When the client shuts down it should call the release() method to close all
 * open connections and do other clean up.
 *
 * @author timmolter
 */
public final class YankPoolManager {

  private final Logger logger = LoggerFactory.getLogger(YankPoolManager.class);

  private final Map<String, HikariDataSource> pools = new HashMap<String, HikariDataSource>();

  private final Properties mergedSqlProperties = new Properties();

  /** The singleton instance */
  protected static final YankPoolManager INSTANCE = new YankPoolManager();

  /**
   * A private constructor since this is a Singleton
   */
  private YankPoolManager() {

  }

  /**
   * Add properties for a connection pool. Yank uses a Hikari connection pool under the hood, so you have to provide the minimal essential properties
   * and the optional properties as defined here: https://github.com/brettwooldridge/HikariCP
   *
   * @param connectionPoolProperties
   */
  protected void addConnectionPool(String poolName, Properties connectionPoolProperties) {

    createPool(poolName, connectionPoolProperties);
  }

  protected void addSQLStatements(Properties sqlProperties) {

    this.mergedSqlProperties.putAll(sqlProperties);
  }

  /**
   * Creates a Hikari connection pool and puts it in the pools map.
   *
   * @param poolName
   * @param connectionPoolProperties
   */
  private void createPool(String poolName, Properties connectionPoolProperties) {

    // DBUtils execute methods require autoCommit to be true.
    connectionPoolProperties.put("autoCommit", true);

    HikariConfig config = new HikariConfig(connectionPoolProperties);
    config.setPoolName(poolName);
    HikariDataSource ds = new HikariDataSource(config);
    pools.put(poolName, ds);
    logger.info("Initialized pool '{}'", poolName);
  }

  /**
   * Closes all connection pools
   */
  protected synchronized void release() {

    logger.info("Releasing Yank connection pools...");

    Set<String> allPools = pools.keySet();

    for (Iterator<String> iterator = allPools.iterator(); iterator.hasNext();) {

      String poolName = iterator.next();
      logger.debug("Releasing pool: {}...", poolName);

      pools.get(poolName).shutdown();
    }
  }

  /**
   * Get the DataSource corresponding to the provided poolName
   *
   * @param poolName
   * @return
   */
  protected DataSource getDataSource(String poolName) {

    return pools.get(poolName);
  }

  /**
   * @return the mergedSqlProperties
   */
  protected Properties getMergedSqlProperties() {

    return mergedSqlProperties;
  }

}
