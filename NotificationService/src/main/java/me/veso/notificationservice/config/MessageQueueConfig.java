package me.veso.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {
    public static final String EXCHANGE_NAME = "notification.direct";
    public static final String ATTENDANCE_CREATED_QUEUE_NAME = "notify.attendance.created.queue";
    public static final String STATUS_UPDATED_QUEUE_NAME = "notify.status.updated.queue";
    public static final String USERS_ASSIGNED_QUEUE_NAME = "notify.users.assigned.queue";

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue attendanceCreatedQueue(){
        return new Queue(ATTENDANCE_CREATED_QUEUE_NAME, false);
    }

    @Bean
    public Queue statusUpdatedQueue(){
        return new Queue(STATUS_UPDATED_QUEUE_NAME, false);
    }

    @Bean
    public Queue usersAssignedQueue(){
        return new Queue(USERS_ASSIGNED_QUEUE_NAME, false);
    }

    @Bean
    public Binding attendanceCreatedbinding(@Qualifier("attendanceCreatedQueue") Queue attendanceCreatedqueue, DirectExchange exchange) {
        return BindingBuilder.bind(attendanceCreatedqueue).to(exchange).with("notify.attendance.created");
    }

    @Bean
    public Binding statusUpdatedbinding(@Qualifier("statusUpdatedQueue") Queue statusUpdatedqueue, DirectExchange exchange) {
        return BindingBuilder.bind(statusUpdatedqueue).to(exchange).with("notify.status.updated");
    }

    @Bean
    public Binding usersAssignedbinding(@Qualifier("usersAssignedQueue") Queue usersAssignedQueue, DirectExchange exchange) {
        return BindingBuilder.bind(usersAssignedQueue).to(exchange).with("notify.users.assigned");
    }
}
