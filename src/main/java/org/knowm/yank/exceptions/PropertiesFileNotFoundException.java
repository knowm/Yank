package org.knowm.yank.exceptions;

/**
 * An exception to indicate that a Connection pool could not be found given the Connection pool
 * name.
 */
public class PropertiesFileNotFoundException extends RuntimeException {

  /**
   * Constructor
   *
   * @param message
   * @param cause
   */
  public PropertiesFileNotFoundException(String message, Throwable cause) {

    super(message, cause);
  }
}
