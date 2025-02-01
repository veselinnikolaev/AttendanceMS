package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.dto.CategoryDetailsDto;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.repository.CategoryIdRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryIdService  {
    private final CategoryIdRepository categoryIdRepository;
    private final RestTemplate client;
    private final String categoryServiceUrl = "http://CATEGORY_SERVICE/categories";

    public CategoryId saveIdLongIfNotExists(String id) {
        CategoryDetailsDto category = client.getForObject(categoryServiceUrl + "/{id}", CategoryDetailsDto.class, id);
        if(category == null){
            throw new RuntimeException("Category with id " + id + " does not exist");
        }
        return categoryIdRepository.findByCategoryId(id)
                .orElseGet(() -> categoryIdRepository.save(new CategoryId().setCategoryId(id)));
    }
}
