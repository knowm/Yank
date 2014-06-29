/**
 * Copyright 2011 - 2014 Xeiam LLC.
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
package com.xeiam.yank.exceptions;

/**
 * An exception to indicate that a Connection could not be obtained for some reason
 * 
 * @author timmolter
 */
public class ConnectionException extends RuntimeException {

  /**
   * Constructor
   */
  public ConnectionException(String poolName) {

    super("Connection was null! There is a Connection problem associated with the connection pool: " + poolName);
  }

}
