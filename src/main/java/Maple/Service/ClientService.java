package Maple.Service;

import Maple.Dto.CountryDto;
import Maple.Entity.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.UUID;

/**
 * Business logic layer for Client operations
 * Handles DTO-Entity conversion and external API integration
 */
@ApplicationScoped
public class ClientService {

    @Inject
    @RestClient
    RestCountriesClient restCountriesClient;

    /**
     * Fetches the demonym for a given country code from the RestCountries API
     * @param countryCode ISO 3166-1 country code (e.g., "US", "ES")
     * @return The English demonym (e.g., "American", "Spanish") or null if not found
     */
    private String fetchDemonym(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) {
            return null;
        }
        
        try {
            List<CountryDto> countries = restCountriesClient.getCountryByCode(countryCode);
            if (countries != null && !countries.isEmpty()) {
                return countries.get(0).getDemonym();
            }
        } catch (Exception e) {
            System.err.println("Error fetching demonym for country '" + countryCode + "': " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Creates a new client with automatic demonym enrichment
     * Validates email uniqueness (case-insensitive) and fetches the country's demonym
     * @return The persisted client with auto-generated ID and demonym
     */
    @Transactional
    public Client create(Client client) {
        Client.find("email", client.email).firstResultOptional().ifPresent(existing -> {
            throw new IllegalArgumentException("Client with email " + client.email + " already exists.");
        });
  
        if (client.country != null) {
            String demonym = fetchDemonym(client.country);
            if (demonym != null) {
                client.demonym = demonym;
            }
        }
        
        client.persist();
        
        return client;
    }

    /**
     * Retrieves all clients from the database
     * @return List of all client entities
     */
    public List<Client> findAll() {
        return Client.listAll();
    }

    /**
     * Retrieves all clients belonging to a specific country
     * @return List of clients from the specified country
     */
    public List<Client> findByCountry(String country) {
        return Client.list("country", country);
    }

    /**
     * Finds a client by their unique identifier
     * @return The client entity or null if not found
     */
    public Client findById(UUID id) {
        return Client.findById(id);
    }

    /**
     * Updates an existing client's modifiable fields (email, address, phone, country)
     * Re-validates email uniqueness and refreshes demonym if country changes
     * @return The updated client entity or null if not found
     */
    @Transactional
    public Client update(UUID id, Client updatedClient) {
        Client.find("email", updatedClient.email).firstResultOptional().ifPresent(existing -> {
            Client existingClient = (Client) existing;
            if(!existingClient.id.equals(id)) {
                throw new IllegalArgumentException("Client with email " + updatedClient.email + " already exists.");
            }
        });

        Client client = Client.findById(id);
        if (client == null) {
            return null;
        }

        client.email = updatedClient.email;
        client.address = updatedClient.address;
        client.phone = updatedClient.phone;
        client.country = updatedClient.country;

        if (client.country != null) {
            String demonym = fetchDemonym(client.country);
            if (demonym != null) {
                client.demonym = demonym;
            }
        }

        client.persist();
        
        return client;
    }

    /**
     * Deletes a client by their unique identifier
     * @return true if the client was deleted, false if not found
     */
    @Transactional
    public boolean delete(UUID id) {
        return Client.deleteById(id);
    }
}
