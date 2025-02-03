package me.veso.userservice.listener;

import lombok.RequiredArgsConstructor;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.entity.User;
import me.veso.userservice.service.CategoryService;
import me.veso.userservice.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RabbitListener(queues = "category.deleted.queue")
@RequiredArgsConstructor
public class CategoryDeletedListener {
    private final CategoryService categoryService;
    private final UserService userService;

    @RabbitHandler
    public void handleDeletedCategory(String id){
        CategoryId category = categoryService.findByCategoryId(id);

        categoryService.delete(category);

        List<User> users = userService.findAllByCategoryId(id);

        users.forEach(u -> u.removeCategory(category));

        userService.saveAll(users);
    }
}
