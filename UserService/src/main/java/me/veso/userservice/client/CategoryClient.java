package me.veso.userservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.dto.CategoryDetailsDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryClient {
    private final RestTemplate restTemplate;
    private final String categoryServiceUrl = "http://CATEGORY_SERVICE/categories";

    public CategoryDetailsDto getCategoryForId(String id) {
        return restTemplate.getForEntity(categoryServiceUrl + "/{id}", CategoryDetailsDto.class, id).getBody();
    }
}
