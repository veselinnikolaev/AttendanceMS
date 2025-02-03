package me.veso.categoryservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {
    public static final String EXCHANGE_NAME = "categories.direct";

    public static final String ASSIGNING_QUEUE_NAME = "users.assigning.queue";
    public static final String DELETION_QUEUE_NAME = "category.deleted.queue";

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue assigningQueue(){
        return new Queue(ASSIGNING_QUEUE_NAME, false);
    }

    @Bean
    public Queue deletionQueue(){
        return new Queue(DELETION_QUEUE_NAME, false);
    }

    @Bean
    public Binding assingingBinding(Queue assigningQueue, DirectExchange exchange) {
        return BindingBuilder.bind(assigningQueue).to(exchange).with("users.assigned");
    }

    @Bean
    public Binding deletionBinding(Queue deletionQueue, DirectExchange exchange) {
        return BindingBuilder.bind(deletionQueue).to(exchange).with("category.deleted");
    }
}
