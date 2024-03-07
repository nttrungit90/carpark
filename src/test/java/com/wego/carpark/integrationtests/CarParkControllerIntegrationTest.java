package com.wego.carpark.integrationtests;

import com.wego.carpark.dto.NearestCarParkDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarParkControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testFindNearestAvailableCarParkInvalidInputParameters() {
        String apiUrl = "/api/carparks/nearest?longitude={longitude}&latitude={latitude}&page={page}&per_page={per_page}";
        Map<String, Object> vars = new HashMap<>();
        vars.put("longitude", "invalid");
        vars.put("latitude", 1.30106);
        vars.put("page", 1);
        vars.put("per_page", 3);

        ResponseEntity<String> respEntity = restTemplate.getForEntity(apiUrl, String.class, vars);
        assertThat(respEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    void findNearestAvailableCarParkValidInputParameters() {
        String apiUrl = "/api/carparks/nearest?longitude={longitude}&latitude={latitude}&page={page}&per_page={per_page}";
        Map<String, Object> vars = new HashMap<>();
        vars.put("longitude", 103.85412);
        vars.put("latitude", 1.30106);
        vars.put("page", 2);
        vars.put("per_page", 3);

        ResponseEntity<NearestCarParkDto[]> respEntity = restTemplate.getForEntity(apiUrl, NearestCarParkDto[].class, vars);
        assertThat(respEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<NearestCarParkDto> nearestCarParkDtos = Arrays.asList(respEntity.getBody());

        assertThat(nearestCarParkDtos).hasSize(2);

        // check that element are sorted asc by distance
        assertThat(nearestCarParkDtos).usingElementComparator(
                Comparator.comparingDouble(NearestCarParkDto::getDistance)).isSorted();

        assertThat(nearestCarParkDtos.get(0).getCarParkNo()).isEqualTo("A87");
        assertThat(nearestCarParkDtos.get(0).getTotalLots()).isEqualTo(124);
        assertThat(nearestCarParkDtos.get(0).getAvailableLots()).isEqualTo(95);

        assertThat(nearestCarParkDtos.get(1).getCarParkNo()).isEqualTo("TP17");
        assertThat(nearestCarParkDtos.get(1).getTotalLots()).isEqualTo(119);
        assertThat(nearestCarParkDtos.get(1).getAvailableLots()).isEqualTo(3);

    }
}