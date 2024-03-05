# Car Park
Tech stack:
- Spring boot
- Mysql
- Docker compose
- Maven

Init CarPark project using https://start.spring.io/ and add necessary dependencies 
- Spring Web: Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container.
- Spring Data JPA: Persist data in SQL stores with Java Persistence API using Spring Data and Hibernate.
- MySQL Driver
- Spring Boot Actuator: Supports built in (or custom) endpoints that let you monitor and manage your application - such as application health, metrics, sessions, etc.
- Lombok: Java annotation library which helps to reduce boilerplate code.

Create Dockerfile for car park app and create docker-compose.xml file for carpark app && mysql

Use this Maven Wrapper plugin to make auto installation in a simple Spring Boot project. (https://www.baeldung.com/maven-wrapper)
- mvn -N wrapper:wrapper