package com.wego.carpark.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NearestCarParkDto {
    @JsonIgnore
    private String carParkNo;

    private String address;

    private Double latitude;

    private Double longitude;

    @JsonProperty("total_lots")
    private Integer totalLots;

    @JsonProperty("available_lots")
    private Integer availableLots;

    @JsonIgnore
    private Double distance;
}
