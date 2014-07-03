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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;

import org.junit.Test;

import com.xeiam.yank.PropertiesUtils;

/**
 * @author timmolter
 */
public class TestPropertiesUtils {

  @Test
  public void testLoadProperties() {

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB.properties");
    assertThat(dbProps.get("driverclassname").toString(), equalTo("org.hsqldb.jdbcDriver"));

    Properties sqlProps = PropertiesUtils.getPropertiesFromPath("./src/test/resources/HSQL_SQL.properties");
    assertThat(sqlProps.get("BOOKS_SELECT_BY_TITLE").toString(), equalTo("SELECT * FROM BOOKS WHERE TITLE = ?"));

  }
}
