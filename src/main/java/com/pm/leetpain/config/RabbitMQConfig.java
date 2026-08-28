package com.pm.leetpain.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "submission_queue";
    public static final String EXCHANGE_NAME = "submission_exchange";
    public static final String ROUTING_KEY = "submission_routing_key";

    @Bean
    public Queue submissionQueue(){
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME);
    }
    @Bean
    public Binding binding(Queue submissionQueue, DirectExchange submissionExchange) {
        return BindingBuilder.bind(submissionQueue).to(submissionExchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());

        // Exactly 2 worker threads
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(2);

        // Each worker thread fetches only 1 message at a time
        factory.setPrefetchCount(1);

        return factory;
    }

}
