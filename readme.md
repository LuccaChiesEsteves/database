# Database exercise
This project was made using java 20 and Springboot

# Requirements
To run this project you will need to have Intellij or some other editor with lombok plugin installed and JDK 20 on your machine

# HOW TO RUN
To run the application you just need to run the DatabaseApplication.java class

# ENDPOINTS
# GET AN ENTRY (USE -1 TO GET ALL ENTRIES)
GET /entries/-1 HTTP/1.1
Host: localhost:8080

# INSERT AN ENTRY
POST /entries HTTP/1.1
Host: localhost:8080
Content-Type: text/plain
Content-Length: 4

test

# UPDATE AN ENTRY
PUT /entries/1 HTTP/1.1
Host: localhost:8080
Content-Type: text/plain
Content-Length: 8

newValue

# DELETE AN ENTRY
DELETE /entries/1 HTTP/1.1
Host: localhost:8080

