package Maple.Dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import Maple.Entity.Client;

/**
 * Data Transfer Object for Client
 * Separates API layer from persistence layer
 * ID and demonym are excluded - they are auto-generated
 */
public class ClientDto {

    public UUID id;

    @NotBlank(message = "First name is required")
    public String firstName;

    public String middleName;

    @NotBlank(message = "Last name is required")
    public String lastName;

    public String secondLastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    public String email;

    @NotBlank(message = "Address is required")
    public String address;

    @NotBlank(message = "Phone is required")
    public String phone;

    @NotBlank(message = "Country code is required")
    @Size(min = 2, max = 3, message = "Country code must be 2-3 characters (ISO 3166-1)")
    public String country;

    public String demonym;

    // Constructors
    public ClientDto() {
    }

    public ClientDto(Client client) {
        this.id = client.id;
        this.firstName = client.firstName;
        this.middleName = client.middleName;
        this.lastName = client.lastName;
        this.secondLastName = client.secondLastName;
        this.email = client.email;
        this.address = client.address;
        this.phone = client.phone;
        this.country = client.country;
        this.demonym = client.demonym;
    }
}
