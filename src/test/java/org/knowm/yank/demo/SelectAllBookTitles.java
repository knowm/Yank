package org.knowm.yank.demo;

import java.util.List;
import java.util.Properties;
import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

/**
 * Selects all Book titles from the BOOKS table. Demonstrates fetching a column as a List in a table
 * given the column name
 *
 * @author timmolter
 */
public class SelectAllBookTitles {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

    Yank.setupDefaultConnectionPool(dbProps);

    // query
    List<String> bookTitles = BooksDAO.selectAllBookTitles();
    for (String bookTitle : bookTitles) {
      System.out.println(bookTitle);
    }

    Yank.releaseDefaultConnectionPool();
  }
}
