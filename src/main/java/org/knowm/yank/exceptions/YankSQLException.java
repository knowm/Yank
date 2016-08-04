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
