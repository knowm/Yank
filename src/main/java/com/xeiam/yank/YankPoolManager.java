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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * This class is a Singleton that provides access to one connection pool defined in a Property file. When the client shuts down it should call the
 * release() method to close all open connections and do other clean up.
 * <p>
 * This class should not be directly accessed by client code.
 *
 * @author timmolter
 */
public final class YankPoolManager {

  private final Logger logger = LoggerFactory.getLogger(YankPoolManager.class);

  private HikariDataSource hikariDataSource;

  private final Properties mergedSqlProperties = new Properties();

  /** The singleton instance */
  protected static final YankPoolManager INSTANCE = new YankPoolManager();

  /**
   * A private constructor since this is a Singleton
   */
  private YankPoolManager() {

  }

  /**
   * Add properties for a DataSource (connection pool). Yank uses a Hikari DataSource (connection pool) under the hood, so you have to provide the
   * minimal essential properties and the optional properties as defined here: https://github.com/brettwooldridge/HikariCP
   *
   * @param dataSourceProperties
   */
  protected void setupDataSource(Properties dataSourceProperties) {

    createPool(dataSourceProperties);
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
  private void createPool(Properties connectionPoolProperties) {

    releaseDataSource();

    // DBUtils execute methods require autoCommit to be true.
    connectionPoolProperties.put("autoCommit", true);

    HikariConfig config = new HikariConfig(connectionPoolProperties);
    HikariDataSource hikariDataSource = new HikariDataSource(config);
    this.hikariDataSource = hikariDataSource;
    logger.info("Initialized pool '{}'", hikariDataSource.getPoolName());
  }

  /**
   * Closes connection pool
   */
  protected synchronized void releaseDataSource() {

    if (this.hikariDataSource != null) {
      logger.debug("Releasing pool: {}...", this.hikariDataSource.getPoolName());
      this.hikariDataSource.shutdown();
    }
  }

  /**
   * Get the DataSource
   *
   * @return
   */
  protected HikariDataSource getDataSource() {

    return this.hikariDataSource;
  }

  /**
   * @return the mergedSqlProperties
   */
  protected Properties getMergedSqlProperties() {

    return mergedSqlProperties;
  }

}
