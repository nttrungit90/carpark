package com.wego.carpark.mapper;

import com.wego.carpark.dto.CarParkCsvDto;
import com.wego.carpark.model.CarPark;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CarParkMapper {
    CarPark carPartCsvDtoToCarPart(CarParkCsvDto source);
}
