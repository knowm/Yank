/**
 * Copyright 2015-2017 Knowm Inc. (http://knowm.org) and contributors.
 * Copyright 2011-2015 Xeiam LLC (http://xeiam.com) and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.knowm.yank;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.yank.annotations.Column;

/**
 * @author timmolter
 */
public class ColumnMappingTest {

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
