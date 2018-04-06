package org.knowm.yank.demo;

import java.util.Properties;

import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

/**
 * Create a table called BOOKS. Demonstrates getting the connection pool properties from a *.properties file.
 *
 * @author timmolter
 */
public class CreateBooksTableWithPropsFile {

  public static void main(String[] args) {

    // MYSQL_DB.properties file on classpath
    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

    // Alternative method: MYSQL_DB.properties file using path to file
    // Properties props = PropertiesUtils.getPropertiesFromPath("/path/to/MYSQL_DB.properties");

    // SQL Statements in Properties file
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_SQL.properties");

    Yank.setupDefaultConnectionPool(dbProps);
    Yank.addSQLStatements(sqlProps);

    // query
    BooksDAO.createBooksTable();

    Yank.releaseDefaultConnectionPool();

  }
}
