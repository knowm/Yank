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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a connection pool. It creates new connections on demand, up to a max number if specified. It also makes sure a connection is still open before it is returned to a client.
 *
 * @author timmolter
 */
public class DBConnectionPool {

  /** slf4J logger wrapper */
  private static Logger logger = LoggerFactory.getLogger(DBConnectionPool.class);

  private int checkedOut = 0;
  private List<Connection> pool = new Vector<Connection>();
  private final int maxConn;
  private final String password;
  private final String url;
  private final String user;

  /**
   * Creates new connection pool.
   *
   * @param url The JDBC URL for the database
   * @param user The database user, or null
   * @param password The database user password, or null
   * @param maxConn The maximal number of connections, or 0 for no limit
   */
  public DBConnectionPool(String url, String user, String password, int maxConn) {

    this.url = url;
    this.user = user;
    this.password = password;
    this.maxConn = maxConn;
  }

  /**
   * Checks in a connection to the pool. Notify other Threads that may be waiting for a connection.
   *
   * @param con The connection to check in
   */
  public synchronized void freeConnection(Connection con) {

    if (con != null) {
      try {
        con.rollback();
      } catch (SQLException e) {
        logger.debug("exception while rolling back connection", e);
      }
      // Put the connection at the end of the Vector
      pool.add(con);
      checkedOut--;
      notifyAll();
    }
  }

  /**
   * Checks out a connection from the pool. If no free connection is available,
   * a new connection is created unless the max number of
   * connections has been reached. If a free connection has been closed
   * by the database, it is removed from the pool and
   * this method is called again recursively.
   *
   * @return a Connection, null if pool size has been exceeded
   */
  public synchronized Connection getConnection() {

    Connection connection = null;
    if (pool.size() > 0) { // if there are some in the pool...
      // Pick the first Connection in the Vector
      // to get round-robin usage
      connection = pool.remove(0);
      try { // but first see if it's clean
        if (connection.isClosed()) {
          logger.debug("Removed closed connection from pool");
          // Try again recursively
          connection = getConnection();
        }
      } catch (SQLException e) {
        logger.debug("Removed bad connection from pool", e);
        // Try again recursively
        connection = getConnection();
      }
    }
    else if (maxConn == 0 || checkedOut < maxConn) { // otherwise the pool was empty
      connection = newConnection();
    }
    if (connection != null) { // keep track of how many are checked out
      checkedOut++;
    }
    logger.trace("Number of Connections in connection pool = " + checkedOut);
    return connection;
  }

  /**
   * Closes all available connections.
   */
  public synchronized void release() {

    for (Connection con : pool) {

      logger.debug("Closing connection...");

      try {
        con.close();
        logger.debug("Closed a connection in pool.");
      } catch (SQLException e) {
        logger.error("Couldn't close connection for pool.", e);
      } catch (Exception e) {
        logger.error("Couldn't close connection for pool.", e);
      }
    }
    pool.clear();
    checkedOut = 0;
  }

  /**
   * Creates a new connection, using a userid and password if specified.
   */
  private Connection newConnection() {

    Connection con = null;
    try {
      if (user == null) {
        con = DriverManager.getConnection(url);
      }
      else {
        con = DriverManager.getConnection(url, user, password);
      }
      logger.debug("Created a new connection in pool.");
    } catch (SQLException e) {
      logger.error("Can't create a new connection for " + url + ".", e);
      return null;
    }
    return con;
  }

  /**
   * For unit testing. Clients should never need this method.
   *
   * @return
   */
  int getCheckedOut() {

    return checkedOut;
  }
}
