/**
 * Copyright 2015 Knowm Inc. (http://knowm.org) and contributors.
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
package org.knowm.yank;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.knowm.yank.exceptions.PropertiesFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A convenience class used to load Properties files
 *
 * @author timmolter
 */
public class PropertiesUtils extends Properties {

  private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

  /**
   * private Constructor to prevent instantiation
   */
  private PropertiesUtils() {

  }

  /**
   * Loads a Properties file from the classpath matching the given file name
   *
   * @param fileName
   * @return The Properties file
   * @throws PropertiesFileNotFoundException if the Properties file could not be loaded from the classpath
   */
  public static Properties getPropertiesFromClasspath(String fileName) {

    Properties props = new Properties();
    try {
      InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
      if (is == null) { // try this instead
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        logger.debug("loaded properties file with Thread.currentThread()");
      }
      props.load(is);
    } catch (Exception e) {
      throw new PropertiesFileNotFoundException("ERROR LOADING PROPERTIES FROM CLASSPATH >" + fileName + "< !!!", e);
    }
    return props;
  }

  /**
   * Loads a Properties file from the given file name
   *
   * @param fileName
   * @return The Properties file
   * @throws PropertiesFileNotFoundException if the Properties file could not be loaded from the given path and file name
   */
  public static Properties getPropertiesFromPath(String fileName) {

    Properties props = new Properties();
    FileInputStream fis;
    try {
      fis = new FileInputStream(fileName);
      props.load(fis);
      fis.close();
    } catch (Exception e) {
      throw new PropertiesFileNotFoundException("ERROR LOADING PROPERTIES FROM PATH >" + fileName + "< !!!", e);
    }
    return props;
  }

}
