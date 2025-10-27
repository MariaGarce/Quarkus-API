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
     * Fetch demonym from RestCountries API
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

    public List<Client> findAll() {
        return Client.listAll();
    }

    public List<Client> findByCountry(String country) {
        return Client.list("country", country);
    }

    public Client findById(UUID id) {
        return Client.findById(id);
    }

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

    @Transactional
    public boolean delete(UUID id) {
        return Client.deleteById(id);
    }
}
