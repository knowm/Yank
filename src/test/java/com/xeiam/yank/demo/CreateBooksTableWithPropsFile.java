/**
 * Copyright 2011 - 2014 Xeiam LLC.
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

import com.xeiam.yank.DBConnectionManager;
import com.xeiam.yank.PropertiesUtils;

/**
 * Create a table called BOOKS. Demonstrates getting the connection pool properties from a *.properties file.
 * 
 * @author timmolter
 */
public class CreateBooksTableWithPropsFile {

  public static void main(String[] args) {

    // MYSQL_DB.properties file on classpath
    Properties props = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

    // Alternative method: MYSQL_DB.properties file using path to file
    // Properties props = PropertiesUtils.getPropertiesFromPath("/path/to/MYSQL_MYSQL_DB.properties");

    // SQL Statements in Properties file
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_SQL.properties");

    // init DB Connection Manager
    DBConnectionManager.INSTANCE.init(props, sqlProps);

    // query
    BooksDAO.createBooksTable();

    // shutodwn DB Connection Manager
    DBConnectionManager.INSTANCE.release();

  }
}
