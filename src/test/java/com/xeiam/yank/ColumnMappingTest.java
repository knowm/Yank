package com.xeiam.yank;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xeiam.yank.annotations.Column;

/**
 * @author timmolter
 */
public class ColumnMappingTest {

  @BeforeClass
  public static void setUpDB() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_SQL.properties");

    Yank.addConnectionPool(dbProps);
    Yank.addSQLStatements(sqlProps);
  }

  @AfterClass
  public static void tearDownDB() {

    Yank.release();
  }

  @Test
  public void testBooksTable() {

    // Make the column names German :)
    String sql = "CREATE TABLE Buecher (TITEL VARCHAR(42) NULL, AUTOR VARCHAR(42) NULL, PREIS DECIMAL(10,2) NOT NULL)";
    Yank.executeSQL(sql, null);

    Buch book = new Buch();
    book.setTitle("Cryptonomicon");
    book.setAuthorName("Neal Stephenson");
    book.setPrice(23.99);
    Object[] params = new Object[] { book.getTitle(), book.getAuthorName(), book.getPrice() };
    sql = "INSERT INTO Buecher  (TITEL, AUTOR, PREIS) VALUES (?, ?, ?)";
    Yank.executeSQL(sql, params);

    sql = "SELECT * FROM Buecher WHERE TITEL = ?";
    params = new Object[] { "Cryptonomicon" };
    book = Yank.querySingleObjectSQL(sql, Buch.class, params);

    assertThat(book.getPrice(), equalTo(23.99));
    assertThat(book.getAuthorName(), equalTo("Neal Stephenson"));

  }

  public static class Buch {

    private int id;
    @Column("TITEL")
    private String title;
    @Column("AUTOR")
    private String authorName;
    @Column("PREIS")
    private double price;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getTitle() {

      return title;
    }

    public void setTitle(String title) {

      this.title = title;
    }

    public String getAuthorName() {
      return authorName;
    }

    public void setAuthorName(String authorName) {
      this.authorName = authorName;
    }

    public double getPrice() {

      return price;
    }

    public void setPrice(double price) {

      this.price = price;
    }

  }

}
