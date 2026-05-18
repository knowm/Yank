package org.knowm.yank;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.yank.demo.Book;
import org.knowm.yank.demo.BooksDAO;

/**
 * Tests insert behaviour with MySQL. Requires a running MySQL instance at localhost:3306 with a
 * database named "Yank" and root access (no password). Tests are skipped automatically if MySQL is
 * not available.
 *
 * <p>To run locally: {@code docker run --rm -p 3306:3306 -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -e
 * MYSQL_DATABASE=Yank mysql:8}
 *
 * <p>For CI, add a MySQL service container to the GitHub Actions workflow.
 */
public class BooksTableMySQLTest {

  private static final String INSERT_SQL =
      "INSERT INTO BOOKS (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";

  private static boolean mysqlAvailable = false;

  @BeforeClass
  public static void setUpDB() {

    try {
      Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");
      Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_SQL.properties");

      Yank.setupDefaultConnectionPool(dbProps);
      Yank.addSQLStatements(sqlProps);

      // Drop table in case it exists from a previous run, then recreate
      Yank.execute("DROP TABLE IF EXISTS BOOKS", null);
      Yank.executeSQLKey("BOOKS_CREATE_TABLE", null);

      mysqlAvailable = true;
    } catch (Exception e) {
      mysqlAvailable = false;
    }
  }

  @AfterClass
  public static void tearDownDB() {

    if (mysqlAvailable) {
      Yank.execute("DROP TABLE IF EXISTS BOOKS", null);
      Yank.releaseDefaultConnectionPool();
    }
  }

  @Test
  public void testDefaultLongInsert() {

    Assume.assumeTrue("MySQL not available - skipping", mysqlAvailable);

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);

    // MySQL AUTO_INCREMENT starts at 1 and returns Long
    long id = BooksDAO.insertBook(book);

    assertTrue(id > 0L);
  }

  @Test
  public void testGenericInsertWithLongClass() {

    Assume.assumeTrue("MySQL not available - skipping", mysqlAvailable);

    Object[] params = {"Snow Crash", "Neal Stephenson", 15.99};

    // MySQL getGeneratedKeys() returns Long - explicit Long.class works
    Long id = Yank.insert(INSERT_SQL, params, Long.class);

    assertThat(id, notNullValue());
    assertTrue(id > 0L);
  }

  @Test
  public void testGenericInsertWithNumberClass() {

    Assume.assumeTrue("MySQL not available - skipping", mysqlAvailable);

    Object[] params = {"Dune", "Frank Herbert", 14.99};

    // Number.class works for any driver
    Number id = Yank.insert(INSERT_SQL, params, Number.class);

    assertThat(id, notNullValue());
    assertTrue(id.longValue() > 0L);
  }
}
