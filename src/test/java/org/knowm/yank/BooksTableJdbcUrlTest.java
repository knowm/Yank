/**
 * Copyright 2015-2016 Knowm Inc. (http://knowm.org) and contributors.
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

import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.yank.demo.Book;
import org.knowm.yank.demo.BooksDAO;

/**
 * @author timmolter
 */
public class BooksTableJdbcUrlTest {

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

    BooksDAO.createBooksTable();

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    long i = BooksDAO.insertBook(book);
    assertThat(i, equalTo(0L));

    List<Book> allBooks = BooksDAO.selectAllBooks();
    assertThat(allBooks.size(), equalTo(1));

  }
}
