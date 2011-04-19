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


    ********************************************************
    *                     DISCLAIMER                       *
    *                                                      *
    * Use Yank AT YOUR OWN RISK. Using this system in      *
    * production may result in data loss, data corruption, *
    * or other serious problems.                           *
    *                                                      *
    ********************************************************