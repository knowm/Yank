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

import java.io.Serializable;

/**
 * This abstract class represents a Bean. A Bean needs to implement Serializable and have getters and setters for all of it's fields. The field names must match the column names (case-insensitive) in the table. Create all field names with all
 * lowercase letters and no underscores or other symbol. Must have a no-arg constructor!!!!
 * 
 * @author timmolter
 */
@SuppressWarnings("serial")
public abstract class Bean implements Serializable {

}
