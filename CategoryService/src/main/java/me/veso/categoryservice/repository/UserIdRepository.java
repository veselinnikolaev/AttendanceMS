package me.veso.categoryservice.repository;

import me.veso.categoryservice.entity.UserId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserIdRepository extends MongoRepository<UserId, String> {
    Optional<UserId> findByUserId(Long id);
}
