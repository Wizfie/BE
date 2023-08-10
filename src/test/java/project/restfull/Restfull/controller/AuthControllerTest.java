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
import project.restfull.Restfull.model.LoginUserRequest;
import project.restfull.Restfull.model.TokenResponse;
import project.restfull.Restfull.model.WebResponse;
import project.restfull.Restfull.repository.UserRepository;
import project.restfull.Restfull.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void LoginFailedNotFound() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("test");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });


    }

    @Test
    void LoginFailedWrongPassword() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setUsername("cek");
        user.setPassword(BCrypt.hashpw("test",BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("cek");
        request.setPassword("ssssssssss");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

        @Test
        void LoginSuccess() throws Exception {
            User user = new User();
            user.setName("test");
            user.setUsername("test");
            user.setPassword(BCrypt.hashpw("test",BCrypt.gensalt()));

            userRepository.save(user);

            LoginUserRequest request = new LoginUserRequest();
            request.setUsername("test");
            request.setPassword("test");

            mockMvc.perform(
                    post("/api/auth/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            ).andExpectAll(
                    status().isOk()
            ).andDo(result -> {
                WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
                assertNull(response.getErrors());
                assertNotNull(response.getData().getToken());
                assertNotNull(response.getData().getExpiredAt());

                User userDb = userRepository.findById("test").orElse(null);
                assertNotNull(userDb);
                assertEquals(userDb.getToken(), response.getData().getToken());
                assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
            });


        }


    }