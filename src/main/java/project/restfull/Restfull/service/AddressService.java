package project.restfull.Restfull.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import project.restfull.Restfull.entity.Address;
import project.restfull.Restfull.entity.Contact;
import project.restfull.Restfull.entity.User;
import project.restfull.Restfull.model.AddressResponse;
import project.restfull.Restfull.model.CreateAddressRequest;
import project.restfull.Restfull.repository.AddressRepository;
import project.restfull.Restfull.repository.ContactRepository;

import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired ValidatorService validatorService;

    @Autowired
    private ContactRepository  contactRepository;


    @Transactional
    public AddressResponse create(User user , CreateAddressRequest request){
        validatorService.validate(request);
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Not found"));

        Address address = new Address();

        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address);

        return toAddressResponse(address);

    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
    @Transactional(readOnly = true)
    public AddressResponse get(User user,String contactId , String addressId){
        Contact contact = contactRepository.findFirstByUserAndId(user , contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Address is not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));

        return toAddressResponse(address);
    }
}
