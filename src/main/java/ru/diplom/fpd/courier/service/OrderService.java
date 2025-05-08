package ru.diplom.fpd.courier.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.diplom.fpd.courier.model.Order;
import ru.diplom.fpd.courier.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;


    public List<Order> getOrdersWithoutCourier() {
        return orderRepository.findAllByCourierIsNull();
    }

}
