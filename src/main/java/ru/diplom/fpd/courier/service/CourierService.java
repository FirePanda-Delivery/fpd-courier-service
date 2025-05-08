package ru.diplom.fpd.courier.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.RemoteCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static ru.diplom.fpd.courier.configuration.ApplicationConfig.ACTIVE_COURIER_LOCATION_CACHE_NAME;
import ru.diplom.fpd.courier.dto.Coordinates;
import ru.diplom.fpd.courier.dto.CourierDto;
import ru.diplom.fpd.courier.dto.CourierLocationDto;
import ru.diplom.fpd.courier.dto.CourierReq;
import ru.diplom.fpd.courier.dto.kafka.CourierFoundMessage;
import ru.diplom.fpd.courier.dto.kafka.CourierSearchMessage;
import ru.diplom.fpd.courier.mapper.CourierMapper;
import ru.diplom.fpd.courier.mapper.OrderMapper;
import ru.diplom.fpd.courier.model.Courier;
import static ru.diplom.fpd.courier.model.CourierStatus.ACTIVE;
import static ru.diplom.fpd.courier.model.CourierStatus.NOT_ACTIVE;
import ru.diplom.fpd.courier.model.Location;
import ru.diplom.fpd.courier.model.Order;
import ru.diplom.fpd.courier.model.cache.ActiveCourierLocation;
import ru.diplom.fpd.courier.processing.AddressProcessing;
import ru.diplom.fpd.courier.repository.CourierRepositories;
import ru.diplom.fpd.courier.repository.LocationRepository;
import ru.diplom.fpd.courier.repository.OrderRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class CourierService {

    private final CourierRepositories courierRepositories;
    private final CourierMapper courierMapper;
    @Qualifier(ACTIVE_COURIER_LOCATION_CACHE_NAME)
    private final RemoteCache<Long, ActiveCourierLocation> activeCouriersCache;
    private final AddressProcessing addressProcessing;
    @Lazy
    private final CourierKafkaService courierKafkaService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final LocationRepository locationRepository;


    /**
     * get all users except deleted ones
     *
     * @return list of users without deleted
     */
    public List<CourierDto> getCourierList() {

        return courierRepositories.findByIsDeletedFalse().stream()
                .map(courierMapper::toDto)
                .toList();
    }

    public CourierDto get(long id) {
        if (id == 0) {
            throw new NullPointerException("id not set");
        }
        return courierMapper.toDto(courierRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Courier is not found")));
    }

    public CourierDto add(CourierReq courier) {
        return courierMapper.toDto(courierRepositories.save(courierMapper.requestToEntity(courier)));
    }

    public CourierDto update(CourierDto courier) {
        if (!courierRepositories.existsById(courier.getId())) {
            throw new EntityNotFoundException("courier not found!");
        }
        return courierMapper.toDto(courierRepositories.save(courierMapper.toEntity(courier)));
    }

    public void delete(long id) {

        Optional<Courier> courierOptional = courierRepositories.findById(id);

        if (courierOptional.isEmpty()) {
            throw new EntityNotFoundException("user not found!");
        }

        Courier courier = courierOptional.get();
        courier.setDeleted(true);

        courierRepositories.save(courier);
    }

    @Transactional
    public void search(CourierSearchMessage message) {
        Order order = orderMapper.toOrder(message);
        order.setOnActive(true);
        orderRepository.saveAndFlush(order);
        search(order);
    }

    @Transactional
    public void search(Order order) {
        Set<Long> activeCouriersInCity = courierRepositories.findAllActiveFreeByCity(order.getCity()).stream()
                .map(Courier::getId)
                .collect(Collectors.toSet());
        Optional.ofNullable(
                        activeCouriersCache.getAll(activeCouriersInCity))
                .filter(Predicate.not(Map::isEmpty))
                .map(Map::values)
                .map(ArrayList::new)
                .ifPresent(list -> searchByList(list, order));
        orderRepository.saveAndFlush(order);
    }

    private void searchByList(List<ActiveCourierLocation> couriers, Order order) {
        Long courierId = addressProcessing.courierNearestToAddress(couriers, order.getRestaurantAddress());
        courierKafkaService.sendSearchMessage(new CourierFoundMessage(order.getOrderId(), courierId));
        order.setCourier(courierRepositories.findById(courierId)
                .orElseThrow(() -> new EntityNotFoundException("courier not found!")));
    }


    public void courierCompletedOrder(Long courierId, Long orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("order not found!"));
        order.setOnActive(false);
        orderRepository.save(order);
        courierKafkaService.sendCompliantOrderMessage(new CourierFoundMessage(courierId, orderId));
    }

    @Transactional
    public void setCourierLocation(long id, CourierLocationDto location) {
        if (location == null) {
            throw new NullPointerException("Location not set");
        }
        Coordinates coordinates = location.getCoordinates();

        if (ACTIVE.equals(location.getCourierStatus())) {

            courierRepositories.findById(id)
                    .filter(courier -> NOT_ACTIVE.equals(courier.getStatus()))
                    .ifPresent(courier -> courierRepositories.setActiveStatus(id));

            Optional.ofNullable(activeCouriersCache.get(id)).ifPresentOrElse(activeCourierLocation -> {
                activeCourierLocation.setPreviousCoordinates(activeCourierLocation.getCurrentCoordinates());
                activeCourierLocation.setCurrentCoordinates(coordinates);

                orderRepository.findActiveByCourierId(id).ifPresent(order ->
                        locationRepository.save(Location.builder()
                                .orderId(order.getOrderId())
                                .x(coordinates.getX())
                                .y(coordinates.getY())
                                .courier(order.getCourier())
                                .build())
                );
                activeCouriersCache.replace(id, activeCourierLocation);
            }, () -> {
                activeCouriersCache.put(id, new ActiveCourierLocation(id, coordinates, null));
            });
        } else {
            courierRepositories.setNotActiveStatus(id);
            activeCouriersCache.remove(id);
        }
    }
}
