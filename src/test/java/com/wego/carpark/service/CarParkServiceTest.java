package com.wego.carpark.service;

import com.wego.carpark.dto.NearestCarParkDto;
import com.wego.carpark.model.NearestAvailableCarPark;
import com.wego.carpark.repository.CarParkRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarParkServiceTest {

    @Mock
    private CarParkRepository carParkRepository;

    @InjectMocks
    private CarParkService carParkService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        carParkService = new CarParkService(carParkRepository);
    }

    @Test
    public void testFindNearestCarPark() {
        // Mock data
        List<NearestAvailableCarPark> nearestAvailableCarParks = new ArrayList<>();
        nearestAvailableCarParks.add(new NearestAvailableCarParkDto("CP1", "Address 1", 1.36683, 103.85788, "C", 50, 1, 100.0));
        nearestAvailableCarParks.add(new NearestAvailableCarParkDto("CP2", "Address 2", 1.33377, 103.84859, "C", 75, 2, 150.0));
        nearestAvailableCarParks.add(new NearestAvailableCarParkDto("CP2", "Address 2", 1.33377, 103.84859, "D", 10, 6, 150.0));


        when(carParkRepository.findNearestCarPark(anyDouble(), anyDouble(), anyInt(), anyInt()))
                .thenReturn(nearestAvailableCarParks);

        // Call the service method
        List<NearestCarParkDto> result = carParkService.findNearestCarPark(103.74859, 1.46683, 1, 10);

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        NearestCarParkDto carParkDto = result.get(0);
        assertThat(carParkDto.getCarParkNo()).isEqualTo("CP1");
        assertThat(carParkDto.getAddress()).isEqualTo("Address 1");
        assertThat(carParkDto.getLatitude()).isEqualTo(1.36683);
        assertThat(carParkDto.getLongitude()).isEqualTo(103.85788);
        assertThat(carParkDto.getTotalLots()).isEqualTo(50);
        assertThat(carParkDto.getAvailableLots()).isEqualTo(1);
        assertThat(carParkDto.getDistance()).isEqualTo(100.0);

        NearestCarParkDto carParkDto2 = result.get(1);
        assertThat(carParkDto2.getCarParkNo()).isEqualTo("CP2");
        assertThat(carParkDto2.getAddress()).isEqualTo("Address 2");
        assertThat(carParkDto2.getLatitude()).isEqualTo(1.33377);
        assertThat(carParkDto2.getLongitude()).isEqualTo(103.84859);
        assertThat(carParkDto2.getTotalLots()).isEqualTo(85);
        assertThat(carParkDto2.getAvailableLots()).isEqualTo(8);
        assertThat(carParkDto2.getDistance()).isEqualTo(150.0);
    }

    @AllArgsConstructor
    @Data
    class NearestAvailableCarParkDto implements NearestAvailableCarPark {
        private String carParkNo;
        private String address;
        private Double latitude;
        private Double longitude;
        private String lotType;
        private Integer totalLot;
        private Integer availableLot;
        private Double distance;
    }
}
