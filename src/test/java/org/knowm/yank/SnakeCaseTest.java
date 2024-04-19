package org.knowm.yank;

import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.yank.demo.Book;

/**
 * @author timmolter
 */
public class SnakeCaseTest {

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

    String sql =
        "CREATE TABLE Books (TITLE VARCHAR(42) NULL, AUT_HOR VARCHAR(42) NULL, PRICE DECIMAL(10,2) NOT NULL)";
    Yank.execute(sql, null);

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    Object[] params = new Object[] {book.getTitle(), book.getAuthor(), book.getPrice()};
    String SQL = "INSERT INTO BOOKS  (TITLE, AUT_HOR, PRICE) VALUES (?, ?, ?)";
    Yank.execute(SQL, params);
  }
}
