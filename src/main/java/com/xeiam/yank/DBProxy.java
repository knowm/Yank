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

import com.xeiam.yank.exceptions.ConnectionPoolNotFoundException;
import com.xeiam.yank.exceptions.SQLStatementNotFoundException;
import com.xeiam.yank.exceptions.SingleObjectQueryException;

/**
 * A wrapper for DBUtils' QueryRunner's methods: update, query, and batch. Connections are retrieved from the connection pool in DBConnectionManager.
 * 
 * @author timmolter
 */
public class DBProxy {

  private static final DBConnectionManager DB_CONNECTION_MANAGER = DBConnectionManager.INSTANCE;

  /** slf4J logger wrapper */
  private static Logger logger = LoggerFactory.getLogger(DBProxy.class);

  /**
   * Prevent class instantiation with private constructor
   */
  private DBProxy() {

  }

  // ////// INSERT, UPDATE, DELETE, or UPSERT //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method. Returns the number of
   * rows affected.
   * 
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @return The number of rows affected
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static int executeSQLKey(String poolName, String sqlKey, Object[] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return executeSQL(poolName, sql, params);
    }
  }

  /**
   * Executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL prepared statement. Returns the number of rows affected.
   * 
   * @param poolName The connection pool name
   * @param sql The query to execute
   * @param params The replacement parameters
   * @return The number of rows affected
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static int executeSQL(String poolName, String sql, Object[] params) {

    int returnInt = 0;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionPoolNotFoundException(poolName);
      }

      con.setAutoCommit(false);

      returnInt = new QueryRunner().update(con, sql, params);

      con.commit();

    } catch (SQLException e) {
      logger.error("Error in SQL query!!!", e);
      if (con != null) {
        try {
          con.rollback();
        } catch (SQLException e1) {
          logger.error("Exception caught while rolling back transaction", e1);
        }
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(poolName, con);
    }

    return returnInt;

  }

  // ////// Object List QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return just one Object given a SQL Key using an SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method.
   * 
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @param type The Class of the desired return Object matching the table
   * @return The Object
   * @throws SingleObjectQueryException if more than one Object was returned for the given SQL query
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static Object querySingleObjectSQLKey(String poolName, String sqlKey, Object[] params, Class<? extends Object> type) {

    List<? extends Object> list = queryObjectListSQLKey(poolName, sqlKey, params, type);

    if (list != null && list.size() == 1) {
      return list.get(0);
    } else {
      throw new SingleObjectQueryException();
    }

  }

  /**
   * Return just one Object given an SQL statement
   * 
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params The replacement parameters
   * @param type The Class of the desired return Object matching the table
   * @return The Object
   * @throws SingleObjectQueryException if more than one Object was returned for the given SQL query
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static Object querySingleObjectSQL(String poolName, String sql, Object[] params, Class<? extends Object> type) {

    List<? extends Object> list = queryObjectListSQL(poolName, sql, params, type);

    if (list != null && list.size() == 1) {
      return list.get(0);
    } else {
      throw new SingleObjectQueryException();
    }
  }

  /**
   * Return a List of Objects given a SQL Key using an SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method.
   * 
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @param type The Class of the desired return Objects matching the table
   * @return The List of Objects
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static List<? extends Object> queryObjectListSQLKey(String poolName, String sqlKey, Object[] params, Class<? extends Object> type) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return queryObjectListSQL(poolName, sql, params, type);
    }
  }

  /**
   * Return a List of Objects given an SQL statement
   * 
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params The replacement parameters
   * @param type The Class of the desired return Objects matching the table
   * @return The List of Objects
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static List<? extends Object> queryObjectListSQL(String poolName, String sql, Object[] params, Class<? extends Object> beanClass) {

    List<Object> returnList = null;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionPoolNotFoundException(poolName);
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
      DB_CONNECTION_MANAGER.freeConnection(poolName, con);
    }

    return returnList;
  }

  // ////// OBJECT[] LIST QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return a List of generic Object[]s given a SQL Key using an SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method.
   * 
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @return The List of generic Object[]s
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static List<Object[]> queryGenericObjectArrayListSQLKey(String poolName, String sqlKey, Object[] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return queryGenericObjectArrayListSQL(poolName, sql, params);
    }
  }

  /**
   * Return a List of generic Object[]s given an SQL statement
   * 
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params The replacement parameters
   * @return The List of generic Object[]s
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static List<Object[]> queryGenericObjectArrayListSQL(String poolName, String sql, Object[] params) {

    List<Object[]> returnList = null;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionPoolNotFoundException(poolName);
      }

      con.setAutoCommit(false);

      ResultSetHandler rsh = new ArrayListHandler();
      returnList = (List<Object[]>) new QueryRunner().query(con, sql, rsh, params);

      con.commit();
    } catch (Exception e) {
      logger.error("ERROR QUERYING!!!", e);
      try {
        con.rollback();
      } catch (SQLException e1) {
        logger.error("Exception caught while rolling back transaction", e1);
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(poolName, con);
    }

    return returnList;
  }

  // ////// BATCH //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Batch executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method
   * 
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @return The number of rows affected or each individual execution
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static int[] executeBatchSQLKey(String poolName, String sqlKey, Object[][] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return executeBatchSQL(poolName, sql, params);
    }
  }

  /**
   * Batch executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL statement
   * 
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params The replacement parameters
   * @return The number of rows affected or each individual execution
   * @throws ConnectionPoolNotFoundException if a Connection pool could not be found given the Connection pool name
   */
  public static int[] executeBatchSQL(String poolName, String sql, Object[][] params) {

    int[] returnIntArray = null;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionPoolNotFoundException(poolName);
      }

      con.setAutoCommit(false);

      returnIntArray = new QueryRunner().batch(con, sql, params);

      con.commit();
    } catch (Exception e) {
      logger.error("ERROR QUERYING!!!", e);
      try {
        con.rollback();
      } catch (SQLException e1) {
        logger.error("Exception caught while rolling back transaction", e1);
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(poolName, con);
    }

    return returnIntArray;
  }
}
