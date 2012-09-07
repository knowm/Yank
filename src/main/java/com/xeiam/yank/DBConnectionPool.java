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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
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
  static Logger logger = LoggerFactory.getLogger(DBConnectionPool.class);

  private int checkedOut;
  private Vector<Connection> freeConnections = new Vector<Connection>();
  private int maxConn;
  private String password;
  private String URL;
  private String user;

  /**
   * Creates new connection pool.
   * 
   * @param URL The JDBC URL for the database
   * @param user The database user, or null
   * @param password The database user password, or null
   * @param maxConn The maximal number of connections, or 0 for no limit
   */
  public DBConnectionPool(String URL, String user, String password, int maxConn) {

    this.URL = URL;
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

    // Put the connection at the end of the Vector
    freeConnections.addElement(con);
    checkedOut--;
    notifyAll();
  }

  /**
   * Checks out a connection from the pool. If no free connection is available, a new connection is created unless the max number of connections has been reached. If a free connection has been closed by the database, it's removed from the pool and
   * this method is called again recursively.
   */
  public synchronized Connection getConnection() {

    Connection con = null;
    if (freeConnections.size() > 0) {
      // Pick the first Connection in the Vector
      // to get round-robin usage
      con = freeConnections.firstElement();
      freeConnections.removeElementAt(0);
      try {
        if (con.isClosed()) {
          logger.debug("Removed bad connection from pool");
          // Try again recursively
          con = getConnection();
        }
      } catch (SQLException e) {
        logger.debug("Removed bad connection from pool", e);
        // Try again recursively
        con = getConnection();
      }
    } else if (maxConn == 0 || checkedOut < maxConn) {
      con = newConnection();
    }
    if (con != null) {
      checkedOut++;
    }
    logger.trace("Number of Connections in connection pool = " + checkedOut);
    return con;
  }

  /**
   * Closes all available connections.
   */
  public synchronized void release() {

    Enumeration<Connection> allConnections = freeConnections.elements();
    while (allConnections.hasMoreElements()) {
      logger.debug("Closing connection...");

      Connection con = allConnections.nextElement();
      try {
        con.close();
        logger.debug("Closed a connection in pool.");
      } catch (SQLException e) {
        logger.error("Couldn't close connection for pool.", e);
      } catch (Exception e) {
        logger.error("Couldn't close connection for pool.", e);
      }
    }
    freeConnections.removeAllElements();
  }

  /**
   * Creates a new connection, using a userid and password if specified.
   */
  private Connection newConnection() {

    Connection con = null;
    try {
      if (user == null) {
        con = DriverManager.getConnection(URL);
      } else {
        con = DriverManager.getConnection(URL, user, password);
      }
      logger.debug("Created a new connection in pool.");
    } catch (SQLException e) {
      logger.error("Can't create a new connection for " + URL + ".", e);
      return null;
    }
    return con;
  }
}
