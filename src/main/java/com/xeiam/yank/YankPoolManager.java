/**
 * Copyright 2011 - 2014 Xeiam LLC.
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private Properties sqlProperties;

  /** The singleton instance */
  public static final YankPoolManager INSTANCE = new YankPoolManager();

  /**
   * A private constructor since this is a Singleton
   */
  private YankPoolManager() {

  }

  /**
   * Init method with both DB properties and SQL properties file
   *
   * @param dbProperties
   * @param sqlProperties
   */
  public void init(Properties dbProperties, Properties sqlProperties) {

    logger.info("Initializing DBConnectionManager...");

    if (dbProperties == null) {
      logger.error("DB PROPS NULL!!!");
    }
    if (sqlProperties == null) {
      logger.warn("SQL PROPS NULL!!!");
    }
    this.sqlProperties = sqlProperties;

    createPools(dbProperties);
  }

  /**
   * Init method without a MYSQL_SQL.properties file
   *
   * @param dbProperties The connection pool properties
   */
  public void init(Properties dbProperties) {

    logger.info("Initializing DBConnectionManager...");

    if (dbProperties == null) {
      logger.error("DB PROPS NULL!!!");
    }

    this.sqlProperties = new Properties(); // create an empty properties file

    createPools(dbProperties);
  }

  /**
   * Creates instances of DBConnectionPool objects based on the properties. A ConnectionPool can be defined with the following properties:
   *
   * <pre>
   * poolname.url         The JDBC URL for the database
   * poolname.user        A database user (optional)
   * poolname.password    A database user password (if user specified)
   * poolname.maxconn     The maximal number of connections (optional)
   * </pre>
   *
   * @param dbProperties The connection pool properties
   */
  private void createPools(Properties dbProperties) {

    Enumeration propNames = dbProperties.propertyNames();
    while (propNames.hasMoreElements()) {
      String name = (String) propNames.nextElement();
      if (name.endsWith(".url")) {
        String poolName = name.substring(0, name.lastIndexOf('.'));
        String url = dbProperties.getProperty(poolName + ".url").trim();
        if (url == null) {
          logger.warn("No URL specified for {}", poolName);
          continue;
        }
        String user = dbProperties.getProperty(poolName + ".user").trim();
        String password = dbProperties.getProperty(poolName + ".password").trim();

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);

        // make this optional, per docs
        String maxconn = dbProperties.getProperty(poolName + ".maxconn");
        if (maxconn != null) {
          maxconn = maxconn.trim();
          try {
            ds.setMaximumPoolSize(Integer.valueOf(maxconn));
          } catch (NumberFormatException e) {
            logger.warn("Invalid maxconn value {} for {} pool's default will be used", maxconn, poolName);
          }
        }

        pools.put(poolName, ds);
        logger.info("Initialized pool '{}'", poolName);
      }
    }
  }

  /**
   * Get the DataSource corresponding to the provided poolName
   *
   * @param poolName
   * @return
   */
  public DataSource getDataSource(String poolName) {

    return pools.get(poolName);
  }

  /**
   * Closes all open connections
   */
  public synchronized void release() {

    logger.info("Releasing DBConnectionManager...");

    Set<String> allPools = pools.keySet();

    for (Iterator<String> iterator = allPools.iterator(); iterator.hasNext();) {

      String poolName = iterator.next();
      logger.debug("Releasing pool: {}...", poolName);

      pools.get(poolName).shutdown();
    }

  }

  /**
   * @return the sqlProperties
   */
  public Properties getSqlProperties() {

    return sqlProperties;
  }

}
