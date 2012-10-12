/**
 * Copyright 2011 Xeiam LLC.
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
package com.xeiam.yank;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author timmolter
 */
public class PropertiesUtils extends Properties {

  static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

  /**
   * Loads a Properties file from the classpath matching the given file name
   * 
   * @param fileName
   * @return
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
    } catch (IOException e) {
      logger.error("ERROR LOADING PROPERTIES FROM CLASSPATH!!!", e);
    } catch (NullPointerException e) {
      logger.error("ERROR LOADING PROPERTIES FROM CLASSPATH!!!", e);
    }
    return props;
  }

  /**
   * Loads a Properties file from the given file name
   * 
   * @param fileName
   * @return
   */
  public static Properties getPropertiesFromPath(String fileName) {

    Properties props = new Properties();
    FileInputStream fis;
    try {
      fis = new FileInputStream(fileName);
      props.load(fis);
      fis.close();
    } catch (Exception e) {
      logger.error("ERROR LOADING PROPERTIES FROM PATH!!!", e);
    }
    return props;
  }

}
