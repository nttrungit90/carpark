package com.wego.carpark.schedulingtasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wego.carpark.model.CarParkAvailability;
import com.wego.carpark.repository.CarParkAvailabilityRepository;
import com.wego.carpark.repository.CarParkRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ConditionalOnProperty(
        value="scheduledtask.CarParkAvailabilityUpdater.enabled",
        havingValue = "true",
        matchIfMissing = false)
@Slf4j
@Component
public class CarParkAvailabilityUpdater {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final RestTemplate restTemplate;
    private final CarParkAvailabilityRepository carParkAvailabilityRepository;
    private final CarParkRepository carParkRepository;

    @Value("${carpark.availability.api.url}")
    private String apiUrl;

    @Autowired
    public CarParkAvailabilityUpdater(RestTemplate restTemplate,
                                      CarParkAvailabilityRepository carParkAvailabilityRepository,
                                      CarParkRepository carParkRepository) {
        this.restTemplate = restTemplate;
        this.carParkAvailabilityRepository = carParkAvailabilityRepository;
        this.carParkRepository = carParkRepository;
    }


    @Scheduled(fixedDelayString = "${fixed.delay.milliseconds}")
    public void updateCarParkAvailability() {

        log.info("UpdateCarParkAvailabilityTask started");

        ApiResponse apiResponse = restTemplate.getForObject(apiUrl, ApiResponse.class);

        List<CarParkAvailability> carParkAvailabilities = new ArrayList<>();
        List<String> notExistCarPark = new ArrayList<>();
        for(CarParkData carParkData : apiResponse.getItems().get(0).getCarParkData()) {

            if(carParkRepository.findByCarParkNo(carParkData.getCarParkNumber()).isEmpty()) {
                notExistCarPark.add(carParkData.getCarParkNumber());
                continue;
            }

            Date lastUpdateTime = convertToDate(carParkData.getUpdateDatetime());
            for(CarParkInfo carParkInfo : carParkData.getCarParkInfo()) {

                Optional<CarParkAvailability> carParkAvailabilityOpt =
                        carParkAvailabilityRepository.findByCarParkNoAndLotType(
                                carParkData.getCarParkNumber(), carParkInfo.getLotType());

                CarParkAvailability carParkAvailability = null;

                if(carParkAvailabilityOpt.isPresent()) {
                    Date previousUpdateTime = carParkAvailabilityOpt.get().getUpdateDatetime();
                    if(previousUpdateTime == null || lastUpdateTime.after(previousUpdateTime)) {
                        // update if update_datetime is different with previous time
                        carParkAvailability = carParkAvailabilityOpt.get();
                        carParkAvailability.setTotalLot(Integer.valueOf(carParkInfo.getTotalLots()));
                        carParkAvailability.setAvailableLot(Integer.valueOf(carParkInfo.getLotsAvailable()));
                        carParkAvailability.setUpdateDatetime(lastUpdateTime);
                    }

                } else {
                    // insert new
                    carParkAvailability = CarParkAvailability.builder()
                            .carParkNo(carParkData.getCarParkNumber())
                            .lotType(carParkInfo.getLotType())
                            .totalLot(Integer.valueOf(carParkInfo.getTotalLots()))
                            .availableLot(Integer.valueOf(carParkInfo.getLotsAvailable()))
                            .updateDatetime(lastUpdateTime)
                            .build();
                }

                if(carParkAvailability != null) {
                    carParkAvailabilities.add(carParkAvailability);
                }
            }
        }

        if(!notExistCarPark.isEmpty()) {
            log.warn("CarPark {} does not exist in database", notExistCarPark);
        }

        carParkAvailabilityRepository.saveAll(carParkAvailabilities);

        log.info("UpdateCarParkAvailabilityTask updated {} record", carParkAvailabilities.size());
    }

    @Data
    static class ApiResponse {
        @JsonProperty("api_info")
        private ApiInfo apiInfo;

        @JsonProperty("items")
        private List<Item> items;

    }

    @Data
    static class ApiInfo {
        private String status;
    }

    @Data
    static class Item {
        private String timestamp;

        @JsonProperty("carpark_data")
        private List<CarParkData> carParkData;

    }

    @Data
    static class CarParkData {

        @JsonProperty("carpark_info")
        private List<CarParkInfo> carParkInfo;

        @JsonProperty("carpark_number")
        private String carParkNumber;

        @JsonProperty("update_datetime")
        private String updateDatetime;

    }

    @Data
    static class CarParkInfo {

        @JsonProperty("total_lots")
        private String totalLots;

        @JsonProperty("lot_type")
        private String lotType;

        @JsonProperty("lots_available")
        private String lotsAvailable;


    }

    private static Date convertToDate(String dateString) {
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            log.error("Error parsing {} to Data", dateString, e);
        }

        return null;
    }

}
