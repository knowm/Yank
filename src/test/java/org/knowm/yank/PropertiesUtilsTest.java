/**
 * Copyright 2015-2016 Knowm Inc. (http://knowm.org) and contributors.
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;
import org.knowm.yank.exceptions.PropertiesFileNotFoundException;

/**
 * @author timmolter
 */
public class PropertiesUtilsTest {

  @Test
  public void testLoadProperties() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB.properties");
    assertThat(dbProps.get("jdbcUrl").toString(), equalTo("jdbc:hsqldb:mem:blah;shutdown=true"));

    Properties sqlProps = PropertiesUtils.getPropertiesFromPath("./src/test/resources/HSQL_SQL.properties");
    assertThat(sqlProps.get("BOOKS_SELECT_BY_TITLE").toString(), equalTo("SELECT * FROM BOOKS WHERE TITLE = ?"));

  }

  @Test
  public void testLoadPropertiesFail() {

    try {
      PropertiesUtils.getPropertiesFromClasspath("HSQL_DB_XYZ.properties");
      fail("PropertiesFileNotFoundException should have been thrown!!!");
    } catch (PropertiesFileNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }
}
