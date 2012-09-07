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

import java.util.Properties;

import com.xeiam.yank.DBConnectionManager;
import com.xeiam.yank.PropertiesUtils;

/**
 * Inserts a Book into the BOOKS table. Demonstrates fetching the connection pool properties from a file on the classpath
 * 
 * @author timmolter
 */
public class InsertBook {

  public static void main(String[] args) {

    Properties props = PropertiesUtils.getPropertiesFromClasspath("DB.properties");

    DBConnectionManager.INSTANCE.init(props);

    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    BooksDAO.insertBook(book);

    DBConnectionManager.INSTANCE.release();

  }
}
