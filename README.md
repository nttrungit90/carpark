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
- Business layer: CarParkService, CarParkUploadService, CarParkAvailabilityUpdater (scheduled task run every 1 mins to update carpark availability)
- Presentation Layer: CarParkController, CarParkUploadController

# API:
- GET api/carparks/nearest?longitude=103.85412&latitude=1.30106&page=1&per_page=10: Return available car park order by distance
  ![Screenshot from 2024-03-07 13-54-05](https://github.com/nttrungit90/carpark/assets/9838628/26827708-358d-4f18-bd4d-1d727a2d3087)

- POST /api/carparks/upload-csv-file: Import CarPark from csv file
  ![Screenshot from 2024-03-07 12-55-51](https://github.com/nttrungit90/carpark/assets/9838628/f820f8d6-0478-4890-8a08-e0b01e1ef67a)

# Scheduler job to update carpark availability
The class is CarParkAvailabilityUpdater run every 1 minutes (can be updated via property "fixed.delay.milliseconds" in application.properties

# How to build and run
- Need to have maven and java version 17 installed
- Build using maven: mvn clean install
- Run with docker compose: **_docker-compose up_**
- Fill car_park table with data from file /resourse/HDBCarparkInformation.csv by using API **_POST /api/carparks/upload-csv-file_** above (just need to run 1 time)
- Wait 1 minutes for CarParkAvailabilityUpdater to pull for carpark availability from _**API Endpoint: https://api.data.gov.sg/v1/transport/carpark-availability**_
- Test the application: http://localhost:8090/api/carparks/nearest?longitude=103.85412&latitude=1.30106&page=1&per_page=10
  
# Issue faced during development
- When importing HDBCarparkInformation.csv, I initially called an external API to convert geocodes from 3414 (SVY21) to 4326 (WGS84). However, since the API required an access token, I registered for an account and obtained one. Unfortunately, this API is both slow and limited to 250 calls per period, significantly extending the import process (approximately 10 minutes). Fortunately, I discovered the locationtech.proj4j library, which allows direct import and use, reducing the import time to a few seconds.
- The original scheduler job designed to update carpark availability in the database one by one was time-consuming. To address this issue, I implemented a batch update approach, which significantly reduced the running time.
- How to calculate distance? Using Haversine formula with in business code or using mysql built-in spatial functions https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-distance-sphere. To make it simple and not coupled to mysql I implement Haversine formula with in business code. We need to do test to verify the correctness or performance between 2 solutions.
- MySQL Health check command ["CMD", "mysqladmin" ,"ping", "-h", "localhost"], which make carpark app startup failed. After hours search for solution, finally use **atkrad/wait4x** and command **tcp mysqldb:3306 -t 300s -i 250ms **to check mysql connection 

