package project.restfull.Restfull.controller;

;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.test.web.servlet.result.*;
import project.restfull.Restfull.entity.Contact;
import project.restfull.Restfull.entity.User;
import project.restfull.Restfull.model.AddressResponse;
import project.restfull.Restfull.model.CreateAddressRequest;
import project.restfull.Restfull.model.WebResponse;
import project.restfull.Restfull.repository.AddressRepository;
import project.restfull.Restfull.repository.ContactRepository;
import project.restfull.Restfull.repository.UserRepository;
import project.restfull.Restfull.security.BCrypt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();


        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setToken("tes");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000000L);
        userRepository.save(user);


        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId("UID");
        contact.setFirstName("nana");
        contact.setLastName("gajah");
        contact.setEmail("nana@example.com");
        contact.setPhone("11111");
        contactRepository.save(contact);
    }

    @Test
    void createAddressBadReq() throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                post("/api/contact/test/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "tes")

        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNotNull(response.getErrors());


                }
        );
    }

    @Test
    void createAddressSuccess() throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("jalan");
        request.setCity("tng");
        request.setProvince("banten");
        request.setPostalCode("54421");
        request.setCountry("indonesia");

        mockMvc.perform(
                post("/api/contact/UID/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "tes")

        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertEquals(request.getStreet(), response.getData().getStreet());
                    assertEquals(request.getCity(), response.getData().getCity());
                    assertEquals(request.getProvince(), response.getData().getProvince());
                    assertEquals(request.getCountry(), response.getData().getCountry());
                    assertEquals(request.getPostalCode(), response.getData().getPostalCode());

                    assertTrue(addressRepository.existsById(response.getData().getId()));


                }
        );
    }
    @Test
    void getAddressNotFound() throws Exception {

        mockMvc.perform(
                get("/api/contact/test/addresses/ddd")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "tes")

        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNotNull(response.getErrors());


                }
        );
    }
}

