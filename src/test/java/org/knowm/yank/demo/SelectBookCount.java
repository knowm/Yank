package org.knowm.yank.demo;

import java.util.Properties;
import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

/** Selects Book count from the BOOKS table. */
public class SelectBookCount {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

    Yank.setupDefaultConnectionPool(dbProps);

    // query
    long numBooks = BooksDAO.getNumBooks();
    System.out.println("The number of books in the table are: " + numBooks);

    Yank.releaseDefaultConnectionPool();
  }
}
