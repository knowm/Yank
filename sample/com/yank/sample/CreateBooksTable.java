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
package com.yank.sample;

import java.util.Properties;

import com.xeiam.yank.DBConnectionManager;

/**
 * Create a table called BOOKS. Demonstrates hardcoding the connection pool properties
 * 
 * @author timmolter
 */
public class CreateBooksTable {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.setProperty("driverclassname", "com.mysql.jdbc.Driver");
        props.setProperty("yank.url", "jdbc:mysql://localhost:3306/Yank");
        props.setProperty("yank.user", "root");
        props.setProperty("yank.password", "");
        props.setProperty("yank.maxconn", "5");

        DBConnectionManager.INSTANCE.init(props, null);

        BooksDAO.createBooksTable();

        DBConnectionManager.INSTANCE.release();

    }
}
