/**
 * Copyright 2015 Knowm Inc. (http://knowm.org) and contributors.
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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;
import org.knowm.yank.demo.Book;

/**
 * @author timmolter
 */
public class GenericObjectTest {

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
    String sql = "CREATE TABLE BOOKS (TITLE VARCHAR(42) NULL, AUTHOR VARCHAR(42) NULL, PRICE DECIMAL(10,2) NOT NULL)";
    Yank.execute(sql, null);

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    Object[] params = new Object[] { book.getTitle(), book.getAuthor(), book.getPrice() };
    sql = "INSERT INTO BOOKS (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";
    Yank.execute(sql, params);

    sql = "SELECT * FROM BOOKS WHERE TITLE = ?";
    params = new Object[] { "Cryptonomicon" };
    List<Object[]> rawRows = Yank.queryObjectArrays(sql, params);

    assertThat(rawRows.get(0), not(equalTo(null)));
    assertThat((String) rawRows.get(0)[0], equalTo("Cryptonomicon"));
    assertThat((BigDecimal) rawRows.get(0)[2], equalTo(new BigDecimal("23.99")));

  }

}
