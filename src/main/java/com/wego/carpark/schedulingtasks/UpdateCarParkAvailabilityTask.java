package com.wego.carpark.schedulingtasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wego.carpark.model.CarParkAvailability;
import com.wego.carpark.repository.CarParkAvailabilityRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ConditionalOnProperty(
        value="scheduledtask.UpdateCarParkAvailabilityTask.enabled",
        havingValue = "true",
        matchIfMissing = false)
@Slf4j
@Component
public class UpdateCarParkAvailabilityTask {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final RestTemplate restTemplate;
    private final CarParkAvailabilityRepository carParkAvailabilityRepository;

    @Value("${carpark.availability.api.url}")
    private String apiUrl;

    @Autowired
    public UpdateCarParkAvailabilityTask(RestTemplate restTemplate,CarParkAvailabilityRepository carParkAvailabilityRepository) {
        this.restTemplate = restTemplate;
        this.carParkAvailabilityRepository = carParkAvailabilityRepository;
    }


    @Scheduled(fixedDelay = 5*60*1000)
    public void updateCarParkAvailability() {

        log.info("UpdateCarParkAvailabilityTask started");

        ApiResponse apiResponse = restTemplate.getForObject(apiUrl, ApiResponse.class);

        for(CarParkData carParkData : apiResponse.getItems().get(0).getCarParkData()) {

            Date lastUpdateTime = convertToDate(carParkData.getUpdateDatetime());
            for(CarParkInfo carParkInfo : carParkData.getCarParkInfo()) {

                Optional<CarParkAvailability> carParkAvailabilityOpt =
                        carParkAvailabilityRepository.findByCarParkNoAndLotType(
                                carParkData.getCarParkNumber(), carParkInfo.getLotType());

                CarParkAvailability carParkAvailability = null;

                if(carParkAvailabilityOpt.isPresent()) {
                    Date previousUpdateTime = carParkAvailabilityOpt.get().getUpdateDatetime();
                    if(previousUpdateTime != null || lastUpdateTime.after(previousUpdateTime)) {
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
                try {
                    if(carParkAvailability != null) {
                        carParkAvailabilityRepository.save(carParkAvailability);
                    }

                } catch (Exception e) {
                    log.error("Error updating carParkAvailability {}", carParkAvailability, e);
                }
            }
        }

        log.info("CarParkAvailability updated by scheduling task");
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
