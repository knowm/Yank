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
 * Create a table called BOOKS. Demonstrates getting the connection pool properties from a *.properties file.
 * 
 * @author timmolter
 */
public class CreateBooksTableWithPropsFile {

  public static void main(String[] args) {

    // DB.properties file on classpath
    Properties props = PropertiesUtils.getPropertiesFromClasspath("DB.properties");

    // DB.properties file using path to file
    // Properties props = PropertiesUtils.getPropertiesFromPath("/path/to/DB.properties");

    DBConnectionManager.INSTANCE.init(props);

    BooksDAO.createBooksTable();

    DBConnectionManager.INSTANCE.release();

  }
}
