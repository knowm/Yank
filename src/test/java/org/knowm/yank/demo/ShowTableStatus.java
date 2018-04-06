package org.knowm.yank.demo;

import java.util.List;
import java.util.Properties;

import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

/**
 * Gets table status from the YANK database. Demonstrates fetching a List of Object[]s from the DB. You need not return lists of Objects!
 *
 * @author timmolter
 */
public class ShowTableStatus {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");
    // SQL Statements in Properties file
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_SQL.properties");

    Yank.setupDefaultConnectionPool(dbProps);
    Yank.addSQLStatements(sqlProps);

    // query
    List<Object[]> matrix = BooksDAO.getTableStatus();
    for (Object[] objects : matrix) {
      for (Object object : objects) {
        System.out.println(object == null ? "null" : object.toString());
      }
    }

    Yank.releaseDefaultConnectionPool();

  }
}
