## [![Yank](https://raw.githubusercontent.com/timmolter/Yank/devlop/etc/Yank_64_64.png)](http://xeiam.com/yank) Yank
Ultra-Light JDBC Persistance Layer

## In a Nutshell

Never deal with the monotony and pitfalls of handling JDBC ResultSets and Connections again. Yank provides a dead-simple API for saving and yanking Java objects into and out of databases.

## Long Description
Yank is a very easy-to-use yet flexible SQL-centric persistence layer for JDBC-compatible databases build on top of org.apache.DBUtils. Yank is a different approach to the over-ORMing of Java persistence.
Rather than try to abstract away the SQL underneath, Yank assumes you want low level control over the SQL queries you execute. Yank wraps DBUtils,
hiding the nitty-gritty Connection and ResultSet handling behind a straight-forward proxy class: `Yank`. "Query" methods execute SELECT statements and return POJOs or a List of POJOs. "Execute"
methods execute INSERT, UPDATE, and DELETE (and other) statements. Recently, batch executing, column list querying and scalar querying has been added. Since version 3.0.0, Yank uses the
[Hikari connection pool](https://github.com/brettwooldridge/HikariCP) for managing JDBC datasources and connection pools.

## Features

 * [x] Apache 2.0 license
 * [x] ~10KB Jar
 * [x] Uses Apache DBUtils for JDBC
 * [x] Uses HikariCP for connection pooling
 * [x] Easy prepared statements
 * [x] Java object data sinking
 * [x] Java object and object List querying
 * [x] Batch execute
 * [x] Works with any JDBC-compliant database
 * [x] Write your own SQL statements
 * [x] Optionally store connection pool properties in a Properties file
 * [x] Optionally store SQL statements in a Properties file
 * [x] Java 5 and up

## Basic Example

```java
public static void main(String[] args) {

  // Connection Pool Properties
  Properties dbProps = new Properties();
  dbProps.setProperty("jdbcUrl", "jdbc:mysql://localhost:3306/Yank");
  dbProps.setProperty("username", "root");
  dbProps.setProperty("password", "");

  // add connection pool
  Yank.addConnectionPool("myconnectionpoolname", dbProps);

  // query book
  String sql = "SELECT * FROM BOOKS WHERE TITLE = ?";
  Object[] params = new Object[] { "Cryptonomicon" };
  Book book = Yank.querySingleObjectSQL("myconnectionpoolname", sql, Book.class, params);
  System.out.println(book.toString());

  // release connection pool
  Yank.release();
}
```
Note that the String `myconnectionpoolname` is used as a key for the connection pool to use as the first argument in the `Yank.*` SQL methods. With Yank, you can run multiple pools, each connected to different databases and/or database types (MySQL, Oracle, etc.), all from the same app.

## Hide Those Properties Away!

```java
// Connection Pool Properties
Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_DB.properties");

// add connection pool
Yank.addConnectionPool("myconnectionpoolname", dbProps);
```
Why? Hardcoding properties is fine for something quick and dirty, but loading them from a file is generally more convenient and flexible. BTW, you can load them from a path too with: `PropertiesUtils.getPropertiesFromPath(String fileName)`.

## Hide Those SQL Statements Away!

```java
// SQL Statements in Properties file
Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("MYSQL_SQL.properties");
Yank.addSQLStatements(sqlProps);
// ...
String sqlKey = "BOOKS_CREATE_TABLE";
Yank.executeSQLKey("myconnectionpoolname", sqlKey, null);
```
Why? Sometimes it's nice to have all your SQL statements in one place. As an example see: [MYSQL_SQL.properties](https://github.com/timmolter/Yank/blob/develop/src/test/resources/MYSQL_SQL.properties). Also this allows you to swap databases easily without changing any code. Keep one for database type `X` and one for database type `Y`. BTW, to access the actual statements in the  properties file, you use the `Yank.*SQLKey(...)` methods in `Yank`. You can also add multiple properties files and they will be merged!

## Organize Your Persistence Layer Code
```java
public class Book {

  private String title;
  private String author;
  private double price;

  // getters and setters
}
```
```java

public class BooksDAO {

  public static int insertBook(Book book) {

    Object[] params = new Object[] { book.getTitle(), book.getAuthor(), book.getPrice() };
    String SQL = "INSERT INTO BOOKS  (TITLE, AUTHOR, PRICE) VALUES (?, ?, ?)";
    return Yank.executeSQL("myconnectionpoolname", SQL, params);
  }

  // ...

  public static List<Book> selectAllBooks() {

    String SQL = "SELECT * FROM BOOKS";
    return Yank.queryObjectListSQL("myconnectionpoolname", SQL, Book.class, null);
  }
}
```
Why? By creating a DAO class and putting all methods related to a single database table in it, you have a single point of access to that table. In this example the **BooksDAO** corresponds to a table called **Books**, which contains rows of **Book** objects. BTW, the automatic mapping from database row to Java objects happens because the object's field names match exactly the table column names. This is the one constraint you need to follow.

## Summary

Whether or not your app is a tiny scipt, a large webapp, or anything in between the main pattern to follow is the same:

1. Configure a connection pool: `Yank.addConnectionPool("myconnectionpoolname", dbProps);`
1. Use Yank once or many times: `Yank.executeSQL("myconnectionpoolname", SQL, params);`
1. Release the connection pool: ` Yank.release();`

For an example of Yank in action in a `DropWizard` web application see [XDropWizard](https://github.com/timmolter/XDropWizard).

Now go ahead and [study some more examples](http://xeiam.com/yank-example-code), [download the thing](http://xeiam.com/yank-change-log) and [provide feedback](https://github.com/timmolter/Yank/issues).

## Missing Features

 * [x] Transactions
 * [x] Custom table column to POJO property mapping
 * [x] SQLExceptions (SQL Exceptions are internally caught and logged.)

The above missing features were deliberately excluded to keep the library as simple as possible. For many cases, they are not necessary. If you need those features, check out these projects similar to Yank: [sql2o](http://www.sql2o.org/) and [JDBI](http://jdbi.org/).

## Getting Started
### Non-Maven
Download Jar: http://xeiam.com/yank-change-log
#### Dependencies
* commons-dbutils.dbutils-1.5.0
* org.slf4j.slf4j-api-1.7.7
* a JDBC-compliant Connector jar

### Maven
The Yank release artifacts are hosted on Maven Central.

Add the Yank library as a dependency to your pom.xml file:
```xml
<dependency>
    <groupId>com.xeiam</groupId>
    <artifactId>yank</artifactId>
    <version>2.3.0</version>
</dependency>
```
For snapshots, add the following to your pom.xml file:
```xml
<repository>
  <id>sonatype-oss-snapshot</id>
  <snapshots/>
  <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>

<dependency>
    <groupId>com.xeiam</groupId>
    <artifactId>yank</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```
## Building

#### general

    mvn clean package  
    mvn javadoc:javadoc  

#### maven-license-plugin

    mvn license:check
    mvn license:format
    mvn license:remove

## Bugs
Please report any bugs or submit feature requests to [Yank's Github issue tracker](https://github.com/timmolter/Yank/issues).  

## Continuous Integration
[![Build Status](https://travis-ci.org/timmolter/Yank.png?branch=develop)](https://travis-ci.org/timmolter/Yank.png)  
[Build History](https://travis-ci.org/timmolter/Yank/builds)

## Donations
15MvtM8e3bzepmZ5vTe8cHvrEZg6eDzw2w  
