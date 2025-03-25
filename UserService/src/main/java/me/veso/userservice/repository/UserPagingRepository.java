package me.veso.userservice.repository;

import me.veso.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPagingRepository extends PagingAndSortingRepository<User, Long> {
    Page<User> findAll(Pageable pageable);

    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);
}
