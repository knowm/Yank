/**
 * Copyright 2011 - 2015 Xeiam LLC.
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
package com.xeiam.yank;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xeiam.yank.demo.Book;
import com.xeiam.yank.demo.BooksDAO;

/**
 * @author timmolter
 */
public class GenerousBeanHandlerTest {

  @BeforeClass
  public static void setUpDB() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_SQL.properties");

    Yank.addConnectionPool("myconnectionpoolname", dbProps);
    Yank.addSQLStatements(sqlProps);
  }

  @AfterClass
  public static void tearDownDB() {

    Yank.release();
  }

  @Test
  public void testBooksTable() {

    String sqlKey = "CREATE TABLE Books (TITLE VARCHAR(42) NULL, AUTHOR_NAME VARCHAR(42) NULL, PRICE DECIMAL(10,2) NOT NULL)";
    Yank.executeSQL("myconnectionpoolname", sqlKey, null);

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthorName("Neal Stephenson");
    book.setPrice(23.99);
    Object[] params = new Object[] { book.getTitle(), book.getAuthorName(), book.getPrice() };
    String SQL = "INSERT INTO BOOKS  (TITLE, AUTHOR_NAME, PRICE) VALUES (?, ?, ?)";
    Yank.executeSQL("myconnectionpoolname", SQL, params);

    book = BooksDAO.selectBook("Cryptonomicon");
    assertThat(book.getPrice(), equalTo(23.99));
    assertThat(book.getAuthorName(), equalTo("Neal Stephenson"));

  }

}
