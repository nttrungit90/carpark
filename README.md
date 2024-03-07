# Car Park
Tech stack:
- Java 17
- Spring boot 3.2.3 (Spring Data JPA: For Hibernate ORM, Spring Web: For our web application / rest api)
- Mysql 8
- Docker compose
- Maven
- Flyway: Version control for your database so you can migrate from any version
- locationtech.proj4j for converting geocode

# Project structure: Simple layered architecture
- Persistence Layer: CarPark, CarParkAvailability, CarParkRepository, CarParkAvailabilityRepository
- Business layer: CarParkService, CarParkImportService, CarParkAvailabilityUpdater (scheduled task run every 1 mins to update carpark availability)
- Presentation Layer: CarParkController, CarParkUploadController

# Database design
![Screenshot from 2024-03-07 23-22-54](https://github.com/nttrungit90/carpark/assets/9838628/4407f84c-d2ac-47bf-941f-d26cda8677a5)

# API:
- GET api/carparks/nearest?longitude=103.85412&latitude=1.30106&page=1&per_page=10: Return available car park order by distance
  ![Screenshot from 2024-03-07 13-54-05](https://github.com/nttrungit90/carpark/assets/9838628/26827708-358d-4f18-bd4d-1d727a2d3087)

# Scheduler job to update carpark availability
The class is CarParkAvailabilityUpdater run every 1 minutes (can be updated via property "fixed.delay.milliseconds" in application.properties

# How to build and run
- Need to have maven, java version 17 and docker installed
- Compiles the project source code, runs tests and packages the project into a JAR: **_mvn clean install_**
- Run with docker compose: **_docker-compose up_**
- Wait 1 minutes for CarParkAvailabilityUpdater to import carpark data from file **_resource/HDBCarparkInformation.csv_** and pull for carpark availability from _**API Endpoint: https://api.data.gov.sg/v1/transport/carpark-availability**_
- Test the application: http://localhost:8090/api/carparks/nearest?longitude=103.85412&latitude=1.30106&page=1&per_page=10

# Scalability and Availability
- Storage: Currently, there are 2223 CarParks and 2333 CarParkAvailabilities. The data is limited for now. Even when data from more provinces/countries are included, it is still expected to be less than 1 million.
- **TPS = 100,000 * 5 /24*60*60 = 5.78** not much (Assume that we have 100,000 users, each call our API 5 times / day) 
- To handle increased user traffic we can add more application servers and use load balancing for distributing traffic across multiple servers. That also improves availability.
- Database scalability look is not a problem here, we can add more read replica since we mostly support queries, not much writes, we can shard data based on some criteria such as geographic location (e.g., provinces, countries)

# Issues and consideration during design and development
- Choosing Sql or NoSql database? Finally decided to use Sql database based on below considerations and assumptions
  - Data has a well-defined schema and structured format
  - Application requires complex queries involving joins now or maybe in the future
  - Scalability: Data set is small for now, even when data from more provinces/countries are included, it is still expected to be less than 1 million. We can sharding data based on some criteria such as geographic location (e.g., provinces, countries)

- When importing HDBCarparkInformation.csv, I initially called an external API to convert geocodes from 3414 (SVY21) to 4326 (WGS84). However, since the API required an access token, I registered for an account and obtained one. Unfortunately, this API is both slow and limited to 250 calls per period, significantly extending the import process (approximately 10 minutes). Fortunately, I discovered the locationtech.proj4j library, which allows direct import and use, reducing the import time to a few seconds.

- The original scheduler job designed to update carpark availability in the database one by one was time-consuming. To address this issue, I implemented a batch update approach, which significantly reduced the running time.

- How to calculate distance? Using Haversine formula with code in sql query or using mysql built-in spatial functions https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-distance-sphere. To make it simple and not coupled to mysql I implemented Haversine formula with in query code. We need to do test to verify the correctness or performance between 2 solutions.

- MySQL Health check command ["CMD", "mysqladmin" ,"ping", "-h", "localhost"], which make carpark app startup failed. After hours search for solution, finally use **atkrad/wait4x** and command **tcp mysqldb:3306 -t 300s -i 250ms**to check mysql connection 

# Further possible improvements
- Consider using Spring ReST Docs to generate API documentation.
- It's worth testing if using built-in MySQL spatial functions for distance queries is more efficient.
- Even though data is updated every minute and input geocodes vary greatly for each query, is it possible to implement caching to improve performance?
