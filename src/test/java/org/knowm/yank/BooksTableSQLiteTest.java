package org.knowm.yank;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.yank.demo.Book;
import org.knowm.yank.demo.BooksDAO;

/**
 * Tests insert behaviour with SQLite (xerial sqlite-jdbc).
 *
 * <p>Key difference vs MySQL/HSQL: SQLite's {@code getGeneratedKeys()} may return a different
 * numeric type depending on the driver version. Using {@link Number} as the idType is always safe.
 * The default {@code Long} path works via {@code Number.longValue()}.
 */
public class BooksTableSQLiteTest {

  private static final String INSERT_SQL =
      "INSERT INTO BOOKS (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";

  @BeforeClass
  public static void setUpDB() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("SQLITE_DB.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("SQLITE_SQL.properties");

    Yank.setupDefaultConnectionPool(dbProps);
    Yank.addSQLStatements(sqlProps);
    Yank.executeSQLKey("BOOKS_CREATE_TABLE", null);
  }

  @AfterClass
  public static void tearDownDB() {

    Yank.releaseDefaultConnectionPool();
  }

  @Test
  public void testDefaultLongInsert() {

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);

    // Default path: always returns Long regardless of what the driver returns internally
    long id = BooksDAO.insertBook(book);

    assertTrue(id > 0L); // SQLite rowid starts at 1
  }

  @Test
  public void testGenericInsertWithNumberClass() {

    Object[] params = {"Snow Crash", "Neal Stephenson", 15.99};

    // Number.class is safe for any numeric driver (Integer, Long, BigInteger, etc.)
    Number id = Yank.insert(INSERT_SQL, params, Number.class);

    assertThat(id, notNullValue());
    assertTrue(id.longValue() > 0L);
  }

  @Test
  public void testSQLiteDriverReturnsInteger() {

    Object[] params = {"The Hobbit", "J.R.R. Tolkien", 8.99};

    // xerial sqlite-jdbc returns Integer (not Long) for row IDs that fit in 32 bits,
    // which is always the case in practice. This is the root cause of issue #137.
    Number id = Yank.insert(INSERT_SQL, params, Number.class);

    assertThat(id, notNullValue());
    assertTrue(
        "Expected SQLite to return Integer for small row IDs, but got: " + id.getClass().getName(),
        id instanceof Integer);
  }

  @Test
  public void testInsertWithIntegerClass() {

    Object[] params = {"Snow Crash", "Neal Stephenson", 15.99};

    // Integer.class works correctly with SQLite
    Integer id = Yank.insert(INSERT_SQL, params, Integer.class);

    assertThat(id, notNullValue());
    assertTrue(id > 0);
  }

  @Test(expected = ClassCastException.class)
  public void testInsertWithLongClassThrowsForSQLite() {

    Object[] params = {"Dune", "Frank Herbert", 14.99};

    // SQLite returns Integer, so Long.class.cast(Integer) throws ClassCastException.
    // Use Integer.class, Number.class, or the default Long insert() instead.
    Yank.insert(INSERT_SQL, params, Long.class);
  }

  @Test
  public void testSelectAfterInsert() {

    Book book = new Book();
    book.setTitle("Cryptonomicon 2");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);

    BooksDAO.insertBook(book);

    List<Book> allBooks = BooksDAO.selectAllBooks();
    assertTrue(allBooks.size() >= 1);
  }
}
