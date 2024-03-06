package com.wego.carpark.model;

public interface NearestAvailableCarPark {
    String getCarParkNo();
    String getAddress();
    Double getLatitude();
    Double getLongitude();
    String getLotType();
    Integer getTotalLot();
    Integer getAvailableLot();
    Double getDistance();
}