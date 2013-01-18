/**
 * Copyright 2011 Xeiam LLC.
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
package com.xeiam.yank.example;

import java.util.List;

import com.xeiam.yank.DBProxy;

/**
 * DAO Class for BOOKS table
 * 
 * @author timmolter
 */
public class BooksDAO {

  public static int insertBook(Book pBook) {

    Object[] params = new Object[] { pBook.getTitle(), pBook.getAuthor(), pBook.getPrice() };
    String SQL = "INSERT INTO BOOKS  (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";
    return DBProxy.executeSQL("local", SQL, params);
  }

  public static List<Book> selectAllBooks() {

    String SQL = "SELECT * FROM BOOKS";
    return DBProxy.queryObjectListSQL("local", SQL, null, Book.class);
  }

  public static int[] insertBatch(List<Book> pBooks) {

    Object[][] params = new Object[pBooks.size()][];

    for (int i = 0; i < pBooks.size(); i++) {
      Book book = pBooks.get(i);
      params[i] = new Object[] { book.getTitle(), book.getAuthor(), book.getPrice() };
    }

    String SQL = "INSERT INTO BOOKS  (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";
    return DBProxy.executeBatchSQL("local", SQL, params);
  }

  public static int createBooksTable() {

    String SQL = "CREATE TABLE `Books` (`TITLE` varchar(42) DEFAULT NULL, `AUTHOR` varchar(42) DEFAULT NULL,`PRICE` double DEFAULT NULL) ENGINE=MyISAM DEFAULT CHARSET=utf8;";
    return DBProxy.executeSQL("local", SQL, null);
  }

  public static Book selectBook(String pTitle) {

    Object[] params = new Object[] { pTitle };
    return DBProxy.querySingleObjectSQLKey("local", "BOOKS_SELECT_BY_TITLE", Book.class, params);
  }

  public static List<Object[]> getTableStatus() {

    return DBProxy.queryGenericObjectArrayListSQLKey("local", "BOOKS_SELECT_TABLE_STATUS", null);
  }

}
