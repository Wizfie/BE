package project.restfull.Restfull.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateContactRequest {

    @NotBlank
    @Size(max = 100)
    private String fisrtname;

    @Size(max = 100)
    private String lastname;

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 100)
    private String phone;



}
