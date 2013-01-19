## Yank
Ultra-Lightweight JDBC Persistance Layer

## Description
Yank is a very easy-to-use yet flexible Java persistence layer for 
JDBC-compatible databases build on top of org.apache.DBUtils 
(http://commons.apache.org/dbutils/). Yank wraps DBUtils, hiding the nitty-gritty Connection and ResultSet
details behind a straight-forward proxy class: DBProxy. "Query" methods
execute SELECT statements and return a List of POJOs. "Execute" 
methods execute INSERT, UPDATE, and DELETE (and more) statements.  

Usage is very simple: define DB connectivity properties, create a DAO and POJO class, and execute queries.

## Example

    Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_DB.properties");
    Properties sqlProps = PropertiesUtils.getPropertiesFromClasspath("HSQL_SQL.properties");

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
        <version>2.0.0</version>
    </dependency>

For snapshots, add the following to your pom.xml file:

    <repository>
      <id>yank-snapshot</id>
      <snapshots/>
      <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
    
    <dependency>
        <groupId>com.xeiam</groupId>
        <artifactId>yank</artifactId>
        <version>2.0.1-SNAPSHOT</version>
    </dependency>

## Building
mvn clean package  
mvn javadoc:javadoc  

## Bugs
Please report any bugs or submit feature requests to [Yank's Github issue tracker](https://github.com/timmolter/Yank/issues).  

## More Info
Sonar Code Quality: http://sonar.xeiam.com/  
Jenkins CI: http://ci.xeiam.com/  

## Donations
17dQktcAmU4urXz7tGk2sbuiCqykm3WLs6  