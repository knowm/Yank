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

/**
 * An exception to indicate that an SQL statement could not be found for the given key String
 */
public class SQLStatementNotFoundException extends RuntimeException {

  /**
   * Constructor
   */
  public SQLStatementNotFoundException() {

    super(
        "The SQL statement could not be found for the given key String. Make sure you have a file called *.properties on the classpath with valid key value pairs!");
  }

}
