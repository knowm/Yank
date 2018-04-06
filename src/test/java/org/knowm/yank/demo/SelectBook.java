package org.knowm.yank.demo;

import java.util.Properties;

import org.knowm.yank.Yank;

/**
 * Selects a single Book from the BOOKS table. Demonstrates using the Yank API without DAOs or properties.
 *
 * @author timmolter
 */
public class SelectBook {

  public static void main(String[] args) {

    // DB Properties
    Properties dbProps = new Properties();
    dbProps.setProperty("jdbcUrl", "jdbc:mysql://localhost:3306/Yank");
    dbProps.setProperty("username", "root");
    dbProps.setProperty("password", "");

    // add connection pool
    Yank.setupDefaultConnectionPool(dbProps);

    // query book
    String sql = "SELECT * FROM BOOKS WHERE TITLE = ?";
    Object[] params = new Object[] { "Cryptonomicon" };
    Book book = Yank.queryBean(sql, Book.class, params);
    System.out.println(book.toString());

    // release connection pool
    Yank.releaseDefaultConnectionPool();

  }
}
