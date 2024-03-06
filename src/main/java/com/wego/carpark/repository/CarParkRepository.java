package com.wego.carpark.repository;

import com.wego.carpark.model.CarPark;
import com.wego.carpark.model.NearestAvailableCarPark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface CarParkRepository extends JpaRepository<CarPark, Long> {

    /**
     * Find nearest car park with has available lot, order by distance (meter)
     * Distance is calculated using Haversine formula https://en.wikipedia.org/wiki/Haversine_formula
     * @param latitude
     * @param longitude
     * @param limit
     * @param offSetIdx
     * @return
     */
    @Query(value = "SELECT cp.car_park_no AS carParkNo, cp.address, cp.latitude, cp.longitude, cpa.lot_type AS lotType, cpa.total_lot AS totalLot, cpa.available_lot AS availableLot, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(cp.latitude)) * cos(radians(cp.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(cp.latitude)))) AS distance " +
            "FROM car_park cp " +
            "JOIN car_park_availability cpa ON cp.car_park_no = cpa.car_park_no " +
            "WHERE cpa.available_lot > 0 " +
            "ORDER BY distance " +
            "LIMIT :limit OFFSET :offSetIdx",
            nativeQuery = true)
    List<NearestAvailableCarPark> findNearestCarPark(@Param("latitude") Double latitude, @Param("longitude") Double longitude,
                                                     @Param("limit") int limit, @Param("offSetIdx") int offSetIdx);


    Optional<CarPark> findByCarParkNo(String carPakNo);
}