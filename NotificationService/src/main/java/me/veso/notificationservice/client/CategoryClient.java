package me.veso.notificationservice.client;

import lombok.RequiredArgsConstructor;
import me.veso.notificationservice.dto.CategoryDetailsDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CategoryClient {
    private final RestTemplate restTemplate;
    private final String categoryServiceUrl = "http://CATEGORY_SERVICE/categories";

    public CategoryDetailsDto getCategoryForId(String id) {
        return restTemplate.getForEntity(categoryServiceUrl + "/{id}", CategoryDetailsDto.class, id).getBody();
    }
}
