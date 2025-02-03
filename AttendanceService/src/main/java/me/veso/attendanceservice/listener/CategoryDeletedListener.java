package me.veso.attendanceservice.listener;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.service.AttendanceService;
import me.veso.attendanceservice.service.CategoryIdService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "category.deleted.queue")
@RequiredArgsConstructor
public class CategoryDeletedListener {
    private final CategoryIdService categoryService;
    private final AttendanceService attendanceService;

    @RabbitHandler
    public void handleDeletedCategory(String id){
        CategoryId category = categoryService.findByCategoryId(id);

        categoryService.delete(category);

        attendanceService.deleteByCategoryId(id);
    }
}
