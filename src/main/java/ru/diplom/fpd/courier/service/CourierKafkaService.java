package ru.diplom.fpd.courier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import static ru.diplom.fpd.courier.configuration.KafkaConfig.COURIER_SEARCH_LISTENER_FACTORY;
import ru.diplom.fpd.courier.configuration.property.CourierKafkaProperties;
import ru.diplom.fpd.courier.dto.kafka.CourierFoundMessage;
import ru.diplom.fpd.courier.dto.kafka.CourierSearchMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierKafkaService {

    private final CourierKafkaProperties properties;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CourierService courierService;


    public void sendSearchMessage(CourierFoundMessage message) {
        log.info("Sending search courier message");
        kafkaTemplate.send(properties.getOutTopic(), message);
    }

    @KafkaListener(containerFactory = COURIER_SEARCH_LISTENER_FACTORY,
            topics = "${kafka.order-service.in-topic}",
            groupId = "${kafka.order-service.group-id}")
    public void listenSearchedMessage(CourierSearchMessage message) {
        courierService.search(message);
    }

    public void sendCompliantOrderMessage(CourierFoundMessage message) {
        kafkaTemplate.send(properties.getCompleteOrderTopic(), message);
    }

}
