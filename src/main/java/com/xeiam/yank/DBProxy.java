/**
 * Copyright 2011 Xeiam LLC.
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper for DBUtils' QueryRunner's methods: update, query, and batch. Connections are retrieved from the connection pool in DBConnectionManager.
 * 
 * @author timmolter
 */
public class DBProxy {

  private static final DBConnectionManager mDBConnectionManager = DBConnectionManager.INSTANCE;

  /** slf4J logger wrapper */
  static Logger logger = LoggerFactory.getLogger(DBProxy.class);

  /**
   * Prevent class instantiation.
   */
  private DBProxy() {

  }

  // ////// INSERT, UPDATE, DELETE, or UPSERT //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL statement with params. Returns the number of rows affected
   * 
   * @param sqlKey
   * @param params
   * @return int - -1 means something went wrong
   * @throws Exception
   */
  public static int executeIUDSQLKey(String poolName, String sqlKey, Object[] params) {

    String sql = mDBConnectionManager.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      logger.warn("NO SQL statement found with key: '" + sqlKey + "' in SQL properties file");

      return -1;
    }
    return executeIUDSQL(poolName, sql, params);
  }

  /**
   * Execute a UID - Update, Insert, Delete statement using an SQL String
   * 
   * @param poolName
   * @param sql
   * @param params
   * @return
   */
  public static int executeIUDSQL(String poolName, String sql, Object[] params) {

    int returnInt = -1;

    Connection con = null;

    try {
      con = mDBConnectionManager.getConnection(poolName);

      if (con == null) {
        return returnInt;
      }

      con.setAutoCommit(false);

      returnInt = new QueryRunner().update(con, sql, params);

      con.commit();

    } catch (Exception e) {
      if (con != null) {
        try {
          con.rollback();
        } catch (SQLException e1) {
          logger.error("Exception caught while rolling back transaction", e1);
        }
      }

      logger.error("Error in SQL query!!!", e);
      // e.printStackTrace();
    } finally {
      mDBConnectionManager.freeConnection(poolName, con);
    }

    return returnInt;

  }

  // ////// BEAN QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Convenience method to return just one Bean given a SQL Key
   * 
   * @param poolName
   * @param sqlKey
   * @param params
   * @param beanClass
   * @return Bean - null if returned list is not equal to one
   */
  public static Object querySingleBeanSQLKey(String poolName, String sqlKey, Object[] params, Class<? extends Object> beanClass) {

    List<? extends Object> list = queryBeanListSQLKey(poolName, sqlKey, params, beanClass);

    if (list != null && list.size() == 1) {
      return list.get(0);
    }

    return null;
  }

  /**
   * Convenience method to return just one Bean given an SQL statement
   * 
   * @param poolName
   * @param sql
   * @param params
   * @param beanClass
   * @return Bean - null if returned list is not equal to one
   */
  public static Object querySingleBeanSQL(String poolName, String sql, Object[] params, Class<? extends Object> beanClass) {

    List<? extends Object> list = queryBeanListSQL(poolName, sql, params, beanClass);

    if (list != null && list.size() == 1) {
      return list.get(0);
    }

    return null;
  }

  /**
   * Convenience method to return a List of Beans given an SQL Key with params
   * 
   * @param poolName
   * @param sqlKey
   * @param params
   * @param beanClass
   * @return
   */
  public static List<? extends Object> queryBeanListSQLKey(String poolName, String sqlKey, Object[] params, Class<? extends Object> beanClass) {

    List<Object> returnList = null;

    String sql = mDBConnectionManager.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      logger.warn("NO SQL statement found with key: '" + sqlKey + "' in SQL properties file");
      return returnList;
    }

    return queryBeanListSQL(poolName, sql, params, beanClass);
  }

  /**
   * Returns a List of Beans given an SQL Statement with params
   * 
   * @param poolName
   * @param sql
   * @param params
   * @param beanClass
   * @return
   */
  @SuppressWarnings("unchecked")
  public static List<? extends Object> queryBeanListSQL(String poolName, String sql, Object[] params, Class<? extends Object> beanClass) {

    // logger.debug(sql);

    List<Object> returnList = null;

    Connection con = null;

    try {
      con = mDBConnectionManager.getConnection(poolName);

      if (con == null) {
        logger.warn("Connection was null! Poolname = " + poolName);
        return returnList;
      }

      con.setAutoCommit(false);

      ResultSetHandler rsh = new BeanListHandler(beanClass);

      returnList = (List<Object>) new QueryRunner().query(con, sql, rsh, params);

      con.commit();

    } catch (Exception e) {
      logger.error("ERROR QUERYING!!!", e);
      try {
        con.rollback();
      } catch (SQLException e2) {
        logger.error("Exception caught while rolling back transaction", e2);
      }
    } finally {
      mDBConnectionManager.freeConnection(poolName, con);
    }

    return returnList;
  }

  // ////// OBJECT QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns a List of Objects given an SQL Key with params
   * 
   * @param poolName
   * @param sqlKey
   * @param params
   * @return
   */
  public static List<Object[]> queryObjectListSQLKey(String poolName, String sqlKey, Object[] params) {

    List<Object[]> returnList = null;

    String sql = mDBConnectionManager.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      logger.warn("NO SQL statement found with key: '" + sqlKey + "' in SQL properties file");
      return returnList;
    }

    return queryObjectListSQL(poolName, sql, params);
  }

  /**
   * Returns a List of Objects given an SQL String with params
   * 
   * @param poolName
   * @param sql
   * @param params
   * @return
   */
  @SuppressWarnings("unchecked")
  public static List<Object[]> queryObjectListSQL(String poolName, String sql, Object[] params) {

    List<Object[]> returnList = null;

    Connection con = null;

    try {
      con = mDBConnectionManager.getConnection(poolName);

      if (con == null) {
        return returnList;
      }

      con.setAutoCommit(false);

      ResultSetHandler rsh = new ArrayListHandler();
      returnList = (List<Object[]>) new QueryRunner().query(con, sql, rsh, params);

      con.commit();
    } catch (Exception e) {
      try {
        con.rollback();
      } catch (SQLException e1) {
        logger.error("Exception caught while rolling back transaction", e1);
      }
    } finally {
      mDBConnectionManager.freeConnection(poolName, con);
    }

    return returnList;
  }

  // ////// BATCH //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * @param poolName
   * @param sqlKey
   * @param params
   * @return
   */
  public static int[] executeBatchIUDSQLKey(String poolName, String sqlKey, Object[][] params) {

    String sql = mDBConnectionManager.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      logger.warn("NO SQL statement found with key: '" + sqlKey + "' in SQL properties file");
    }

    return executeBatchIUDSQL(poolName, sql, params);
  }

  /**
   * @param poolName
   * @param sql
   * @param params
   * @return
   */
  public static int[] executeBatchIUDSQL(String poolName, String sql, Object[][] params) {

    int[] returnIntArray = null;

    Connection con = null;

    try {
      con = mDBConnectionManager.getConnection(poolName);

      if (con == null) {
        return returnIntArray;
      }

      con.setAutoCommit(false);

      returnIntArray = new QueryRunner().batch(con, sql, params);

      con.commit();
    } catch (Exception e) {
      try {
        con.rollback();
      } catch (SQLException e1) {
        logger.error("Exception caught while rolling back transaction", e1);
        e1.printStackTrace();
      }
    } finally {
      mDBConnectionManager.freeConnection(poolName, con);
    }

    return returnIntArray;
  }

}
