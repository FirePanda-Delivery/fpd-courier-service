package ru.diplom.fpd.courier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.diplom.fpd.courier.model.CourierStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierLocationDto {

    private Coordinates coordinates;
    private CourierStatus courierStatus;
}
