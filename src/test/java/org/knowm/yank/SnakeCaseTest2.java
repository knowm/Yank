/**
 * Copyright 2015 Knowm Inc. (http://knowm.org) and contributors.
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
package org.knowm.yank;

import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author timmolter
 */
public class SnakeCaseTest2 {
  @BeforeClass
  public static void setUpDB() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB.properties");

    Yank.setupDefaultConnectionPool(dbProps);
  }

  @AfterClass
  public static void tearDownDB() {

    Yank.releaseDefaultConnectionPool();
  }

  @Test
  public void test0() {

    String sql = "CREATE TABLE TEST0 (spotfix_id BIGINT NULL)";
    Yank.execute(sql, null);

    Test0Bean test0Bean = new Test0Bean();
    test0Bean.setSpotfix_id(87L);
    Object[] params = new Object[] { test0Bean.getSpotfix_id() };
    String SQL = "INSERT INTO TEST0 (spotfix_id) VALUES (?)";
    int numInserted = Yank.execute(SQL, params);
    System.out.println("numInserted: " + numInserted);

    SQL = "SELECT * FROM TEST0";
    List<Test0Bean> testBeans = Yank.queryBeanList(SQL, Test0Bean.class, null);
    System.out.println(testBeans.get(0).toString());
  }

  public static class Test0Bean {

    private long spotfix_id;

    /**
     * @return the spotfix_id
     */
    public long getSpotfix_id() {
      return spotfix_id;
    }

    /**
     * @param spotfix_id the spotfix_id to set
     */
    public void setSpotfix_id(long spotfix_id) {
      this.spotfix_id = spotfix_id;
    }

    @Override
    public String toString() {
      return "Test0Bean [spotfix_id=" + spotfix_id + "]";
    }

  }

}
