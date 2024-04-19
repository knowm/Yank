package org.knowm.yank;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.yank.demo.Book;
import org.knowm.yank.demo.BooksDAO;

/**
 * @author timmolter
 */
public class BooksTableDataSourceClassNameTest {

  @BeforeClass
  public static void setUpDB() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB_DATA_SRC.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_SQL.properties");

    // init YankPoolManager
    Yank.setupDefaultConnectionPool(dbProps);
    Yank.addSQLStatements(sqlProps);
  }

  @AfterClass
  public static void tearDownDB() {

    Yank.releaseDefaultConnectionPool();
  }

  @Test
  public void testBooksTable() {

    BooksDAO.createBooksTable();

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    long i = BooksDAO.insertBook(book);
    assertThat(i, equalTo(0L));
    i = BooksDAO.insertBook(book);
    assertThat(i, equalTo(1L));

    List<Book> books = new ArrayList<Book>();

    book = new Book();
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

    int[] returnValue = BooksDAO.insertBatch(books);
    assertThat(returnValue.length, equalTo(3));

    List<Book> allBooks = BooksDAO.selectAllBooks();
    assertThat(allBooks.size(), equalTo(5));

    List<String> allBookTitles = BooksDAO.selectAllBookTitles();
    assertThat(allBookTitles.size(), equalTo(5));

    book = BooksDAO.selectBook("Cryptonomicon");
    assertThat(book.getPrice(), equalTo(23.99));

    long numBooks = BooksDAO.getNumBooks();
    assertThat(numBooks, equalTo(5L));
  }
}
