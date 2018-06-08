package org.knowm.yank;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.yank.annotations.Column;

/** @author timmolter */
public class QueryColumnTest {

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

    // Make the column names German :)
    String sql =
        "CREATE TABLE Buecher (ID INT NULL, AUTOR VARCHAR(42) NULL, PREIS DECIMAL(10,2) NOT NULL)";
    Yank.execute(sql, null);

    Buch book = new Buch();
    book.setId(0);
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    Object[] params = new Object[] {book.getId(), book.getAuthor(), book.getPrice()};
    sql = "INSERT INTO Buecher (ID, AUTOR, PREIS) VALUES (?, ?, ?)";
    Yank.execute(sql, params);

    Buch book2 = new Buch();
    book2.setId(1);
    book2.setAuthor("Neal Stephenson");
    book2.setPrice(23.99);
    params = new Object[] {book2.getId(), book2.getAuthor(), book2.getPrice()};
    sql = "INSERT INTO Buecher (ID, AUTOR, PREIS) VALUES (?, ?, ?)";
    Yank.execute(sql, params);

    sql = "SELECT ID FROM Buecher ORDER BY ID ASC";
    List<Integer> ids = Yank.queryColumn(sql, "ID", Integer.class, null);
    assertThat(ids.size(), equalTo(2));
    assertThat(ids.get(0), equalTo(0));
  }

  public static class Buch {

    @Column("ID")
    private int id;

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
