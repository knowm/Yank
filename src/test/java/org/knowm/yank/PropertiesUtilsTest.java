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
