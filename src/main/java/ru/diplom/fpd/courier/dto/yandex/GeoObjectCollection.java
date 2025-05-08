package ru.diplom.fpd.courier.dto.yandex;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.diplom.fpd.courier.deserializers.YandexGeoResponseDeserializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = YandexGeoResponseDeserializer.class)
public class GeoObjectCollection {
    private List<GeoObject> geoObjects;
}
