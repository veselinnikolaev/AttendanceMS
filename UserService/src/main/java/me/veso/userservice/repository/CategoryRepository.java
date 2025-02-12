package me.veso.userservice.repository;

import me.veso.userservice.entity.CategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryId, Long> {
    Optional<CategoryId> findByCategoryId(String categoryId);

    void delete(String categoryId);
}
