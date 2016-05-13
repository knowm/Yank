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
package org.knowm.yank.demo;

import java.util.List;
import java.util.Properties;

import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

/**
 * Selects all Book Objects from the BOOKS table. Demonstrates fetching the connection pool properties from a file on the classpath
 *
 * @author timmolter
 */
public class SelectAllBooks {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

    Yank.setupDefaultConnectionPool(dbProps);

    // query
    List<Book> allBooks = BooksDAO.selectAllBooks();
    for (Book book : allBooks) {
      System.out.println(book.getTitle());
    }

    Yank.releaseDefaultConnectionPool();

  }
}
