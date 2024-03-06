package com.wego.carpark.service;

import com.wego.carpark.dto.CarParkCsvDto;
import com.wego.carpark.mapper.CarParkMapper;
import com.wego.carpark.model.CarPark;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    @Value("${onemap.api.url}")
    private String apiUrl;

    @Value("${onemap.access.token}")
    private String accessToken;

    @Autowired
    public CarParkUploadService(CarParkService carParkService, CarParkMapper carParkMapper,
                                   RestTemplate restTemplate, @Qualifier("geoCodeRetryTemplate") RetryTemplate retryTemplate) {
        this.carParkService = carParkService;
        this.carParkMapper = carParkMapper;
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;

    }

    /**
     * Method to convert List<CarParkCsvDto> to List<CarPark> and save them database
     * @param csvDtos
     */
    public void processCarParkCsvDto(List<CarParkCsvDto> csvDtos) {
        List<CarPark> carParkList = new ArrayList<>();
        for(CarParkCsvDto csvDto : csvDtos) {
            Geocoding geocoding = this.convertGeocodeFromSVY21ToWGS84(csvDto.getLongitude(), csvDto.getLatitude());
            if(geocoding != null) {
                csvDto.setLongitude(geocoding.getLongitude());
                csvDto.setLatitude(geocoding.getLatitude());
                carParkList.add(carParkMapper.carPartCsvDtoToCarPart(csvDto));

            } else {
                log.info("Ignore saving CarParkCsvDto because cannot convert geocode {}", csvDto);
            }
        }

        // save carpark to database
        carParkService.batchCreate(carParkList);

    }

    /**
     * Method to convert geocode from 3414(SVY21) to 4326(WGS84)
     * @param longitude in SVY21 format
     * @param latitude in SVY21 format
     * @return Geocoding in WGS84 format
     */
    private Geocoding convertGeocodeFromSVY21ToWGS84(Double longitude, Double latitude) {
        // call external api to convert geocode from 3414(SVY21) to 4326(WGS84)
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken);
            String url = String.format(apiUrl, longitude, latitude);
            HttpEntity<Geocoding> requestEntity = new HttpEntity<>(headers);

            // can be a bottleneck due to API throttling
            RetryCallback<ResponseEntity<Geocoding>, Exception> callback = context -> {
                // Log the retry attempt
                int retryCount = context.getRetryCount();
                if (retryCount > 0) {
                    log.warn("Retry attempt #" + retryCount);
                }

                // Execute the REST API call
                return restTemplate.exchange(url, HttpMethod.GET, requestEntity, Geocoding.class);
            };

            ResponseEntity<Geocoding> response = retryTemplate.execute(callback);
            return response.getBody();


        } catch (Exception e) {
            log.error("Error calling geocode api", e);
        }

        return null;

    }


    @Data
    @AllArgsConstructor
    private static class Geocoding {
        private Double longitude;
        private Double latitude;
    }

}
