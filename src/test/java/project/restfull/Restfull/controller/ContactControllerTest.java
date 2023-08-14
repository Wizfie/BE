package project.restfull.Restfull.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import project.restfull.Restfull.entity.Contact;
import project.restfull.Restfull.entity.User;
import project.restfull.Restfull.model.ContactResponse;
import project.restfull.Restfull.model.CreateContactRequest;
import project.restfull.Restfull.model.UpdateContactRequest;
import project.restfull.Restfull.model.WebResponse;
import project.restfull.Restfull.repository.ContactRepository;
import project.restfull.Restfull.repository.UserRepository;
import project.restfull.Restfull.security.BCrypt;
import project.restfull.Restfull.service.ContactService;


import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactRepository contactRepository;

    @BeforeEach
    void setUp() {

        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("tes1");
        user.setUsername("tes");
        user.setPassword(BCrypt.hashpw("pass", BCrypt.gensalt()));
        user.setToken("tes");
        user.setTokenExpiredAt(System.currentTimeMillis() + 11111111111111111L);
        userRepository.save(user);
    }

    @Test
    void createContactBadReq() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFisrtname("");
        request.setEmail("salah");


        mockMvc.perform(
                post("/api/contact")
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
    void createContactSuccess() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFisrtname("wis");
        request.setLastname("ss");
        request.setEmail("wis@example.com");
        request.setPhone("1111");


        mockMvc.perform(
                post("/api/contact")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "tes")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertEquals("wis", response.getData().getFirstname());
                    assertEquals("ss", response.getData().getLastname());
                    assertEquals("wis@example.com", response.getData().getEmail());
                    assertEquals("1111", response.getData().getPhone());

                    assertTrue(contactRepository.existsById(response.getData().getId()));


                }

        );
    }

    @Test
    void getContactNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contact/111111111111")
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

    @Test
    void getContactSuccess() throws Exception {

        User user = userRepository.findById("tes").orElseThrow();

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("nana");
        contact.setLastName("gajah");
        contact.setEmail("nana@example.com");
        contact.setPhone("11111");
        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contact/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "tes")

        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertEquals(contact.getId(), response.getData().getId());
                    assertEquals(contact.getFirstName(), response.getData().getFirstname());
                    assertEquals(contact.getLastName(), response.getData().getLastname());
                    assertEquals(contact.getEmail(), response.getData().getEmail());
                    assertEquals(contact.getPhone(), response.getData().getPhone());

                }

        );


    }

    @Test
    void updateContactBadRequest() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFisrtname("");
        request.setEmail("salah");


        mockMvc.perform(
                put("/api/contact/1234")
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
    void updateContactSuccess() throws Exception {

        User user = userRepository.findById("tes").orElseThrow();

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("saber");
        contact.setLastName("roam");
        contact.setEmail("saber@example.com");
        contact.setPhone("12222");
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFisrtname("wis");
        request.setLastname("fie");
        request.setEmail("wis@example.com");
        request.setPhone("1234");


        mockMvc.perform(
                put("/api/contact/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "tes")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertEquals(request.getFisrtname(), response.getData().getFirstname());
                    assertEquals(request.getLastname(), response.getData().getLastname());
                    assertEquals(request.getEmail(), response.getData().getEmail());
                    assertEquals(request.getPhone(), response.getData().getPhone());

                    assertTrue(contactRepository.existsById(response.getData().getId()));
                }
        );
    }

    @Test
    void deleteContactNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/contact/111111111111")
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
    @Test
    void deleteContactSuccess() throws Exception {

        User user = userRepository.findById("tes").orElseThrow();

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("nana");
        contact.setLastName("gajah");
        contact.setEmail("nana@example.com");
        contact.setPhone("11111");
        contactRepository.save(contact);


        mockMvc.perform(
                delete("/api/contact/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "tes")

        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertEquals("OK", response.getData());

                }

        );


    }

    @Test
    void searchNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contact")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "tes")

        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertEquals( 0,response.getData().size());
                    assertEquals( 0,response.getPaging().getCurrentPage());
                    assertEquals( 0,response.getPaging().getTotalPage());
                    assertEquals( 10,response.getPaging().getSize());

                }

        );


    }

    @Test
    void searchName() throws Exception {

        User user = userRepository.findById("tes").orElseThrow();


        for (int i = 0; i < 100; i++) {

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("nana" + i);
        contact.setLastName("gajah");
        contact.setEmail("nana@example.com");
        contact.setPhone("11111");
        contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contact")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("name","nana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "tes")

        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertNull(response.getErrors());
                    assertEquals( 0,response.getData().size());
                    assertEquals( 0,response.getPaging().getCurrentPage());
                    assertEquals( 0,response.getPaging().getTotalPage());
                    assertEquals( 10,response.getPaging().getSize());

                }

        );


    }
}