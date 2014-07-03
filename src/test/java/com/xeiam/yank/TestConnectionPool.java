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
package com.xeiam.yank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

/**
 * @author timmolter
 */
public class TestConnectionPool {

  @Test
  public void testReleaseAndMaxConnections() {

    DBConnectionPool pool = new DBConnectionPool("jdbc:hsqldb:mem:aname;shutdown=true", "root", "", 3);
    assertNotNull(pool.getConnection());
    assertEquals(pool.getCheckedOut(), 1);
    assertNotNull(pool.getConnection());
    assertNotNull(pool.getConnection());
    assertNull(pool.getConnection());
    pool.release();
    assertNotNull(pool.getConnection());
  }

  @Test
  public void testGetCheckedOutConnections() {

    DBConnectionPool pool = new DBConnectionPool("jdbc:hsqldb:mem:aname;shutdown=true", "root", "", 3);

    // get
    Connection con1 = pool.getConnection();
    assertNotNull(con1);
    assertEquals(1, pool.getCheckedOut());
    Connection con2 = pool.getConnection();
    assertNotNull(con2);
    assertEquals(2, pool.getCheckedOut());
    Connection con3 = pool.getConnection();
    assertNotNull(con3);
    assertEquals(3, pool.getCheckedOut());
    Connection con4 = pool.getConnection(); // will be null since pool is full
    assertNull(con4);
    assertEquals(3, pool.getCheckedOut());

    // free to pool
    pool.freeConnection(con1);
    assertEquals(2, pool.getCheckedOut());
    pool.freeConnection(con2);
    assertEquals(1, pool.getCheckedOut());
    pool.freeConnection(con3);
    assertEquals(0, pool.getCheckedOut());
    pool.freeConnection(con4);
    assertEquals(0, pool.getCheckedOut());
    pool.freeConnection(null);
    assertEquals(0, pool.getCheckedOut());

    // get
    con1 = pool.getConnection();
    assertNotNull(con1);
    assertEquals(1, pool.getCheckedOut());

    pool.release();
    assertEquals(0, pool.getCheckedOut());
  }

  @Test
  public void testGetCheckedOutConnections2() throws SQLException {

    DBConnectionPool pool = new DBConnectionPool("jdbc:hsqldb:mem:aname;shutdown=true", "root", "", 3);

    // add
    Connection con1 = pool.getConnection();
    assertNotNull(con1);
    assertEquals(1, pool.getCheckedOut());
    con1.close();
    pool.freeConnection(con1);
    assertEquals(0, pool.getCheckedOut());

    con1 = pool.getConnection();
    assertFalse(con1.isClosed()); // even though we closed the connection and put it pack in the pool, we get a new non-closed connection
  }

}
