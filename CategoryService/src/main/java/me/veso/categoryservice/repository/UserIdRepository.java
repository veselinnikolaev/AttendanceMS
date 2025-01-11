package me.veso.categoryservice.repository;

import me.veso.categoryservice.entity.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserIdRepository extends JpaRepository<UserId, Long> {
    Optional<UserId> findByUserId(Long id);
}
