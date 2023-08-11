package project.restfull.Restfull.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import project.restfull.Restfull.entity.User;
import project.restfull.Restfull.model.ContactResponse;
import project.restfull.Restfull.model.CreateContactRequest;
import project.restfull.Restfull.model.WebResponse;
import project.restfull.Restfull.service.ContactService;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;


    @PostMapping
            (
                    path = "/api/contact",
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE
            )
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request) {
        ContactResponse contactResponse = contactService.create(user, request);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();

    }

    @GetMapping(
            path = "/api/contact/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get(User user ,@PathVariable("contactId")String contacId){
        ContactResponse contactResponse = contactService.get(user, contacId);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }
    }
