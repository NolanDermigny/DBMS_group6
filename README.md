Database - Programming Assignment 2 (PA 2)

Same Group as HW 5
Needs Info on Network Programming Part 
This assignment will have you build a Java or Python application that will interface with your MySQL database.  You will be using your work from homework 3 and 5, regardless of the grade you received on that assignment.  Please reach out to me if you need help updating either of these.

 
Java Resources
There is a pdf attached from SWEN 200 that reviews basic UI capabilities of Java’s Swing libraries.

 
Python Resources
UI Development: https://youtube.com/playlist?list=PLzMcBGfZo4-lB8MZfHPLTEHO9zJDDLpYj
Another python option is tkinter.

 
Database Access
A reminder that our MySQL server is not visible through Ship's firewall.  As such if you need your code to access it off campus there are two options.  The simpler option is to setup an SSH tunnel/port forwarding using a program like MobaXterm.
Overview of SSH Tunneling SSH Tunneling - Local & Remote Port Forwarding (by Example)
How to setup SSH Tunnel in Putty/Moba: DIY | Local Port Forwarding using Putty and MobaXterm
The other option is to look into VPN access to our network (VPN Access for School of Engineering Students - Shippensburg University School of Engineering ). 

Reminder
Lastly, a reminder that Character is a Keyword in MySQL.  So, if you choose that as a name for a table or any other MySQL keywords (not sure if also applies to columns), you'll need to change it.


Git Repository
It is highly recommended, but not required to use a Git repository to manage your code.  

Part I
You will need to create a method or group of methods to build the tables for your database.  
This should just use Connectivity Framework you became familiar with in HW 4 and the SQL statements you created in Part 2 of Homework 3. I strongly encourage groups to get this in place quickly, this will then allow each group member to work on their part of Part III independently if needed. 

 
Part II
You will need to create a method or methods to populate the tables created in Part II with at least 5 values each per table.  Using the Connectivity Framework you became familiar with in HW 4, and your SQL statements from Part 3 of Homework 3 should accomplished most of this.  Depending on your foreign key constraints you may need to remove and then add a constraint back to add in the initial values.
Parts I and II do not need to use the networking component used in HW 4.  The networking component will be needed for the rest of the application.

Part III
Like was done in HW 4 you need to create a message server that will sit between your application (Part IV of this assignment) and the DB server.  You should have 3-4 options to choose from, pick the one that you think will be the easiest to work with.  Part I and II of this assignment do not need to use this and can connect to the DB server directly.

 

For this assignment you will need to use PreparedStatements and one Stored Procedure on the server side. See Zoom video attached to the assignment for how to create and use a Stored Procedure.  So, you can’t just send the SQL command in raw text for this assignment.  In the message you send you’ll need to encode, likely as just text.

 

Suggestions
SQL Command: INSERT
Table: table_name
Columns Affected: col_name1, col_name2, etc…
Data for Each Column: data_col1, data_col2, etc…

 

SQL Command: DELETE,SELECT
Table: table_name
Columns to Base On: col_name1, col_name2, etc…
Comparison Operator: operator1, operator2, etc…
Data for Comparison: data_col1, data_col2, etc…

 

The above approach for Select statements means it selects all columns from the table for each row it pulls.

 

SQL Command: UPDATE
Table: table_name
Column to Update: col_name
Value to Set To: value
Columns to Base On: col_name1, col_name2, etc…
Comparison Operator: operator1, operator2, etc…
Data for Comparison: data_col1, data_col2, etc…

 

Stored Procedure: procedure_name
Data for Each Parameter: data_param1, data_param2, etc…

 

If you are using text choose a symbol to act as a separator.  For example, ‘:’ could work so long as nothing else you send uses that symbol.

 

Examples:
INSERT:EMPLOYEES:NAME:AGE:Bob:34
INSERT INTO EMPLOYEES (NAME,AGE) values (‘Bob’,34)


SELECT:COMPAY:CEO:=:Frank

SELECT FROM COMPANY WHERE CEO=’Frank’


UPDATE:EMPLOYEES:NAME:Joe:NAME:Bob

UPDATE EMPLOYEES SET Name=’Joe’ WHERE Name=’Bob’

 

Ideally everyone in your group will use the same client/server system.  However, this is not required.  Only that your UI does use a client/server system to function. If you do go the group route you need to plan out early how it should function.  Also, keep in mind that changes in such a setup will affect all your group members.

 

Part IV

Implement the User Interfaces designed and reviewed from Homework 5.   It needs to communicate to the DB server using the message server you setup in Part III of this assignment.  If building in Java see D2L for document on using swing to build a simple UI in Java.  If you are working in Python then use tkinter as it comes standard with Python and so will work in our labs.  Each person in the group is expected to do one of the interfaces from Homework 5, you will only be graded on the interface you worked on.

 

Turn-In: D2L->Assignments (Programming Assignment 2) - Will present project during last week of the semester in class.
