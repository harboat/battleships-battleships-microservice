package com.github.harboat.battleships;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BattleshipConfiguration {

    @Value("${rabbitmq.exchanges.game}")
    private String internalGameExchange;

    @Value("${rabbitmq.queues.game}")
    private String gameQueue;

    @Value("${rabbitmq.routing-keys.game}")
    private String internalGameRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(internalGameExchange);
    }

    @Bean
    public Queue gameQueue() {
        return new Queue(gameQueue);
    }

    @Bean
    public Binding internalToPlacementBinding() {
        return BindingBuilder
                .bind(gameQueue())
                .to(internalTopicExchange())
                .with(internalGameRoutingKey);
    }

}
