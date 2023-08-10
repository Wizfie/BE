package project.restfull.Restfull.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import project.restfull.Restfull.entity.User;
import project.restfull.Restfull.model.RegisterUserRequest;
import project.restfull.Restfull.model.TokenResponse;
import project.restfull.Restfull.model.UserResponse;
import project.restfull.Restfull.model.WebResponse;
import project.restfull.Restfull.repository.UserRepository;
import project.restfull.Restfull.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test");
        request.setPassword("test");
        request.setName("test");
        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))

        ).andExpectAll(
                status().isOk()
        ).andDo(result ->
        {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("OK", response.getData());
        });

    }

    @Test
    void testRegisterBad() throws Exception {

        RegisterUserRequest request = new RegisterUserRequest();
        request.setName("");
        request.setUsername("");
        request.setPassword("");
        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))

        ).andExpect(
                status().isBadRequest()
        ).andDo(result ->
        {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });

    }

    @Test
    void testRegisterDuplicate() throws Exception {

        User user = new User();

        user.setName("test");
        user.setUsername("test");
        user.setPassword("test");

        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setName("test");
        request.setUsername("test");
        request.setPassword("test");
        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))

        ).andExpect(
                status().isBadRequest()
        ).andDo(result ->
        {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });

    }

    @Test
    void getUserUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->
        {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserUnauthorizedTokenNotSend() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->
        {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserSuccess() throws Exception {

        User user = new User();
        user.setName("Test");
        user.setUsername("tes");
        user.setPassword(BCrypt.hashpw("test",BCrypt.gensalt()));
        user.setToken("tes");
        user.setTokenExpiredAt(System.currentTimeMillis() +  10000000000000000L );
        userRepository.save(user);



        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN","tes")
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->
        {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals("tes",response.getData().getUsername());
            assertEquals("Test",response.getData().getName());
        });
    }
}
