package com.ivansaprykin.testtasks.bostongene.springuserservice.service;

import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.UserDoesNotExistException;
import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.UserWithSuchEmailAlreadyExistException;
import com.ivansaprykin.testtasks.bostongene.springuserservice.model.SimplifiedUser;
import com.ivansaprykin.testtasks.bostongene.springuserservice.model.User;
import com.ivansaprykin.testtasks.bostongene.springuserservice.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        User user = new User(userToAdd, passwordEncoder.encode(userToAdd.getPassword()));
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail() );
        if(optionalUser.isPresent()) {
            throw new UserWithSuchEmailAlreadyExistException("User with email: " + user.getEmail() + " already exists.");
        } else {
            userRepository.save(user);
        }
    }

    public SimplifiedUser getUserByEmail(String email) throws UserDoesNotExistException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            return new SimplifiedUser(optionalUser.get());
        } else {
            throw new UserDoesNotExistException("User with email: " + email + " does not exist.");
        }
    }
}
