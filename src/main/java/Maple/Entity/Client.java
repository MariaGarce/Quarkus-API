package Maple.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.UUID;
import Maple.Dto.ClientDto;

/**
 * JPA Entity representing a client in the database
 * Uses Panache Active Record pattern for simplified database operations
 */
@Entity
@Table(name = "clients")
public class Client extends PanacheEntityBase {
    
    /** Unique identifier (auto-generated UUID) */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    /** Client's first name */
    @Column(name = "first_name", nullable = false)
    public String firstName;

    /** Client's middle name (optional) */
    @Column(name = "middle_name")
    public String middleName;

    /** Client's last name */
    @Column(name = "last_name", nullable = false)
    public String lastName;

    /** Client's second last name (optional) */
    @Column(name = "second_last_name")
    public String secondLastName;

    /** Client's email address (unique, stored in lowercase for case-insensitive comparison) */
    @Column(nullable = false, unique = true)
    public String email;

    /** Client's physical address */
    @Column(nullable = false)
    public String address;

    /** Client's phone number */
    @Column(nullable = false)
    public String phone;

    /** ISO 3166-1 country code (2-3 characters) */
    @Column(nullable = false, length = 2)
    public String country;

    /** Country demonym (auto-populated from RestCountries API) */
    @Column
    public String demonym;

    /**
     * Converts a ClientDto to a Client entity
     * Used for creating/updating client records from API requests
     * Email is automatically normalized to lowercase for case-insensitive uniqueness
     * Note: id and demonym are not copied as they are auto-generated
     * 
     * @param dto The ClientDto to convert
     * @return A new Client entity or null if dto is null
     */
    public static Client toEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }

        Client client = new Client();
        client.firstName = dto.firstName;
        client.middleName = dto.middleName;
        client.lastName = dto.lastName;
        client.secondLastName = dto.secondLastName;
        client.email = dto.email.toLowerCase(); // Normalize email to lower case
        client.address = dto.address;
        client.phone = dto.phone;
        client.country = dto.country;

        return client;
    }

}