/**
 * Copyright 2011 - 2013 Xeiam LLC.
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
package com.xeiam.yank.demo;

/**
 * A class used to represent rows in the BOOKS table <br>
 * Note: class member naming tip: data type and name must match SQL table!<br>
 * Note: DBUtils uses reflection to match column names to class member names. <br>
 * Class members are matched to columns based on several factors:
 * <ul>
 * <li>set* methods that match the table's cloumn names (i.e. title <--> setTitle()). The name comparison is case insensitive.</li>
 * <li>The columns are matched to the object's class members</li>
 * <li>If the conversion fails (i.e. the property was an int and the column was a Timestamp) an SQLException is thrown.</li>
 * </ul>
 * 
 * @author timmolter
 */
public class Book {

  private String title;
  private String author;
  private double price;

  /** Pro-tip: In Eclipse, generate all getters and setters after defining class fields: Right-click --> Source --> Generate Getters and Setters... */

  public String getTitle() {

    return title;
  }

  public void setTitle(String title) {

    this.title = title;
  }

  public String getAuthor() {

    return author;
  }

  public void setAuthor(String author) {

    this.author = author;
  }

  public double getPrice() {

    return price;
  }

  public void setPrice(double price) {

    this.price = price;
  }

  /** Pro-tip: In Eclipse, generate a toString() method for a class: Right-click --> Source --> Generate toString()... */

  @Override
  public String toString() {

    return "Book [title=" + title + ", author=" + author + ", price=" + price + "]";
  }

}
