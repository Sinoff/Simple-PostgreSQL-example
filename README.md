# simple sql example 

This repository includes a simple example of PostgreSQL queries:
Create table, delete table, drop table, 
add data row, update data row, delete data row, select data row.
In addition it shows 2 more complex queries, including join and
recursive selection.


This example is a "nevigation application", using a graph
abstraction where map hops are the edges. It also keeps track
of the amount of users using the app and their current hop.

**************************************************************************************************
## DB Tables
The DB has 2 tables:
1. hops
2. users

### hops: 

keeps data of type "hop" (see hop.java).

source |destination | load_factor |
------ |----------- | ------------|

(source,destination) is the key

### users:

keeps data of type "users" (see user.java).

id | source | destination |
-- |------- | ------------|

id is the key, and (source,destination) must exist in "hops" (foreign keys).

**************************************************************************************************

## Important files in the repository:
1. Solution.java - This file is consisting the queries.
2. DBConnector.java - This file connects the postgreSQL.
3. config.properties	- This file contains postgreSQL configiurations.

Also, under test dir there are different tests.

**IMPORTANT**
Please note that the "Automatic tester" doesn't work 100%,
since we work under the assumption that when deleting a hop in DB
there are no users in that hop, however the testing isn't
compatible with this assumption.
