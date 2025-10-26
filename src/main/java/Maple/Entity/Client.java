package Maple.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "first_name", nullable = false)
    @NotBlank
    public String firstName;

    @Column(name = "middle_name")
    public String middleName;

    @Column(name = "last_name", nullable = false)
    @NotBlank
    public String lastName;

    @Column(name = "second_last_name")
    public String secondLastName;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Email
    public String email;

    @Column(nullable = false)
    @NotBlank
    public String address;

    @Column(nullable = false)
    @NotBlank
    public String phone;

    @Column(nullable = false, length = 2)
    @NotBlank
    @Size(min = 2, max = 3) // ISO codes can be 2 or 3 characters
    public String country;

    @Column
    public String demonym;
}