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

import java.util.List;
import java.util.Properties;

import com.xeiam.yank.YankPoolManager;
import com.xeiam.yank.PropertiesUtils;

/**
 * Gets table status from the YANK database. Demonstrates fetching a List of Object[]s from the DB. You need not return lists of Objects!
 * 
 * @author timmolter
 */
public class ShowTableStatus {

  public static void main(String[] args) {

    // DB Properties
    Properties dbprops = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");
    // SQL Statements in Properties file
    Properties sqlprops = PropertiesUtils.getPropertiesFromClasspath("MYSQL_SQL.properties");

    // init DB Connection Manager
    YankPoolManager.INSTANCE.init(dbprops, sqlprops);

    // query
    List<Object[]> matrix = BooksDAO.getTableStatus();
    for (Object[] objects : matrix) {
      for (Object object : objects) {
        System.out.println(object == null ? "null" : object.toString());
      }
    }

    // shutodwn DB Connection Manager
    YankPoolManager.INSTANCE.release();

  }
}
