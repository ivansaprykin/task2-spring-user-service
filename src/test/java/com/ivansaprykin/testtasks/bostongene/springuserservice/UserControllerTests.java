package com.ivansaprykin.testtasks.bostongene.springuserservice;

import com.ivansaprykin.testtasks.bostongene.springuserservice.model.SimplifiedUser;
import com.ivansaprykin.testtasks.bostongene.springuserservice.service.UserService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Random;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private static ObjectMapper mapper;

    @BeforeClass
    public static void createObjectMapper() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    public void in_getUser_when_UserExists_should_ReturnUserAsJson() throws Exception {
        SimplifiedUser user = createUniqueTestUser();
        userService.addUser(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/get")
                .param("email", user.getEmail())
                .accept(MediaType.APPLICATION_JSON);

        user.setPassword("");
        String userShouldBeReturnedWithoutPassword = serializeUserToJson(user);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(userShouldBeReturnedWithoutPassword));
    }

    @Test
    public void in_getUser_when_UserExists_should_ReturnHttp200() throws Exception {
        SimplifiedUser user = createUniqueTestUser();
        userService.addUser(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/get")
                .param("email", user.getEmail())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

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
        SimplifiedUser user = createUniqueTestUser();
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user/add")
                .content(serializeUserToJson(user))
                .contentType("application/JSON")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(header().string ("location", "/user/get?email="+ user.getEmail()))
                .andExpect(status().isCreated());
    }

    @Test
    public void in_addUser_when_AddingExistentUser_should_ReturnHttp409() throws Exception {
        SimplifiedUser user = createUniqueTestUser();
        userService.addUser(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user/add")
                .content(serializeUserToJson(user))
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
    public void in_addUser_when_InvalidJsonInput_should_ReturnHttp400() throws Exception {
        String  invalidJson = serializeUserToJson(createUniqueTestUser()).replace(LocalDate.now().toString(), "wrong!");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user/add")
                .contentType("application/JSON")
                .content(invalidJson)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", startsWith("Error processing JSON")));
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
        SimplifiedUser user = createUniqueTestUser();
        userService.addUser(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/user/delete")
                .param("email", user.getEmail())
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

    private SimplifiedUser createUniqueTestUser() {
        Random rand = new Random();
        String uniqueEmail =  "my" + rand.nextLong() + "@mail" + rand.nextLong() + ".com";
        SimplifiedUser user = new SimplifiedUser("Jane", "Doe", LocalDate.now(), uniqueEmail, "qwerty");
        return user;
    }

    private String serializeUserToJson(SimplifiedUser user) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
    }

}
