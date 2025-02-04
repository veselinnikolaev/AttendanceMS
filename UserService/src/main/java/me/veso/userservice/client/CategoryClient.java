package me.veso.userservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.userservice.dto.CategoryDetailsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryClient {
    private final RestTemplate restTemplate;
    private final String categoryServiceUrl = "http://CATEGORY_SERVICE/categories";

    public CategoryDetailsDto getCategoryForId(String id) {
        try {
            log.debug("Fetching category details for ID: {}", id);
            ResponseEntity<CategoryDetailsDto> response = restTemplate.getForEntity(categoryServiceUrl + "/{id}", CategoryDetailsDto.class, id);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Failed to fetch category details for ID: {}. Error: {}", id, e.getMessage());
            return null;
        }
    }
}
