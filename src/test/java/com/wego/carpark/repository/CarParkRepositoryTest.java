package com.wego.carpark.repository;

import com.wego.carpark.model.NearestAvailableCarPark;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CarParkRepositoryTest {

    @Autowired
    private CarParkRepository carParkRepository;

    @Test
    public void testFindNearestCarParkNormalCase() {

        List<NearestAvailableCarPark> nearestCarParks = carParkRepository.findNearestCarPark(103.85412, 1.30106, 6, 0);

        // Check response size
        assertThat(nearestCarParks).hasSize(5);

        // check that element are sorted asc by distance
        assertThat(nearestCarParks).usingElementComparator(
                Comparator.comparingDouble(NearestAvailableCarPark::getDistance)).isSorted();

    }

    @Test
    public void testFindNearestCarParkWithPaging() {

        List<NearestAvailableCarPark> nearestCarParks1 = carParkRepository.findNearestCarPark(
                103.85412, 1.30106, 3, 0);
        assertThat(nearestCarParks1).hasSize(3);

        List<NearestAvailableCarPark> nearestCarParks2 = carParkRepository.findNearestCarPark(
                103.85412, 1.30106, 3, 3);
        assertThat(nearestCarParks2).isNotEmpty();
        assertThat(nearestCarParks2).hasSize(2);

        List<NearestAvailableCarPark> nearestCarParks3 = carParkRepository.findNearestCarPark(
                103.85412, 1.30106, 3, 6);
        assertThat(nearestCarParks3).isEmpty();

    }
}
