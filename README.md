## [![Yank](http://xeiam.com/static/xeiamweb/images/Yank_64_64.png)](http://xeiam.com/yank) Yank
Ultra-Light JDBC Persistance Layer

## Description
Yank is a very easy-to-use yet flexible SQL-centric persistence layer for 
JDBC-compatible databases build on top of org.apache.DBUtils. Yank is a different approach to the over-ORMing of Java persistence. 
Rather than try to abstract away the SQL underneath, Yank assumes you want low level control over the SQL 
queries you execute and provides a nice framework to keep your persistence layer organized. Yank wraps DBUtils, 
hiding the nitty-gritty Connection and ResultSet
handling behind a straight-forward proxy class: DBProxy. "Query" methods
execute SELECT statements and return POJOs or a List of POJOs. "Execute" 
methods execute INSERT, UPDATE, and DELETE (and other) statements. Recently, 
batch executing, column querying and scalar querying has been added.

Usage is very simple: define DB connectivity properties, create a DAO and POJO class, and execute queries.

## Example

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_SQL.properties"); // optional

    DBConnectionManager.INSTANCE.init(dbProps, sqlProps);
    
    BooksDAO.createBooksTable();
    
    Book book = new Book();
    book.setTitle("Cryptonomicon");
    book.setAuthor("Neal Stephenson");
    book.setPrice(23.99);
    BooksDAO.insertBook(book);
   
    List<Book> allBooks = BooksDAO.selectAllBooks();

    book = BooksDAO.selectBook("Cryptonomicon");
    
    DBConnectionManager.INSTANCE.release();

Now go ahead and [study some more examples](http://xeiam.com/yank_examplecode.jsp), [download the thing](http://xeiam.com/yank_changelog.jsp) and [provide feedback](https://github.com/timmolter/Yank/issues).

## Features
* Depends on light-weight and robust DBUtils library
* ~13KB Jar
* Apache 2.0 license
* Batch execute
* Automatic POJO and POJO List querying
* Works with any JDBC-compliant database
* Write your own SQL statements
* Optionally store SQL statements in a Properties file
* Built-in Connection pool

## Getting Started
### Non-Maven
Download Jar: http://xeiam.com/yank_changelog.jsp
#### Dependencies
* commons-dbutils.dbutils-1.5.0
* org.slf4j.slf4j-api-1.6.5
* a JDBC-compliant Connector jar

### Maven
The Yank release artifacts are hosted on Maven Central.

Add the Yank library as a dependency to your pom.xml file:

    <dependency>
        <groupId>com.xeiam</groupId>
        <artifactId>yank</artifactId>
        <version>2.2.0</version>
    </dependency>

For snapshots, add the following to your pom.xml file:

    <repository>
      <id>sonatype-oss-snapshot</id>
      <snapshots/>
      <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
    
    <dependency>
        <groupId>com.xeiam</groupId>
        <artifactId>yank</artifactId>
        <version>2.3.0-SNAPSHOT</version>
    </dependency>

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