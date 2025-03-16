package me.veso.userservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {
    public static final String EXCHANGE_NAME = "user.direct";
    public static final String USERS_ASSIGNED_QUEUE_NAME = "users.assigned.queue";
    public static final String CATEGORY_DELETED_QUEUE_NAME = "category.deleted.queue";

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue usersAssignedQueue(){
        return new Queue(USERS_ASSIGNED_QUEUE_NAME, false);
    }

    @Bean
    public Queue categoryDeletedQueue(){
        return new Queue(CATEGORY_DELETED_QUEUE_NAME, false);
    }

    @Bean
    public Binding usersAssignedBinding(@Qualifier("usersAssignedQueue") Queue usersAssignedQueue, DirectExchange exchange) {
        return BindingBuilder.bind(usersAssignedQueue).to(exchange).with("users.assigned");
    }

    @Bean
    public Binding categoryDeletedBinding(@Qualifier("categoryDeletedQueue") Queue categoryDeletedQueue, DirectExchange exchange) {
        return BindingBuilder.bind(categoryDeletedQueue).to(exchange).with("category.deleted");
    }
}
