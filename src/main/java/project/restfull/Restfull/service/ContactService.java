package project.restfull.Restfull.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.restfull.Restfull.entity.Contact;
import project.restfull.Restfull.entity.User;
import project.restfull.Restfull.model.ContactResponse;
import project.restfull.Restfull.model.CreateContactRequest;
import project.restfull.Restfull.repository.ContactRepository;

import java.util.UUID;

@Service
public class ContactService {


    @Autowired
    ContactRepository contactRepository;

    @Autowired
    ValidatorService validatorService;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request) {
        validatorService.validate(request);

        Contact contact = new Contact();

        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFisrtname());
        contact.setLastName(request.getLastname());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact);

        return tocontactResponse(contact);
    }

    private ContactResponse tocontactResponse(Contact contact) {
     return ContactResponse.builder()
                .id(contact.getId())
                .firstname(contact.getFirstName())
                .lastname(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();

    }


    public ContactResponse get(User user, String id){
        Contact contact = contactRepository.findFirstByUserAndId(user,id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not found"));

        return tocontactResponse(contact);

    }
}
