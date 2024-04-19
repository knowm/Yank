package org.knowm.yank;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.yank.demo.Book;
import org.knowm.yank.exceptions.YankSQLException;

/**
 * @author timmolter
 */
public class YankSQLExceptionTest {

  @BeforeClass
  public static void setUpDB() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_SQL.properties");

    Yank.setupDefaultConnectionPool(dbProps);
    Yank.addSQLStatements(sqlProps);
  }

  @AfterClass
  public static void tearDownDB() {

    Yank.releaseDefaultConnectionPool();
  }

  @Test
  public void testBooksTable() {

    Yank.setThrowWrappedExceptions(true);

    String sql =
        "CREATE TABLE Books (TITLE VARCHAR(42) NULL, AUT_HOR VARCHAR(42) NULL, PRICE DECIMAL(10,2) NOT NULL)";
    Yank.execute(sql, null);

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    Object[] params = new Object[] {book.getTitle(), book.getAuthor(), book.getPrice()};
    String SQL = "INSERT INTO BOOKS (TITLE, AUT_HOR, PRICE) VALUES (?, ?, ?, ?)";
    try {
      Yank.execute(SQL, params);
      fail("YankSQLException should have been thrown!!!");
    } catch (YankSQLException e) {
      // e.printStackTrace();
      assertThat(
          e.getMessage(),
          equalTo(
              "Error in SQL query!!!; row column count mismatch in statement [INSERT INTO BOOKS (TITLE, AUT_HOR, PRICE) VALUES (?, ?, ?, ?)] Query: INSERT INTO BOOKS (TITLE, AUT_HOR, PRICE) VALUES (?, ?, ?, ?) Parameters: [Cryptonomicon, Neal Stephenson, 23.99]; Pool Name= yank-default; SQL= INSERT INTO BOOKS (TITLE, AUT_HOR, PRICE) VALUES (?, ?, ?, ?)"));
      SQLException sqlException = e.getSqlException();
    }

    // book = BooksDAO.selectBook("Cryptonomicon");
    // assertThat(book.getPrice(), equalTo(23.99));
    // assertThat(book.getAuthor(), equalTo("Neal Stephenson"));

    Yank.setThrowWrappedExceptions(false);
  }
}
