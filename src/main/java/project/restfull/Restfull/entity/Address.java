package project.restfull.Restfull.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    private String id;
    @Column(name = "contact_id", insertable = false, updatable = false)
    private String contactId;
    private String street;
    private String city;
    private String province;
    @Column(name = "postal_code")
    private String postalCode;
    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    private Contact contact;

}
