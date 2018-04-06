package org.knowm.yank;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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
    //    System.out.println("numInserted: " + numInserted);
    assertThat(numInserted, equalTo(1));

    SQL = "SELECT * FROM TEST0";
    List<Test0Bean> testBeans = Yank.queryBeanList(SQL, Test0Bean.class, null);
    //    System.out.println(testBeans.get(0).toString());
    assertThat(testBeans.get(0).getSpotfix_id(), equalTo(87L));

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
