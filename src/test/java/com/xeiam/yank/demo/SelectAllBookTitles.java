/**
 * Copyright 2011 - 2013 Xeiam LLC.
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
import java.util.Properties;

import com.xeiam.yank.DBConnectionManager;
import com.xeiam.yank.PropertiesUtils;

/**
 * Selects all Book titles from the BOOKS table. Demonstrates fetching a column as a List in a table given the column name
 * 
 * @author timmolter
 */
public class SelectAllBookTitles {

  public static void main(String[] args) {

    // DB Properties
    Properties props = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

    // init DB Connection Manager
    DBConnectionManager.INSTANCE.init(props);

    // query
    List<String> bookTitles = BooksDAO.selectAllBookTitles();
    for (String bookTitle : bookTitles) {
      System.out.println(bookTitle);
    }

    // shutodwn DB Connection Manager
    DBConnectionManager.INSTANCE.release();

  }
}
