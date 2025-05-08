package ru.diplom.fpd.courier.processing;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.diplom.fpd.courier.dto.Coordinates;
import ru.diplom.fpd.courier.dto.yandex.GeoObject;
import ru.diplom.fpd.courier.dto.yandex.GeoObjectCollection;
import ru.diplom.fpd.courier.model.cache.ActiveCourierLocation;

@Component
@RequiredArgsConstructor()
public class AddressProcessing {

    @Qualifier("yandexMapsRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${yandex.api.key}")
    String apiKey;


    public Long courierNearestToAddress(List<ActiveCourierLocation> couriers, String address) {

        Coordinates cords = getCords(address);

        int courierIndexWithMinimumDistanceToAddress = 0;

        double minDistance = 0;

        for (int index = 0; index < couriers.size(); index++) {

            Coordinates courierLocation = couriers.get(index).getCurrentCoordinates();

            if (index == 0) {
                minDistance = Math.sqrt(Math.pow((cords.getX() - courierLocation.getX()), 2) +
                        Math.pow((cords.getY() - courierLocation.getY()), 2));
            }

            double distance = Math.sqrt(Math.pow((cords.getX() - courierLocation.getX()), 2) +
                    Math.pow((cords.getY() - courierLocation.getY()), 2));

            if (minDistance > distance) {

                minDistance = distance;
                courierIndexWithMinimumDistanceToAddress = index;
            }

        }

        return couriers.get(courierIndexWithMinimumDistanceToAddress).getCourierId();
    }



    public final Coordinates getCords(String address) {

        if (address == null || address.isEmpty()) {
            throw new NullPointerException("address not set");
        }

        String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://geocode-maps.yandex.ru/1.x")
                .queryParam("apikey", "{apikey}")
                .queryParam("format", "{format}")
                .queryParam("results", "{results}")
                .queryParam("geocode", "{geocode}")
                .encode()
                .toUriString();

        Map<String, Object> params = Map.of(
                "apikey", apiKey,
                "format", "json",
                "results", 1,
                "geocode", address);

        List<GeoObject> result = restTemplate
                .exchange(urlTemplate, HttpMethod.GET, null,
                        GeoObjectCollection.class,
                        params)
                .getBody()
                .getGeoObjects();

        return Optional.ofNullable(result)
                .filter(Predicate.not(List::isEmpty))
                .map(list -> list.get(0))
                .map(GeoObject::getPoint)
                .map(Coordinates::toCoordinates)
                .orElseThrow();
    }

}


