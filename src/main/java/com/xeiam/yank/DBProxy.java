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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xeiam.yank.exceptions.ConnectionException;
import com.xeiam.yank.exceptions.SQLStatementNotFoundException;

/**
 * A wrapper for DBUtils' QueryRunner's methods: update, query, and batch. Connections are retrieved from the connection pool in DBConnectionManager.
 *
 * @author timmolter
 */
public final class DBProxy {

  private static final DBConnectionManager DB_CONNECTION_MANAGER = DBConnectionManager.INSTANCE;

  /** slf4J logger wrapper */
  private static Logger logger = LoggerFactory.getLogger(DBProxy.class);

  private static final String ROLLBACK_EXCEPTION_MESSAGE = "Exception caught while rolling back transaction";
  private static final String QUERY_EXCEPTION_MESSAGE = "Error in SQL query!!!";

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
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static int executeSQLKey(String poolName, String sqlKey, Object[] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    }
    else {
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
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static int executeSQL(String poolName, String sql, Object[] params) {

    int returnInt = 0;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionException(poolName);
      }

      con.setAutoCommit(false);

      returnInt = new QueryRunner().update(con, sql, params);

      con.commit();

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
      if (con != null) {
        try {
          con.rollback();
        } catch (SQLException e1) {
          logger.error(ROLLBACK_EXCEPTION_MESSAGE, e1);
        }
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(con);
    }

    return returnInt;

  }

  // ////// Single Scalar QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return just one scalar given a SQL Key using an SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method. If more than one row match the
   * query, only the first row is returned.
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param type The Class of the desired return scalar matching the table
   * @param params The replacement parameters
   * @return The Object
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static <T> T querySingleScalarSQLKey(String poolName, String sqlKey, Class<T> type, Object[] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    }
    else {
      return querySingleObjectSQL(poolName, sql, type, params);
    }

  }

  /**
   * Return just one scalar given a an SQL statement
   *
   * @param poolName The connection pool name
   * @param type The Class of the desired return scalar matching the table
   * @param params The replacement parameters
   * @return The scalar Object
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static <T> T querySingleScalarSQL(String poolName, String sql, Class<T> type, Object[] params) {

    T returnObject = null;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionException(poolName);
      }

      con.setAutoCommit(false);

      ScalarHandler<T> resultSetHandler = new ScalarHandler<T>();

      returnObject = new QueryRunner().query(con, sql, resultSetHandler, params);

      con.commit();

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
      try {
        con.rollback();
      } catch (SQLException e2) {
        logger.error(ROLLBACK_EXCEPTION_MESSAGE, e2);
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(con);
    }

    return returnObject;
  }

  // ////// Single Object QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return just one Object given a SQL Key using an SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method. If more than one row match the
   * query, only the first row is returned.
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @param type The Class of the desired return Object matching the table
   * @return The Object
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static <T> T querySingleObjectSQLKey(String poolName, String sqlKey, Class<T> type, Object[] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    }
    else {
      return querySingleObjectSQL(poolName, sql, type, params);
    }

  }

  /**
   * Return just one Object given an SQL statement. If more than one row match the query, only the first row is returned.
   *
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params The replacement parameters
   * @param type The Class of the desired return Object matching the table
   * @return The Object
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static <T> T querySingleObjectSQL(String poolName, String sql, Class<T> type, Object[] params) {

    T returnObject = null;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionException(poolName);
      }

      con.setAutoCommit(false);

      BeanHandler<T> resultSetHandler = new BeanHandler<T>(type);

      returnObject = new QueryRunner().query(con, sql, resultSetHandler, params);

      con.commit();

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
      try {
        con.rollback();
      } catch (SQLException e2) {
        logger.error(ROLLBACK_EXCEPTION_MESSAGE, e2);
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(con);
    }

    return returnObject;
  }

  // ////// Object List QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return a List of Objects given a SQL Key using an SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method.
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @param type The Class of the desired return Objects matching the table
   * @return The List of Objects
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static <T> List<T> queryObjectListSQLKey(String poolName, String sqlKey, Class<T> type, Object[] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    }
    else {
      return queryObjectListSQL(poolName, sql, type, params);
    }
  }

  /**
   * Return a List of Objects given an SQL statement
   *
   * @param <T>
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params The replacement parameters
   * @param type The Class of the desired return Objects matching the table
   * @return The List of Objects
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static <T> List<T> queryObjectListSQL(String poolName, String sql, Class<T> type, Object[] params) {

    List<T> returnList = null;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionException(poolName);
      }

      con.setAutoCommit(false);

      BeanListHandler<T> resultSetHandler = new BeanListHandler<T>(type);

      returnList = new QueryRunner().query(con, sql, resultSetHandler, params);

      con.commit();

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
      try {
        con.rollback();
      } catch (SQLException e2) {
        logger.error(ROLLBACK_EXCEPTION_MESSAGE, e2);
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(con);
    }

    return returnList;
  }

  // ////// Column List QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return a List of Objects from a single table column given a SQL Key using an SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method.
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @param type The Class of the desired return Objects matching the table
   * @return The List of Objects
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static <T> List<T> queryColumnListSQLKey(String poolName, String sqlKey, String columnName, Class<T> type, Object[] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    }
    else {
      return queryObjectListSQL(poolName, sql, type, params);
    }
  }

  /**
   * Return a List of Objects from a single table column given an SQL statement
   *
   * @param <T>
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params The replacement parameters
   * @param type The Class of the desired return Objects matching the table
   * @return The List of Objects
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static <T> List<T> queryColumnListSQL(String poolName, String sql, String columnName, Class<T> type, Object[] params) {

    List<T> returnList = null;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionException(poolName);
      }

      con.setAutoCommit(false);

      ColumnListHandler<T> resultSetHandler = new ColumnListHandler<T>(columnName);

      returnList = new QueryRunner().query(con, sql, resultSetHandler, params);

      con.commit();

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
      try {
        con.rollback();
      } catch (SQLException e2) {
        logger.error(ROLLBACK_EXCEPTION_MESSAGE, e2);
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(con);
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
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static List<Object[]> queryGenericObjectArrayListSQLKey(String poolName, String sqlKey, Object[] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    }
    else {
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
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static List<Object[]> queryGenericObjectArrayListSQL(String poolName, String sql, Object[] params) {

    List<Object[]> returnList = null;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionException(poolName);
      }

      con.setAutoCommit(false);

      ArrayListHandler resultSetHandler = new ArrayListHandler();
      returnList = new QueryRunner().query(con, sql, resultSetHandler, params);

      con.commit();
    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
      try {
        con.rollback();
      } catch (SQLException e1) {
        logger.error(ROLLBACK_EXCEPTION_MESSAGE, e1);
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(con);
    }

    return returnList;
  }

  // ////// BATCH //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Batch executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL statement matching the sqlKey String in a properties file passed to DBConnectionManager via the init method
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params An array of query replacement parameters. Each row in this array is one set of batch replacement values
   * @return The number of rows affected or each individual execution
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static int[] executeBatchSQLKey(String poolName, String sqlKey, Object[][] params) {

    String sql = DB_CONNECTION_MANAGER.getSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    }
    else {
      return executeBatchSQL(poolName, sql, params);
    }
  }

  /**
   * Batch executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL statement
   *
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params An array of query replacement parameters. Each row in this array is one set of batch replacement values
   * @return The number of rows affected or each individual execution
   * @throws ConnectionException if a Connection could not be obtained for some reason
   */
  public static int[] executeBatchSQL(String poolName, String sql, Object[][] params) {

    int[] returnIntArray = null;

    Connection con = null;

    try {
      con = DB_CONNECTION_MANAGER.getConnection(poolName);

      if (con == null) {
        throw new ConnectionException(poolName);
      }

      con.setAutoCommit(false);

      returnIntArray = new QueryRunner().batch(con, sql, params);

      con.commit();
    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
      try {
        con.rollback();
      } catch (SQLException e1) {
        logger.error(ROLLBACK_EXCEPTION_MESSAGE, e1);
      }
    } finally {
      DB_CONNECTION_MANAGER.freeConnection(con);
    }

    return returnIntArray;
  }
}
