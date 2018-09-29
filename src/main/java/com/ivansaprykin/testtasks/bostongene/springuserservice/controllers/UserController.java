package com.ivansaprykin.testtasks.bostongene.springuserservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.UserDoesNotExistException;
import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.UserWithSuchEmailAlreadyExistException;
import com.ivansaprykin.testtasks.bostongene.springuserservice.model.SimplifiedUser;
import com.ivansaprykin.testtasks.bostongene.springuserservice.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/add", consumes = {"application/JSON"})
    public ResponseEntity<?> addUser(@RequestBody SimplifiedUser user) throws UserWithSuchEmailAlreadyExistException {
        userService.addUser(user);

        HttpStatus status = HttpStatus.CREATED;
        HttpHeaders responseHeaders = new HttpHeaders();
        URI location = URI.create("/user/get?email="+ user.getEmail());
        responseHeaders.setLocation(location);

        return new ResponseEntity(responseHeaders, status);
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> deleteUserByEmail(@RequestParam String email) {
        // 204 (successful, no content) to indicate that the action has been successfully applied to the target resource
        // 404 (not found)  server did not find a current representation for the target resource
        // Idempotency doesn't imply that the status code will be the same.
        // What's relevant is the final state of the server.
        return ResponseEntity.status(userService.deleteUserByEmail(email)).build();
    }

    @GetMapping(value = "/get", produces = {"application/JSON"})
    public SimplifiedUser getUserByEmail(@RequestParam String  email) throws UserDoesNotExistException {
        return userService.getUserByEmail(email);
    }


}
