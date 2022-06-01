package com.github.harboat.battleships;

import com.github.harboat.rabbitmq.RabbitMQMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoreQueueProducer {
    private final RabbitMQMessageProducer producer;

    @Value("${rabbitmq.exchanges.core}")
    private String internalExchange;

    @Value("${rabbitmq.routing-keys.core}")
    private String gameCreationResponseRoutingKey;

    public <T> void sendResponse(T response) {
        producer.publish(response, internalExchange, gameCreationResponseRoutingKey);
    }
}
