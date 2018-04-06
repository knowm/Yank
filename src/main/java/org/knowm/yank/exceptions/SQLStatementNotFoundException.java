package org.knowm.yank.exceptions;

/** An exception to indicate that an SQL statement could not be found for the given key String */
public class SQLStatementNotFoundException extends RuntimeException {

  /** Constructor */
  public SQLStatementNotFoundException() {

    super(
        "The SQL statement could not be found for the given key String. Make sure you have a file called *.properties on the classpath with valid key value pairs!");
  }
}
