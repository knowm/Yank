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

    Yank.setupDataSource(dbProps);
    Yank.addSQLStatements(sqlProps);
  }

  @AfterClass
  public static void tearDownDB() {

    Yank.releaseDataSource();
  }

  @Test
  public void testBooksTable() {

    // Make the column names German :)
    String sql = "CREATE TABLE Buecher (TITEL VARCHAR(42) NULL, AUTOR VARCHAR(42) NULL, PREIS DECIMAL(10,2) NOT NULL)";
    Yank.execute(sql, null);

    Buch book = new Buch();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    Object[] params = new Object[] { book.getTitle(), book.getAuthor(), book.getPrice() };
    sql = "INSERT INTO Buecher  (TITEL, AUTOR, PREIS) VALUES (?, ?, ?)";
    Yank.execute(sql, params);

    sql = "SELECT * FROM Buecher WHERE TITEL = ?";
    params = new Object[] { "Cryptonomicon" };
    book = Yank.queryBean(sql, Buch.class, params);

    assertThat(book.getPrice(), equalTo(23.99));
    assertThat(book.getAuthor(), equalTo("Neal Stephenson"));

  }

  public static class Buch {

    private int id;
    @Column("TITEL")
    private String title;
    @Column("AUTOR")
    private String author;
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

    public String getAuthor() {
      return author;
    }

    public void setAuthor(String author) {
      this.author = author;
    }

    public double getPrice() {

      return price;
    }

    public void setPrice(double price) {

      this.price = price;
    }

  }

}
