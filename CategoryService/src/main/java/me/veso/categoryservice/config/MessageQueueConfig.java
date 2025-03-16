package me.veso.categoryservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {
    public static final String EXCHANGE_NAME = "category.direct";
    public static final String CATEGORY_DELETED_ERROR_QUEUE_NAME = "category.deleted.error.queue";

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue categoryDeletedErrorQueue(){
        return new Queue(CATEGORY_DELETED_ERROR_QUEUE_NAME, false);
    }

    @Bean
    public Binding categoryDeletedErrorBinding(Queue assigningQueue, DirectExchange exchange) {
        return BindingBuilder.bind(assigningQueue).to(exchange).with("category.deleted.error");
    }

    @Bean
    public Binding deletionBinding(@Qualifier("categoryDeletedErrorQueue") Queue categoryDeletedErrorQueue, DirectExchange exchange) {
        return BindingBuilder.bind(categoryDeletedErrorQueue).to(exchange).with("category.deleted.error");
    }
}
