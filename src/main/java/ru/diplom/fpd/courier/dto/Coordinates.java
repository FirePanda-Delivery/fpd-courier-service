package ru.diplom.fpd.courier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;

/**
 * Для нормальной работы protostream-processor поля не должны быть private.
 * Скрытие полей достигается мдификаторм protected и ограничением final на классе.
 */
@Data
@AllArgsConstructor(onConstructor = @__(@ProtoFactory))
@NoArgsConstructor
@Proto
public final class Coordinates {

    Double x;

    Double y;

    public static Coordinates toCoordinates(String str) {
        String[] arr = str.split(" ");
        Coordinates cords = new Coordinates();
        cords.setX(Double.parseDouble(arr[0]));
        cords.setY(Double.parseDouble(arr[1]));

        return cords;
    }
}
