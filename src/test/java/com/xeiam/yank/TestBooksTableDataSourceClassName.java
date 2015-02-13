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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xeiam.yank.demo.Book;
import com.xeiam.yank.demo.BooksDAO;

/**
 * @author timmolter
 */
public class TestBooksTableDataSourceClassName {

  @BeforeClass
  public static void setUpDB() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB_DATA_SRC.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_SQL.properties");

    // init YankPoolManager
    Yank.addConnectionPool("myconnectionpoolname", dbProps);
    Yank.addSQLStatements(sqlProps);
  }

  @AfterClass
  public static void tearDownDB() {

    Yank.release();
  }

  @Test
  public void testBooksTable() {

    BooksDAO.createBooksTable();

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    int i = BooksDAO.insertBook(book);
    assertThat(i, equalTo(1));

    List<Book> books = new ArrayList<Book>();

    book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    books.add(book);

    book = new Book();
    book.setTitle("Harry Potter");
    book.setAuthor("Joanne K. Rowling");
    book.setPrice(11.99);
    books.add(book);

    book = new Book();
    book.setTitle("Don Quijote");
    book.setAuthor("Cervantes");
    book.setPrice(21.99);
    books.add(book);

    int[] returnValue = BooksDAO.insertBatch(books);
    assertThat(returnValue.length, equalTo(3));

    List<Book> allBooks = BooksDAO.selectAllBooks();
    assertThat(allBooks.size(), equalTo(4));

    List<String> allBookTitles = BooksDAO.selectAllBookTitles();
    assertThat(allBookTitles.size(), equalTo(4));

    book = BooksDAO.selectBook("Cryptonomicon");
    assertThat(book.getPrice(), equalTo(23.99));

    long numBooks = BooksDAO.getNumBooks();
    assertThat(numBooks, equalTo(4L));

  }
}
