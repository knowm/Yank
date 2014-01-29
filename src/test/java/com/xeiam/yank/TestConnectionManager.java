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
package com.xeiam.yank;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xeiam.yank.DBConnectionManager;

/**
 * @author timmolter
 */
public class TestConnectionManager {

  @BeforeClass
  public static void setUpDB() {

    // DB Properties
    Properties dbProps = new Properties();
    dbProps.setProperty("driverclassname", "org.hsqldb.jdbcDriver");
    dbProps.setProperty("myconnectionpoolname.url", "jdbc:hsqldb:mem:aname;shutdown=true");
    dbProps.setProperty("myconnectionpoolname.user", "root");
    dbProps.setProperty("myconnectionpoolname.password", "");
    dbProps.setProperty("myconnectionpoolname.maxconn", "3");

    DBConnectionManager.INSTANCE.init(dbProps);
  }

  @AfterClass
  public static void tearDownDB() {

    DBConnectionManager.INSTANCE.release();
  }

  @Test
  public void testNonExistantPool() {

    Connection con = DBConnectionManager.INSTANCE.getConnection("myfalseconnectionpoolname");
    assertNull(con);
  }

  @Test
  public void testExistantPool() {

    Connection con = DBConnectionManager.INSTANCE.getConnection("myconnectionpoolname");
    assertNotNull(con);
  }

  @Test
  public void testPool1() {

    Connection con = DBConnectionManager.INSTANCE.getConnection("myconnectionpoolname");
    assertNotNull(con);
    con = DBConnectionManager.INSTANCE.getConnection("myconnectionpoolname");
    assertNotNull(con);
    con = DBConnectionManager.INSTANCE.getConnection("myconnectionpoolname");
    assertNotNull(con);
    con = DBConnectionManager.INSTANCE.getConnection("myconnectionpoolname");
    assertNull(con);
    DBConnectionManager.INSTANCE.release();
  }

  @Test
  public void testPool2() {

    Connection con = DBConnectionManager.INSTANCE.getConnection("myconnectionpoolname");
    assertNotNull(con);
    DBConnectionManager.INSTANCE.release();
    con = DBConnectionManager.INSTANCE.getConnection("myconnectionpoolname");
    assertNotNull(con);
    con = DBConnectionManager.INSTANCE.getConnection("myconnectionpoolname");
    assertNotNull(con);
    con = DBConnectionManager.INSTANCE.getConnection("myconnectionpoolname");
    assertNotNull(con);
    DBConnectionManager.INSTANCE.release();
  }
}
