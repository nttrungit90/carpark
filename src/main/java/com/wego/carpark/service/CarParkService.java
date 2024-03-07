package com.wego.carpark.service;

import com.wego.carpark.dto.NearestCarParkDto;
import com.wego.carpark.exception.NotFoundException;
import com.wego.carpark.model.CarPark;
import com.wego.carpark.model.NearestAvailableCarPark;
import com.wego.carpark.repository.CarParkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CarParkService {
    private final CarParkRepository carParkRepository;

    @Autowired
    public CarParkService(CarParkRepository carParkRepository) {
        this.carParkRepository = carParkRepository;
    }


    /**
     * Find nearest car park with has available lot, order by distance
     * @param longitude
     * @param latitude
     * @param page
     * @param perPage
     * @return List<NearestCarParkDto>
     */
    public List<NearestCarParkDto> findNearestCarPark(Double longitude, Double latitude, int page, int perPage) {

        List<NearestAvailableCarPark> nearestAvailableCarParks = carParkRepository
                .findNearestCarPark(longitude, latitude, perPage, (page -1 ) * perPage);

        return mapToCarParkDto(nearestAvailableCarParks);
    }

    private static List<NearestCarParkDto> mapToCarParkDto(List<NearestAvailableCarPark> nearestCarParks) {
        // Group NearestAvailableCarPark objects by carParkNo
        Map<String, List<NearestAvailableCarPark>> groupedByCarParkNo =
                nearestCarParks.stream()
                        .collect(Collectors.groupingBy(NearestAvailableCarPark::getCarParkNo));

        // Convert the grouped map to a list of CarParkDto objects
        return groupedByCarParkNo.entrySet().stream()
                .map(entry -> {
                    String carParkNo = entry.getKey();
                    List<NearestAvailableCarPark> carParks = entry.getValue();
                    return mapToCarParkDto(carParkNo, carParks);
                })
                .sorted(Comparator.comparing(NearestCarParkDto::getDistance))
                .collect(Collectors.toList());
    }

    private static NearestCarParkDto mapToCarParkDto(String carParkNo, List<NearestAvailableCarPark> carParks) {
        // Create a new CarParkDto object
        NearestCarParkDto nearestCarParkDto = new NearestCarParkDto();

        // Populate fields from the first NearestAvailableCarPark object
        NearestAvailableCarPark firstCarPark = carParks.get(0);
        nearestCarParkDto.setCarParkNo(carParkNo);
        nearestCarParkDto.setAddress(firstCarPark.getAddress());
        nearestCarParkDto.setLatitude(firstCarPark.getLatitude());
        nearestCarParkDto.setLongitude(firstCarPark.getLongitude());
        nearestCarParkDto.setDistance(firstCarPark.getDistance());

        // Accumulate total and available lots
        int totalLots = carParks.stream().mapToInt(NearestAvailableCarPark::getTotalLot).sum();
        int availableLots = carParks.stream().mapToInt(NearestAvailableCarPark::getAvailableLot).sum();
        nearestCarParkDto.setTotalLots(totalLots);
        nearestCarParkDto.setAvailableLots(availableLots);

        return nearestCarParkDto;
    }

    public CarPark create(CarPark carPark) {
        log.debug("CarPark create action called {}", carPark);
        return carParkRepository.save(carPark);
    }

    public List<CarPark> batchCreate(List<CarPark> carParks) {
        return carParkRepository.saveAll(carParks);
    }

    public CarPark findOne(Long carParkId) {
        log.debug("CarPark findById action called {}", carParkId);
        return carParkRepository.findById(carParkId)
                .orElseThrow(() -> new NotFoundException("CarPark of id " +  carParkId + " not found."));
    }

    public List<CarPark> findAll() {
        log.debug("CarPark findAll action called");
        return carParkRepository.findAll();
    }
    public Optional<CarPark> findByCarParkNo(String carParkNo) {
        log.debug("CarPark findByCarParkNo action called {}", carParkNo);
        return carParkRepository.findByCarParkNo(carParkNo);
    }

    public CarPark update(Long carParkId, CarPark newCarPark) {
        log.debug("CarPark update action called, id {}, data {}", carParkId, newCarPark);
        return carParkRepository.findById(carParkId)
                .map(carPark -> {

                    carPark.setAddress(newCarPark.getAddress());
                    carPark.setLongitude(newCarPark.getLongitude());
                    carPark.setLatitude(newCarPark.getLatitude());
                    carPark.setCarParkType(newCarPark.getCarParkType());
                    carPark.setTypeOfParkingSystem(newCarPark.getTypeOfParkingSystem());
                    carPark.setShortTermParking(newCarPark.getShortTermParking());
                    carPark.setFreeParking(newCarPark.getFreeParking());
                    carPark.setNightParking(newCarPark.getNightParking());
                    carPark.setCarParkDecks(newCarPark.getCarParkDecks());
                    carPark.setGantryHeight(newCarPark.getGantryHeight());
                    carPark.setCarParkBasement(newCarPark.getCarParkBasement());

                    return carParkRepository.save(carPark);
                })
                .orElseGet(() -> {
                    newCarPark.setId(carParkId);
                    return carParkRepository.save(newCarPark);
                });
    }

    public void delete(Long carParkId) {
        log.debug("CarPark delete action called with id {}", carParkId);
        carParkRepository.deleteById(carParkId);
    }
}
