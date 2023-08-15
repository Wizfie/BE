package project.restfull.Restfull.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.restfull.Restfull.entity.User;
import project.restfull.Restfull.model.AddressResponse;
import project.restfull.Restfull.model.CreateAddressRequest;
import project.restfull.Restfull.model.WebResponse;
import project.restfull.Restfull.service.AddressService;

@RestController
public class AddressController {


    @Autowired
    private AddressService addressService;



    @PostMapping(
            path = "/api/contact/{contactId}/addresses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(User user ,
                                               @RequestBody CreateAddressRequest request,
                                               @PathVariable("contactId") String contactId){
        request.setContactId(contactId);
        AddressResponse addressResponse = addressService.create(user, request);
        return WebResponse.<AddressResponse>builder()
                .data(addressResponse).build();

    }
}
