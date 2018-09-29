package com.ivansaprykin.testtasks.bostongene.springuserservice.repositories;

import com.ivansaprykin.testtasks.bostongene.springuserservice.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    void deleteByEmail(String email);
}
