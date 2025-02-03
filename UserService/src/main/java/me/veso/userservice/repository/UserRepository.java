package me.veso.userservice.repository;

import me.veso.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.status = :status, u.processedAt = :processedAt WHERE u.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status, @Param("processedAt") LocalDateTime processedAt);

    List<User> findAllByStatus(String status);

    Optional<User> findByUsername(String username);

    List<User> findAllByIdIn(List<Long> ids);

    @Query("SELECT u FROM User u JOIN u.categories c WHERE c.categoryId = :categoryId")
    List<User> findAllByCategoryId(@Param("categoryId") String categoryId);
}
