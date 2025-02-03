package me.veso.userservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.userservice.dto.CategoryDetailsDto;
import me.veso.userservice.entity.CategoryId;
import me.veso.userservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private final RestTemplate client;
    private final String categoryServiceUrl = "http://CATEGORY_SERVICE/categories";

    public CategoryId saveIfNotExists(String categoryId){
        CategoryDetailsDto category = client.getForEntity(categoryServiceUrl + "/{id}", CategoryDetailsDto.class, categoryId).getBody();
        if(category == null){
            throw new RuntimeException("Category with id " + categoryId + " does not exist");
        }

        return repository.findByCategoryId(categoryId)
                .orElseGet(() -> repository.save(new CategoryId().setCategoryId(categoryId)));
    }

    public CategoryId findByCategoryId(String id) {
        return repository.findByCategoryId(id)
                .orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
    }

    @Transactional
    public void delete(CategoryId category) {
        repository.delete(category);
    }
}
