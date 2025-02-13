package me.veso.attendanceservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.service.AttendanceService;
import me.veso.attendanceservice.service.CategoryIdService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "category.deleted.queue")
@RequiredArgsConstructor
@Slf4j
public class CategoryDeletedListener {
    private final CategoryIdService categoryService;
    private final AttendanceService attendanceService;

    @RabbitHandler
    public void handleDeletedCategory(String id){
        log.info("Category deleted event caught");
        CategoryId category = categoryService.findByCategoryId(id);

        categoryService.delete(category.getCategoryId());

        attendanceService.deleteByCategoryId(id);

        log.info("Category successfully deleted, id " + id);
    }
}
