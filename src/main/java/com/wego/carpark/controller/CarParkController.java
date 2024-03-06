package com.wego.carpark.controller;

import com.wego.carpark.dto.NearestCarParkDto;
import com.wego.carpark.model.CarPark;
import com.wego.carpark.service.CarParkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/carparks")
public class CarParkController {
    private final CarParkService carParkService;

    @Autowired
    public CarParkController(CarParkService carParkService) {
        this.carParkService = carParkService;
    }

    @PostMapping
    public CarPark create(@RequestBody CarPark carPark) {
        log.debug("CarParkController create method called with data {}", carPark);
        return carParkService.create(carPark);
    }

    @GetMapping
    public List<CarPark> findAll() {
        log.debug("CarParkController findAll method called");
        return carParkService.findAll();
    }

    @GetMapping("/{id}")
    public CarPark findOne(@PathVariable Long id) {
        log.debug("CarParkController findOne method called with id: {}", id);
        return carParkService.findOne(id);
    }

    @PutMapping("/{id}")
    public CarPark update(@PathVariable Long id, @RequestBody CarPark carPark) {
        log.debug("CarParkController update method called with data: {}", carPark);
        return carParkService.update(id, carPark);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.debug("CarParkController delete method called with id: {}", id);
        carParkService.delete(id);
    }

    @GetMapping("/nearest")
    public List<NearestCarParkDto> findNearestCarPark(@RequestParam(name = "longitude") Double longitude,
                                                      @RequestParam(name = "latitude") Double latitude,
                                                      @RequestParam(name = "page") int page,
                                                      @RequestParam(name = "per_page") int perPage) {


        log.debug("CarParkController findNearestCarPark method called with longitude {}, latitude {}, page {}, perPage {}",
                longitude, latitude, page, perPage);

        return carParkService.findNearestCarPark(longitude, latitude, page, perPage);
    }

}
