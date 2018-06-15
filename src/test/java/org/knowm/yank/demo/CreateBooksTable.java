package org.knowm.yank.demo;

import java.util.Properties;
import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

/**
 * Create a table called BOOKS. <br>
 * Demonstrates hardcoding the connection pool properties rather then getting them from a Properties
 * file. <br>
 * Note: myconnectionpoolname can be anything but it needs to match the first String argument in
 * DBProxy.* method calls. See BooksDAO.java. <br>
 *
 * @author timmolter
 */
public class CreateBooksTable {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = new Properties();
    dbProps.setProperty("jdbcUrl", "jdbc:mysql://localhost:3306/Yank?serverTimezone=UTC");
    dbProps.setProperty("username", "root");
    dbProps.setProperty("password", "");
    dbProps.setProperty("maximumPoolSize", "5");

    // SQL Statements in Properties file
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_SQL.properties");

    Yank.setupDefaultConnectionPool(dbProps);
    Yank.addSQLStatements(sqlProps);

    // create table
    BooksDAO.createBooksTable();

    Yank.releaseDefaultConnectionPool();
  }
}
