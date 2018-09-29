package com.ivansaprykin.testtasks.bostongene.springuserservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions.UserWithSuchEmailAlreadyExistException;
import com.ivansaprykin.testtasks.bostongene.springuserservice.model.SimplifiedUser;
import com.ivansaprykin.testtasks.bostongene.springuserservice.model.User;
import com.ivansaprykin.testtasks.bostongene.springuserservice.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Random;
import java.util.logging.SimpleFormatter;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerExceptionsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void in_getUser_when_MissingRequestParameter_should_ReturnHttp400() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/get")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", endsWith("parameter is missing.")));
    }

    @Test
    public void in_getUser_when_UserNotExist_should_ReturnHttp404() throws Exception {
        String  uniqueEmail = "mailproton@com";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/get")
                .param("email", uniqueEmail);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void in_getUser_when_UsedUnsupportedMethods_should_ReturnHttp405() throws Exception {
        String addUserUrl = "/user/get";

        mockMvc.perform(post(addUserUrl))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(put(addUserUrl))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete(addUserUrl))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void in_addUser_when_AddingNewUser_should_ReturnHttp201() throws Exception {
        SimplifiedUser userToAdd = new SimplifiedUser("ivan", "ivanov", LocalDate.now(), "sm", "qwerty");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user/add")
                .content(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userToAdd))
                .contentType("application/JSON")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(header().string ("location", "/user/get?email="+ userToAdd.getEmail()))
                .andExpect(status().isCreated());
    }

    @Test
    public void in_addUser_when_AddingExistentUser_should_ReturnHttp409() throws Exception {
        String uniqueEmail = "mymailat@com";
        SimplifiedUser userToUpdate = new SimplifiedUser("Alex", "Doe", LocalDate.now(), uniqueEmail, "qwerty");
        userService.addUser(userToUpdate);
        userToUpdate.setFirstName("John");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String userSerializedInJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userToUpdate);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user/add")
                .content(userSerializedInJson)
                .contentType("application/JSON")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict());
    }

    @Test
    public void in_addUser_when_WrongMediaType_should_ReturnHttp415() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user/add")
                .contentType("application/xml")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message", startsWith("Unsupported  media type")));
    }

    @Test
    public void in_addUser_when_UsedUnsupportedMethods_should_ReturnHttp405() throws Exception {
        String addUserUrl = "/user/add";

        mockMvc.perform(get(addUserUrl))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(put(addUserUrl))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete(addUserUrl))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void in_deleteUser_when_DeletingExistentUser_should_ReturnHttp204() throws Exception {
        String  uniqueEmail = "myhotmail@ru";
        SimplifiedUser user = new SimplifiedUser("James", "Potter", LocalDate.now(), uniqueEmail, "pass");
        userService.addUser(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/user/delete")
                .param("email", uniqueEmail)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    public void in_deleteUser_when_DeletingNonExistentUser_should_ReturnHttp404() throws Exception {
        String  uniqueEmail = "protonmail@com";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/user/delete")
                .param("email", uniqueEmail);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void in_deleteUser_with_MissingRequestParameter_should_ReturnHttp400() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/user/delete")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", endsWith("parameter is missing.")));
    }

    @Test
    public void in_deleteUser_when_UsedUnsupportedMethods_should_ReturnHttp405() throws Exception {
        String deleteUserUrl = "/user/delete";

        mockMvc.perform(get(deleteUserUrl))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(put(deleteUserUrl))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(post(deleteUserUrl))
                .andExpect(status().isMethodNotAllowed());
    }

}
