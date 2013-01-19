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
package com.xeiam.yank.demo;

import java.util.List;

import com.xeiam.yank.DBProxy;

/**
 * DAO (Data Access Object) Class for BOOKS table. <br>
 * This is where you create your own methods for SQL interaction with a database table.<br>
 * Each table in your database should have it's own DAO Class.<br>
 * 
 * @author timmolter
 */
public class BooksDAO {

  /**
   * This method demonstrates:
   * <ul>
   * <li>executing an SQL statement with DBProxy.executeSQL</li>
   * <li>using a prepared statement with corresponding params</li>
   * </ul>
   */
  public static int insertBook(Book book) {

    Object[] params = new Object[] { book.getTitle(), book.getAuthor(), book.getPrice() };
    String SQL = "INSERT INTO BOOKS  (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";
    return DBProxy.executeSQL("myconnectionpoolname", SQL, params);
  }

  /**
   * This method demonstrates:
   * <ul>
   * <li>querying a table for a list of Objects, in this case Book objects, using DBProxy.queryObjectListSQL</li>
   * <li>using a non-prepared statement with null params</li>
   * </ul>
   */
  public static List<Book> selectAllBooks() {

    String SQL = "SELECT * FROM BOOKS";
    return DBProxy.queryObjectListSQL("myconnectionpoolname", SQL, Book.class, null);
  }

  /**
   * This method demonstrates:
   * <ul>
   * <li>executing a batch insert statement using DBProxy.executeBatchSQL</li>
   * <li>using a prepared statement with corresponding params</li>
   * </ul>
   */
  public static int[] insertBatch(List<Book> pBooks) {

    Object[][] params = new Object[pBooks.size()][];

    for (int i = 0; i < pBooks.size(); i++) {
      Book book = pBooks.get(i);
      params[i] = new Object[] { book.getTitle(), book.getAuthor(), book.getPrice() };
    }

    String SQL = "INSERT INTO BOOKS  (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";
    return DBProxy.executeBatchSQL("myconnectionpoolname", SQL, params);
  }

  /**
   * This method demonstrates:
   * <ul>
   * <li>the advanced feature of using an SQL Key corresponding to an actual SQL statement stored in a Properties file using DBProxy.executeSQLKey</li>
   * <li>using a non-prepared statement with null params</li>
   * </ul>
   */
  public static int createBooksTable() {

    String sqlKey = "BOOKS_CREATE_TABLE";
    return DBProxy.executeSQLKey("myconnectionpoolname", sqlKey, null);
  }

  /**
   * This method demonstrates:
   * <ul>
   * <li>the advanced feature of using an SQL Key corresponding to an actual SQL statement stored in a Properties file using DBProxy.querySingleObjectSQLKey</li>
   * <li>using a prepared statement with corresponding params</li>
   * </ul>
   */
  public static Book selectBook(String pTitle) {

    Object[] params = new Object[] { pTitle };

    String sqlKey = "BOOKS_SELECT_BY_TITLE";
    return DBProxy.querySingleObjectSQLKey("myconnectionpoolname", sqlKey, Book.class, params);
  }

  /**
   * This method demonstrates:
   * <ul>
   * <li>the advanced feature of using an SQL Key corresponding to an actual SQL statement stored in a Properties file using DBProxy.queryGenericObjectArrayListSQLKey</li>
   * <li>using a non-prepared statement with null params</li>
   * <li>querying for a List of Objects representing all columns in a table</li>
   * </ul>
   */
  public static List<Object[]> getTableStatus() {

    String sqlKey = "BOOKS_SELECT_TABLE_STATUS";
    return DBProxy.queryGenericObjectArrayListSQLKey("myconnectionpoolname", sqlKey, null);
  }

}
