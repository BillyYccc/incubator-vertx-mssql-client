# Google Summer of Code 2019 Summary
**Reactive relational database client**

Student: Billy Yuan  
Mentor: Julien Viet

## Goals

[Project idea](https://wiki.eclipse.org/Google_Summer_of_Code_2019_Ideas#Topic_1:_Reactive_relational_database_client)

Main tasks

1. Read and understand the documentation of TDS protocol. 
2. Implement the SPI of vertx-sql-client for MSSQL.
3. Add proper tests and documentation.
4. Verify the implementation by running tests against existing TCK (Technology Compatibility Kit).
5. Integrate with vertx-sql-client APIs. 

## What’s achieved

I have worked during this summer with my mentor to complete this project, everything is built from scratch and hosted at https://github.com/BillyYccc/incubator-vertx-mssql-client in GitHub. I have accomplished all the expected tasks as they’re planned in the proposal.

### First Evalutation

[Project Management](https://github.com/BillyYccc/incubator-vertx-mssql-client/projects/1)  

Achievements:

* add support for connecting and disconnecting to SQL Server
* add support for basic authentication with SQL Server

### Second Evalution

[Project Management](https://github.com/BillyYccc/incubator-vertx-mssql-client/projects/2)  
 
Achievements:

* add support SQL batch(i.e. Simple query)
* add basic support for value mapping between SQL types and Java types

### Final Evalution

[Project Management](https://github.com/BillyYccc/incubator-vertx-mssql-client/projects/3)

Achievements:

* add support for Prepared queries
* add support for connection pooling
* support for all the data types existed in the TCK of the Reactive SQL Client

## What can be expected in the future

We have built a reactive SQL Server client from scratch based on the SPI of the Reactive SQL Client, we can see the project is taking shape and on track now, the project now provides a reactive access to Microsoft SQL Server databases and we can expect a few contributions in the foreseeable future.

* Improvements for the implementation details: the code can be optimized for simpler logic and better performance
* Integration with the Eclipse Vert.x stack: at a later proper time we would like to move this project to the Reactive SQL Client and integrate with the Eclipse Vert.x stack
* More features: we still have a few improvements such as TLS support to add in this client, and that’s an interesting direction for future work

## Acknowledgements

Firstly I want to give my special ❤️ thanks to Julien who is my Mentor during this project, he has guided and helped me successfully complete this project 🎉, I have learned a lot from the project. Thanks to the all the Vert.x community members and users who have made contributions to this wonderful project.
