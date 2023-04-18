package com.user.repository;

import com.user.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Component
public interface UserRepository extends PagingAndSortingRepository<User, UUID> {
    boolean existsByLogin (String login);
    User findByLogin (String login);

}
