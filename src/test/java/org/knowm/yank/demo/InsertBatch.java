package org.knowm.yank.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

/**
 * Inserts a Batch of Book Objects into the BOOKS table.
 *
 * @author timmolter
 */
public class InsertBatch {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

    Yank.setupDefaultConnectionPool(dbProps);

    // query
    List<Book> books = new ArrayList<Book>();

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    books.add(book);

    book = new Book();
    book.setTitle("Harry Potter");
    book.setAuthor("Joanne K. Rowling");
    book.setPrice(11.99);
    books.add(book);

    book = new Book();
    book.setTitle("Don Quijote");
    book.setAuthor("Cervantes");
    book.setPrice(21.99);
    books.add(book);

    BooksDAO.insertBatch(books);

    Yank.releaseDefaultConnectionPool();
  }
}
