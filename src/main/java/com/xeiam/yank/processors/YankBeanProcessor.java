package com.xeiam.yank.processors;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.BeanProcessor;

import com.xeiam.yank.annotations.Column;

/**
 * Combines the orverride map of BeanProcessor with the snake case mapping of GenererousBeanProcessor.
 *
 * @author timmolter
 */
public class YankBeanProcessor<T> extends BeanProcessor {

  /**
   * ResultSet column to bean property name overrides.
   */
  private final Map<String, String> columnToFieldOverrides;

  /**
   * Constructor for YankBeanProcessor configured with Bean class type to look for "Column" annotations for column ==> field mapping
   *
   * @param type The Bean type
   */
  public YankBeanProcessor(Class<T> type) {
    super();
    this.columnToFieldOverrides = getMapping(type);
  }

  private Map<String, String> getMapping(Class<T> type) {

    final Map<String, String> columnToPropertyOverrides = new HashMap<String, String>();

    for (Field field : type.getDeclaredFields()) {
      if (field.isAnnotationPresent(Column.class)) {
        columnToPropertyOverrides.put(field.getAnnotation(Column.class).value(), field.getName());
      }
    }

    return columnToPropertyOverrides;
  }

  /**
   * The positions in the returned array represent column numbers. The values stored at each position represent the index in the
   * <code>PropertyDescriptor[]</code> for the bean property that matches the column name. Also tried to match snake case column names or overrides.
   * If no bean property was found for a column, the position is set to <code>PROPERTY_NOT_FOUND</code>.
   *
   * @param rsmd The <code>ResultSetMetaData</code> containing column information.
   * @param props The bean property descriptors.
   * @throws SQLException if a database access error occurs
   * @return An int[] with column index to property index mappings. The 0th element is meaningless because JDBC column indexing starts at 1.
   */
  @Override
  protected int[] mapColumnsToProperties(final ResultSetMetaData rsmd, final PropertyDescriptor[] props) throws SQLException {

    final int cols = rsmd.getColumnCount();
    final int[] columnToProperty = new int[cols + 1];
    Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

    for (int col = 1; col <= cols; col++) {
      String columnName = rsmd.getColumnLabel(col);

      if (null == columnName || 0 == columnName.length()) {
        columnName = rsmd.getColumnName(col);
      }

      String overrideName = columnToFieldOverrides.get(columnName);
      if (overrideName == null) {
        overrideName = columnName;
      }

      final String generousColumnName = columnName.replace("_", "");

      for (int i = 0; i < props.length; i++) {
        final String propName = props[i].getName();

        // see if either the column name, or the generous one matches
        if (columnName.equalsIgnoreCase(propName) || generousColumnName.equalsIgnoreCase(propName) || overrideName.equalsIgnoreCase(propName)) {
          columnToProperty[col] = i;
          break;
        }
      }
    }

    return columnToProperty;
  }

}
