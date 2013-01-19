/**
 * Copyright (C) 2013 Xeiam LLC http://xeiam.com
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xeiam.yank.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xeiam.yank.DBConnectionManager;
import com.xeiam.yank.PropertiesUtils;
import com.xeiam.yank.demo.Book;
import com.xeiam.yank.demo.BooksDAO;

/**
 * @author timmolter
 */
public class TestBooksTable {

  @BeforeClass
  public static void setUpDB() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_SQL.properties");

    DBConnectionManager.INSTANCE.init(dbProps, sqlProps);
  }

  @AfterClass
  public static void tearDownDB() {

    DBConnectionManager.INSTANCE.release();
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

    book = BooksDAO.selectBook("Cryptonomicon");
    assertThat(book.getPrice(), equalTo(23.99));

  }
}
