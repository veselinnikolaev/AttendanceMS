package me.veso.userservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {
    public static final String EXCHANGE_NAME = "user.direct";
    public static final String QUEUE_NAME = "status.updated.queue";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("user.status.updated");
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("user.status.deadletter.queue").build();
    }
}
