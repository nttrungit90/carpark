package com.wego.carpark.repository;

import com.wego.carpark.model.CarParkAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CarParkAvailabilityRepository extends JpaRepository<CarParkAvailability, Long> {
    Optional<CarParkAvailability> findByCarParkNoAndLotType(String carParkNo, String lotType);
}
