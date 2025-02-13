package me.veso.attendanceservice.repository;

import me.veso.attendanceservice.entity.CategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryIdRepository extends JpaRepository<CategoryId, Long> {
    Optional<CategoryId> findByCategoryId(String id);

    void deleteByCategoryId(String categoryId);
}
