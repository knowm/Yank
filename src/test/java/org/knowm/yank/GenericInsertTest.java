package org.knowm.yank;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** Tests the generic {@code insert(..., Class<T> idType)} overloads using an in-memory HSQL DB. */
public class GenericInsertTest {

  private static final String INSERT_SQL =
      "INSERT INTO BOOKS (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";

  @BeforeClass
  public static void setUpDB() {

    Properties dbProps =
        PropertiesUtils.getPropertiesFromClasspath("HSQL_DB_DATA_SRC.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_SQL.properties");

    Yank.setupDefaultConnectionPool(dbProps);
    Yank.addSQLStatements(sqlProps);
    Yank.executeSQLKey("BOOKS_CREATE_TABLE", null);
  }

  @AfterClass
  public static void tearDownDB() {

    Yank.releaseDefaultConnectionPool();
  }

  @Test
  public void testInsertWithNumberClass() {

    Object[] params = {"Dune", "Frank Herbert", 14.99};
    Number id = Yank.insert(INSERT_SQL, params, Number.class);

    assertThat(id, notNullValue());
    assertTrue(id.longValue() >= 0L);
  }

  @Test
  public void testInsertWithNumberClassSecondRow() {

    // Verify a second insert returns an incremented id
    Object[] params1 = {"Foundation", "Isaac Asimov", 12.99};
    Object[] params2 = {"Neuromancer", "William Gibson", 11.99};

    Number id1 = Yank.insert(INSERT_SQL, params1, Number.class);
    Number id2 = Yank.insert(INSERT_SQL, params2, Number.class);

    assertThat(id1, notNullValue());
    assertThat(id2, notNullValue());
    assertTrue(id2.longValue() >= id1.longValue() + 1);
  }

  @Test
  public void testDefaultLongInsertStillWorks() {

    Object[] params = {"1984", "George Orwell", 9.99};
    long id = Yank.insert(INSERT_SQL, params);

    // Default Long path should still work (no breaking change)
    assertTrue(id >= 0L);
  }
}
