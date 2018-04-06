package org.knowm.yank.demo;

import java.util.Properties;
import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

/**
 * Inserts a Book into the BOOKS table. Demonstrates fetching the connection pool properties from a
 * file on the classpath
 *
 * @author timmolter
 */
public class InsertBook {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

    Yank.setupDefaultConnectionPool(dbProps);

    // query
    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    long autoID = BooksDAO.insertBook(book);
    System.out.println(autoID);

    Yank.releaseDefaultConnectionPool();
  }
}
