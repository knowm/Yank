package com.xeiam.yank.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * This gets the long value representing the auto-assigned primary key id from the ResultSet object generated from inserting a row.
 *
 * @author timmolter
 */
public class InsertedIDResultSetHandler implements ResultSetHandler<Long> {

  public Long handle(ResultSet rs) throws SQLException {

    if (rs.next()) {
      return rs.getLong(1);
    }
    return null;
  }

}
