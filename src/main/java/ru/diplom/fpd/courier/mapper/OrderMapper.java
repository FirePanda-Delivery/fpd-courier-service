package ru.diplom.fpd.courier.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.diplom.fpd.courier.dto.kafka.CourierSearchMessage;
import ru.diplom.fpd.courier.model.Order;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "restaurantAddress", source = "restaurantAddress.address")
    @Mapping(target = "city", source = "restaurantAddress.city")
    @Mapping(target = "courier", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toOrder(CourierSearchMessage courierSearchMessage);


}