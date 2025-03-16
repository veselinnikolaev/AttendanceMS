package me.veso.notificationservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.notificationservice.client.UserClient;
import me.veso.notificationservice.dto.AttendanceDetailsDto;
import me.veso.notificationservice.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RabbitListener(queues = "attendance.created.queue")
@RequiredArgsConstructor
@Slf4j
public class AttendanceCreatedListener {
    private final MailService mailService;
    private final UserClient userClient;

    @RabbitHandler
    public void handleAttendanceCreated(List<AttendanceDetailsDto> attendanceDetailsDtos) {
        List<CompletableFuture<Void>> futures = attendanceDetailsDtos.stream()
                .map(attendanceDetailsDto -> CompletableFuture.supplyAsync(() -> userClient.getUserForId(attendanceDetailsDto.userId()))
                        .thenAccept(user -> {
                            if (user == null) {
                                log.warn("Skipping email notification for user ID {}, user not found.", attendanceDetailsDto.userId());
                                return;
                            }
                            mailService.send(user.email(), "New Attendance",
                                    """
                                            Hi %s,
                                            There is a new attendance for your profile!
                                            Attendance status: %s.
                                            """.formatted(user.username(), attendanceDetailsDto.status()));
                        })
                        .exceptionally(ex -> {
                            log.error("Failed to fetch user details for ID {}: {}", attendanceDetailsDto.userId(), ex.getMessage());
                            return null;
                        }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
