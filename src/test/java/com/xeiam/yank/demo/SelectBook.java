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
package com.xeiam.yank.demo;

import java.util.Properties;

import com.xeiam.yank.Yank;

/**
 * Selects a single Book from the BOOKS table. Demonstrates using the Yank API without DAOs or properties.
 *
 * @author timmolter
 */
public class SelectBook {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = new Properties();
    dbProps.setProperty("jdbcUrl", "jdbc:mysql://localhost:3306/Yank");
    dbProps.setProperty("username", "root");
    dbProps.setProperty("password", "");

    // add connection pool
    Yank.setupDataSource(dbProps);

    // query book
    String sql = "SELECT * FROM BOOKS WHERE TITLE = ?";
    Object[] params = new Object[] { "Cryptonomicon" };
    Book book = Yank.queryBean(sql, Book.class, params);
    System.out.println(book.toString());

    // release connection pool
    Yank.releaseDataSource();

  }
}
