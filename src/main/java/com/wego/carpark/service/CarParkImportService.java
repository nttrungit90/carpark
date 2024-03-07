package com.wego.carpark.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.wego.carpark.dto.CarParkCsvDto;
import com.wego.carpark.mapper.CarParkMapper;
import com.wego.carpark.model.CarPark;
import com.wego.carpark.repository.CarParkRepository;
import com.wego.carpark.utill.GeocodeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


/**
 * CarPark upload related stuff
 */
@Slf4j
@Service
public class CarParkImportService {
    private final CarParkService carParkService;
    private final CarParkMapper carParkMapper;
    private final ResourceLoader resourceLoader;
    private final CarParkRepository carParkRepository;

    @Autowired
    public CarParkImportService(CarParkService carParkService, CarParkMapper carParkMapper,
                                ResourceLoader resourceLoader, CarParkRepository carParkRepository) {
        this.carParkService = carParkService;
        this.carParkMapper = carParkMapper;
        this.resourceLoader = resourceLoader;
        this.carParkRepository = carParkRepository;

    }

    public void importCarParkData() {
        // only import if never import before
        if(carParkRepository.count() > 0) {
            return;
        }

        // Load the CSV file from the resources directory
        Resource resource = resourceLoader.getResource("classpath:" + "HDBCarparkInformation.csv");

        // Get the InputStream from the resource
        List<CarParkCsvDto> carParkCsvDtos = readCarParkCsvDto(resource);

        this.processCarParkCsvDto(carParkCsvDtos);
    }

    public List<CarParkCsvDto> readCarParkCsvDto(InputStreamSource inputStreamSource) {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStreamSource.getInputStream()))) {

            // create csv bean reader
            CsvToBean<CarParkCsvDto> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(CarParkCsvDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            // convert `csvToBean` object to list of CarParkCsvDto
            return csvToBean.parse();

        } catch (IOException e) {
            log.error("Error reading file", e);
            throw new RuntimeException(e);
        }
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
        return new CarParkImportService.Geocoding(geocode[1], geocode[0]);

    }

    @Data
    @AllArgsConstructor
    private static class Geocoding {
        private Double longitude;
        private Double latitude;
    }

}
