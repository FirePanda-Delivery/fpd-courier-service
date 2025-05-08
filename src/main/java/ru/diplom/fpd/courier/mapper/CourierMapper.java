package ru.diplom.fpd.courier.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.diplom.fpd.courier.dto.CourierDto;
import ru.diplom.fpd.courier.dto.CourierReq;
import ru.diplom.fpd.courier.model.Courier;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourierMapper {

    Courier toEntity(CourierDto courierDto);

    CourierDto toDto(Courier courier);

    Courier requestToEntity(CourierReq courierReq);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Courier partialUpdate(CourierDto courierDto, @MappingTarget Courier courier);
}