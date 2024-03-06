package com.wego.carpark.service;

import com.wego.carpark.dto.CarParkCsvDto;
import com.wego.carpark.mapper.CarParkMapper;
import com.wego.carpark.model.CarPark;
import com.wego.carpark.utill.GeocodeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * CarPark upload related stuff
 */
@Slf4j
@Service
public class CarParkUploadService {
    private final CarParkService carParkService;
    private final CarParkMapper carParkMapper;


    @Autowired
    public CarParkUploadService(CarParkService carParkService, CarParkMapper carParkMapper) {
        this.carParkService = carParkService;
        this.carParkMapper = carParkMapper;

    }

    /**
     * Method to convert List<CarParkCsvDto> to List<CarPark> and save them database
     * @param csvDtos
     */
    public void processCarParkCsvDto(List<CarParkCsvDto> csvDtos) {

        List<CarPark> carParkList = new ArrayList<>();
        csvDtos.forEach(csvDto -> {
            Geocoding geocoding = this.convertGeocodeFromSVY21ToWGS84(csvDto.getLongitude(), csvDto.getLatitude());
            csvDto.setLongitude(geocoding.getLongitude());
            csvDto.setLatitude(geocoding.getLatitude());
            carParkList.add(carParkMapper.carPartCsvDtoToCarPart(csvDto));
        });

        // batch save carpark to database
        carParkService.batchCreate(carParkList);

    }

    /**
     * Method to convert geocode from 3414(SVY21) to 4326(WGS84)
     * @param longitude in SVY21 format
     * @param latitude in SVY21 format
     * @return Geocoding in WGS84 format
     */
    private Geocoding convertGeocodeFromSVY21ToWGS84(Double longitude, Double latitude) {
        double[] geocode = GeocodeConverter.convertSVY21ToWGS84(longitude, latitude);
        return new CarParkUploadService.Geocoding(geocode[1], geocode[0]);

    }

    @Data
    @AllArgsConstructor
    private static class Geocoding {
        private Double longitude;
        private Double latitude;
    }

}
