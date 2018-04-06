package org.knowm.yank.demo;

import java.util.List;
import java.util.Properties;

import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

/**
 * Selects all Book Objects from the BOOKS table. Demonstrates fetching the connection pool properties from a file on the classpath
 *
 * @author timmolter
 */
public class SelectAllBooks {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

    Yank.setupDefaultConnectionPool(dbProps);

    // query
    List<Book> allBooks = BooksDAO.selectAllBooks();
    for (Book book : allBooks) {
      System.out.println(book.getTitle());
    }

    Yank.releaseDefaultConnectionPool();

  }
}
