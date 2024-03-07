package com.wego.carpark.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.wego.carpark.dto.CarParkCsvDto;
import com.wego.carpark.service.CarParkImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/carparks")
public class CarParkUploadController {
    private final CarParkImportService carParkImportService;

    @Autowired
    public CarParkUploadController(CarParkImportService carParkImportService) {
        this.carParkImportService = carParkImportService;

    }

    @PostMapping("/upload-csv-file")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file) {

        // validate, parse uploaded file
        List<CarParkCsvDto> carParkCsvDtos = processFile(file);

        // File is good, now asynchronously process the file
        CompletableFuture.runAsync(() -> processCarParkCsvDto(carParkCsvDtos));

        // Return a response immediately
        return "File uploaded successfully. Processing in background.";
    }

    /**
     * Method validate, parse uploaded file and convert them to List<CarParkCsvDto>
     * @param file
     * @return List<CarParkCsvDto>
     */
    private List<CarParkCsvDto> processFile(MultipartFile file) {
        return carParkImportService.readCarParkCsvDto(file);
    }

    /**
     * Method to process list carParks, meaning saving them to database here
     * @param carParks
     */
    private void processCarParkCsvDto(List<CarParkCsvDto> carParks) {
        carParkImportService.processCarParkCsvDto(carParks);
    }



}
