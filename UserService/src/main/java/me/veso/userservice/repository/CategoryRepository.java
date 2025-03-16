package me.veso.userservice.repository;

import me.veso.userservice.entity.CategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryId, Long> {
    Optional<CategoryId> findByCategoryId(String categoryId);

    @Modifying
    @Transactional
    @Query("UPDATE CategoryId c SET c.deleted = true WHERE c.categoryId = :categoryId")
    void deleteByCategoryId(String categoryId);
}
