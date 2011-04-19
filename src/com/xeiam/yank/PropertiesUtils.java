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
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author timmolter
 */
public class PropertiesUtils extends Properties {

    private static final long serialVersionUID = -7997446570265335696L;

    /** log4j logger */
    static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    /**
     * Loads a Properties file from the classpath matching the given file name
     * 
     * @param pFileName
     * @return
     */
    public static Properties getPropertiesFromClasspath(String pFileName) {

        Properties props = new Properties();
        try {
            props.load(ClassLoader.getSystemResourceAsStream(pFileName));
        } catch (IOException e) {
            logger.error("ERROR LOADING PROPERTIES FROM CLASSPATH!!!", e);
        }
        return props;
    }

    /**
     * Loads a Properties file from the given file name
     * 
     * @param pFileName
     * @return
     */
    public static Properties getPropertiesFromPath(String pFileName) {

        Properties props = new Properties();
        FileInputStream fis;
        try {
            fis = new FileInputStream(pFileName);
            props.load(fis);
            fis.close();
        } catch (Exception e) {
            logger.error("ERROR LOADING PROPERTIES FROM PATH!!!", e);
        }
        return props;
    }

}
