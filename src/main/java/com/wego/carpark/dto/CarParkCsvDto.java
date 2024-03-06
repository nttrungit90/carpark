package com.wego.carpark.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class CarParkCsvDto {

    @CsvBindByName(column = "car_park_no")
    private String carParkNo;

    @CsvBindByName(column = "address")
    private String address;

    @CsvBindByName(column = "x_coord")
    private Double longitude;

    @CsvBindByName(column = "y_coord")
    private Double latitude;

    @CsvBindByName(column = "car_park_type")
    private String carParkType;

    @CsvBindByName(column = "type_of_parking_system")
    private String typeOfParkingSystem;

    @CsvBindByName(column = "short_term_parking")
    private String shortTermParking;

    @CsvBindByName(column = "free_parking")
    private String freeParking;

    @CsvBindByName(column = "night_parking")
    private String nightParking;

    @CsvBindByName(column = "car_park_decks")
    private Integer carParkDecks;

    @CsvBindByName(column = "gantry_height")
    private Double gantryHeight;

    @CsvBindByName(column = "car_park_basement")
    private String carParkBasement;
}
