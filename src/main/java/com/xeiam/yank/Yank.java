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

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xeiam.yank.exceptions.SQLStatementNotFoundException;
import com.xeiam.yank.handlers.InsertedIDResultSetHandler;
import com.xeiam.yank.processors.YankBeanProcessor;

/**
 * A wrapper for DBUtils' QueryRunner's methods: update, query, and batch. Connections are retrieved from the connection pool in DBConnectionManager.
 *
 * @author timmolter
 */
public final class Yank {

  private static final YankPoolManager YANK_POOL_MANAGER = YankPoolManager.INSTANCE;

  /** slf4J logger wrapper */
  private static Logger logger = LoggerFactory.getLogger(Yank.class);

  private static final String QUERY_EXCEPTION_MESSAGE = "Error in SQL query!!!";

  /**
   * Prevent class instantiation with private constructor
   */
  private Yank() {

  }

  // ////// INSERT //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Executes a given INSERT SQL prepared statement matching the sqlKey String in a properties file loaded via Yank.addSQLStatements(...). Returns the
   * auto-increment id of the inserted row.
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @return the auto-increment id of the inserted row, or null if no id is available
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   */
  public static Long insertSQLKey(String sqlKey, Object[] params) {

    String sql = YANK_POOL_MANAGER.getMergedSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return insertSQL(sql, params);
    }
  }

  /**
   * Executes a given INSERT SQL prepared statement. Returns the auto-increment id of the inserted row.
   *
   * @param poolName The connection pool name
   * @param sql The query to execute
   * @param params The replacement parameters
   * @return the auto-increment id of the inserted row, or null if no id is available
   */
  public static Long insertSQL(String sql, Object[] params) {

    Long returnLong = null;

    try {
      ResultSetHandler<Long> rsh = new InsertedIDResultSetHandler();
      returnLong = new QueryRunner(YANK_POOL_MANAGER.getDataSource()).insert(sql, rsh, params);
    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
    }

    return returnLong == null ? 0 : returnLong;
  }

  // ////// INSERT, UPDATE, DELETE, or UPSERT //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL statement matching the sqlKey String in a properties file loaded via
   * Yank.addSQLStatements(...). Returns the number of rows affected.
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @return The number of rows affected
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   */
  public static int executeSQLKey(String sqlKey, Object[] params) {

    String sql = YANK_POOL_MANAGER.getMergedSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return executeSQL(sql, params);
    }
  }

  /**
   * Executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL prepared statement. Returns the number of rows affected.
   *
   * @param poolName The connection pool name
   * @param sql The query to execute
   * @param params The replacement parameters
   * @return The number of rows affected
   */
  public static int executeSQL(String sql, Object[] params) {

    int returnInt = 0;

    try {

      returnInt = new QueryRunner(YANK_POOL_MANAGER.getDataSource()).update(sql, params);

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
    }

    return returnInt;
  }

  // ////// Single Scalar QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return just one scalar given a SQL Key using an SQL statement matching the sqlKey String in a properties file loaded via
   * Yank.addSQLStatements(...). If more than one row match the query, only the first row is returned.
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param type The Class of the desired return scalar matching the table
   * @param params The replacement parameters
   * @return The Object
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   */
  public static <T> T querySingleScalarSQLKey(String sqlKey, Class<T> type, Object[] params) {

    String sql = YANK_POOL_MANAGER.getMergedSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return querySingleObjectSQL(sql, type, params);
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
   */
  public static <T> T querySingleScalarSQL(String sql, Class<T> type, Object[] params) {

    T returnObject = null;

    try {

      ScalarHandler<T> resultSetHandler = new ScalarHandler<T>();

      returnObject = new QueryRunner(YANK_POOL_MANAGER.getDataSource()).query(sql, resultSetHandler, params);

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
    }

    return returnObject;
  }

  // ////// Single Object QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return just one Object given a SQL Key using an SQL statement matching the sqlKey String in a properties file loaded via
   * Yank.addSQLStatements(...). If more than one row match the query, only the first row is returned.
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @param type The Class of the desired return Object matching the table
   * @return The Object
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   */
  public static <T> T querySingleObjectSQLKey(String sqlKey, Class<T> type, Object[] params) {

    String sql = YANK_POOL_MANAGER.getMergedSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return querySingleObjectSQL(sql, type, params);
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
   */
  public static <T> T querySingleObjectSQL(String sql, Class<T> type, Object[] params) {

    T returnObject = null;

    try {

      BeanHandler<T> resultSetHandler = new BeanHandler<T>(type, new BasicRowProcessor(new YankBeanProcessor(type)));

      returnObject = new QueryRunner(YANK_POOL_MANAGER.getDataSource()).query(sql, resultSetHandler, params);

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
    }

    return returnObject;
  }

  // ////// Object List QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return a List of Objects given a SQL Key using an SQL statement matching the sqlKey String in a properties file loaded via
   * Yank.addSQLStatements(...).
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @param type The Class of the desired return Objects matching the table
   * @return The List of Objects
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   */
  public static <T> List<T> queryObjectListSQLKey(String sqlKey, Class<T> type, Object[] params) {

    String sql = YANK_POOL_MANAGER.getMergedSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return queryObjectListSQL(sql, type, params);
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
   */
  public static <T> List<T> queryObjectListSQL(String sql, Class<T> type, Object[] params) {

    List<T> returnList = null;

    try {

      BeanListHandler<T> resultSetHandler = new BeanListHandler<T>(type, new BasicRowProcessor(new GenerousBeanProcessor()));

      returnList = new QueryRunner(YANK_POOL_MANAGER.getDataSource()).query(sql, resultSetHandler, params);

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
    }

    return returnList;
  }

  // ////// Column List QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return a List of Objects from a single table column given a SQL Key using an SQL statement matching the sqlKey String in a properties file loaded
   * via Yank.addSQLStatements(...).
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @param type The Class of the desired return Objects matching the table
   * @return The List of Objects
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   */
  public static <T> List<T> queryColumnListSQLKey(String sqlKey, String columnName, Class<T> type, Object[] params) {

    String sql = YANK_POOL_MANAGER.getMergedSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return queryObjectListSQL(sql, type, params);
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
   */
  public static <T> List<T> queryColumnListSQL(String sql, String columnName, Class<T> type, Object[] params) {

    List<T> returnList = null;

    try {

      ColumnListHandler<T> resultSetHandler = new ColumnListHandler<T>(columnName);

      returnList = new QueryRunner(YANK_POOL_MANAGER.getDataSource()).query(sql, resultSetHandler, params);

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
    }

    return returnList;
  }

  // ////// OBJECT[] LIST QUERY //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Return a List of generic Object[]s given a SQL Key using an SQL statement matching the sqlKey String in a properties file loaded via
   * Yank.addSQLStatements(...).
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params The replacement parameters
   * @return The List of generic Object[]s
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   */
  public static List<Object[]> queryGenericObjectArrayListSQLKey(String sqlKey, Object[] params) {

    String sql = YANK_POOL_MANAGER.getMergedSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return queryGenericObjectArrayListSQL(sql, params);
    }
  }

  /**
   * Return a List of generic Object[]s given an SQL statement
   *
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params The replacement parameters
   * @return The List of generic Object[]s
   */
  public static List<Object[]> queryGenericObjectArrayListSQL(String sql, Object[] params) {

    List<Object[]> returnList = null;

    try {

      ArrayListHandler resultSetHandler = new ArrayListHandler(new BasicRowProcessor(new GenerousBeanProcessor()));
      // returnList = new QueryRunner().query(con, sql, resultSetHandler, params);
      returnList = new QueryRunner(YANK_POOL_MANAGER.getDataSource()).query(sql, resultSetHandler, params);

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
    }

    return returnList;
  }

  // ////// BATCH //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Batch executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL statement matching the sqlKey String in a properties file loaded via
   * Yank.addSQLStatements(...).
   *
   * @param poolName The connection pool name
   * @param sqlKey The SQL Key found in a properties file corresponding to the desired SQL statement value
   * @param params An array of query replacement parameters. Each row in this array is one set of batch replacement values
   * @return The number of rows affected or each individual execution
   * @throws SQLStatementNotFoundException if an SQL statement could not be found for the given sqlKey String
   */
  public static int[] executeBatchSQLKey(String sqlKey, Object[][] params) {

    String sql = YANK_POOL_MANAGER.getMergedSqlProperties().getProperty(sqlKey);
    if (sql == null || sql.equalsIgnoreCase("")) {
      throw new SQLStatementNotFoundException();
    } else {
      return executeBatchSQL(sql, params);
    }
  }

  /**
   * Batch executes the given INSERT, UPDATE, DELETE, REPLACE or UPSERT SQL statement
   *
   * @param poolName The connection pool name
   * @param sql The SQL statement
   * @param params An array of query replacement parameters. Each row in this array is one set of batch replacement values
   * @return The number of rows affected or each individual execution
   */
  public static int[] executeBatchSQL(String sql, Object[][] params) {

    int[] returnIntArray = null;

    try {

      returnIntArray = new QueryRunner(YANK_POOL_MANAGER.getDataSource()).batch(sql, params);

    } catch (Exception e) {
      logger.error(QUERY_EXCEPTION_MESSAGE, e);
    }

    return returnIntArray;
  }

  /**
   * Add properties for a connection pool. Yank uses a Hikari connection pool under the hood, so you have to provide the minimal essential properties
   * and the optional properties as defined here: https://github.com/brettwooldridge/HikariCP
   *
   * @param connectionPoolProperties
   */
  public static void addConnectionPool(Properties connectionPoolProperties) {

    YANK_POOL_MANAGER.addConnectionPool(connectionPoolProperties);
  }

  /**
   * Add SQL statements in a properties file. Adding more will merge Properties.
   *
   * @param sqlProperties
   */
  public static void addSQLStatements(Properties sqlProperties) {

    YANK_POOL_MANAGER.addSQLStatements(sqlProperties);
  }

  /**
   * Closes all open connection pools
   */
  public static synchronized void release() {

    YANK_POOL_MANAGER.release();
  }

  /**
   * Exposes access to the configured DataSource
   *
   * @return a configured (pooled) DataSource.
   */
  public static DataSource getDataSource() {

    return YANK_POOL_MANAGER.getDataSource();
  }
}
