package com.ivansaprykin.testtasks.bostongene.springuserservice.service;

import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.UserDoesNotExistException;
import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.UserWithSuchEmailAlreadyExistException;
import com.ivansaprykin.testtasks.bostongene.springuserservice.model.SimplifiedUser;
import com.ivansaprykin.testtasks.bostongene.springuserservice.model.User;
import com.ivansaprykin.testtasks.bostongene.springuserservice.repositories.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public HttpStatus deleteUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            userRepository.deleteById(optionalUser.get().getId());
            return HttpStatus.NO_CONTENT;
        } else {
            return HttpStatus.NOT_FOUND;
        }
    }

    public void addUser(SimplifiedUser userToAdd) throws UserWithSuchEmailAlreadyExistException {
        User user = new User(userToAdd);
        try {
            userRepository.save(user);
        } catch (DataAccessException ex) {
            throw new UserWithSuchEmailAlreadyExistException("User with email: " + user.getEmail() + " already exists!"); // TODO remove root cause
        }
    }

    public SimplifiedUser getUserByEmail(String email) throws UserDoesNotExistException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            return new SimplifiedUser(optionalUser.get());
        } else {
            throw new UserDoesNotExistException("No user with email: " + email);
        }
    }
}
