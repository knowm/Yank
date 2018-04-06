package org.knowm.yank.exceptions;

import java.sql.SQLException;

/**
 * An exception to wrap a checked SQLException
 */
public class YankSQLException extends RuntimeException {

  private static final String BEAN_EXCEPTION_MESSAGE = "Error converting row to bean!! Make sure you have a default no-args constructor!";
  private static final String QUERY_EXCEPTION_MESSAGE = "Error in SQL query!!!";

  private final SQLException sqlException;

  /**
   * Constructor
   *
   * @param sqlException
   * @param poolName
   * @param sql
   */
  public YankSQLException(SQLException sqlException, String poolName, String sql) {

    super(getSpecialMessage(sqlException) + "; " + sqlException.getMessage() + "; Pool Name= " + poolName + "; SQL= " + sql);
    this.sqlException = sqlException;
  }

  static String getSpecialMessage(SQLException sqlException) {

    String specialMessage = QUERY_EXCEPTION_MESSAGE;
    if (sqlException.getMessage().startsWith("Cannot create")) {
      specialMessage = BEAN_EXCEPTION_MESSAGE;
    }
    return specialMessage;
  }

  public SQLException getSqlException() {
    return sqlException;
  }
}
