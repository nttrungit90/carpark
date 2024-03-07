package com.wego.carpark.dto;

import lombok.Data;

@Data
public class NearestCarParkDto {
    private String carParkNo;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer totalLots;
    private Integer availableLots;
    private Double distance;
}
