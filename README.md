Description
===============

Yank is a very easy-to-use yet flexible Java persistence layer for 
JDBC-compatible databases build on top of org.apache.DBUtils 
(http://commons.apache.org/dbutils/). DBUtils is small, fast, and
transparent API designed to make working with JDBC databases easier.
Yank wraps DBUtils, hiding the nitty-gritty connection and resultset
details behind a straight-forward proxy class: DBProxy. Query methods
execute SELECT statements and return a List of Bean objects. Execute 
methods execute INSERT, UPDATE, and DELETE (and more) statements. 
Database connections are pulled out of DBConnectionPool which is 
created and managed by DBConnenctionManager. 

Getting Started
===============

Non-Maven
---------
Download Jar: http://xeiam.com/yank.jsp

Maven
-----
The Yank artifacts are currently hosted on the Xeiam Nexus repository here:

    <repositories>
      <repository>
        <id>xchange-release</id>
        <releases/>
        <url>http://nexus.xeiam.com/content/repositories/releases</url>
      </repository>
      <repository>
        <id>xchange-snapshot</id>
        <snapshots/>
        <url>http://nexus.xeiam.com/content/repositories/snapshots/</url>
      </repository>
    </repositories>
  
Add this to dependencies in pom.xml:

    <dependency>
      <groupId>com.xeiam</groupId>
      <artifactId>yank</artifactId>
      <version>1.1.2</version>
    </dependency>

Building
===============
mvn clean package  
mvn javadoc:javadoc  