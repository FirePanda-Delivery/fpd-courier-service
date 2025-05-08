package ru.diplom.fpd.courier.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.diplom.fpd.courier.service.CourierService;
import ru.diplom.fpd.courier.service.OrderService;

@Service
@RequiredArgsConstructor
public class CourierSearchScheduler {

    private final CourierService courierService;
    private final OrderService orderService;

    @Scheduled(fixedRateString = "${schedulers.courier-found.rate}")
    public void courierSearchJob() {
        orderService.getOrdersWithoutCourier().forEach(courierService::search);
    }

}
