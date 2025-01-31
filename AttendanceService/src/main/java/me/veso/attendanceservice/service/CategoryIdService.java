package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.repository.CategoryIdRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryIdService  {
    private final CategoryIdRepository categoryIdRepository;

    public CategoryId saveIdLongIfNotExists(Long id) {
        return categoryIdRepository.findByCategoryId(id)
                .orElseGet(() -> categoryIdRepository.save(new CategoryId().setCategoryId(id)));
    }
}
