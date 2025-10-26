package Maple.Service;

import Maple.Dto.CountryDto;
import Maple.Entity.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClientService {

    @Inject
    @RestClient
    RestCountriesClient restCountriesClient;

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
    public Client update(UUID id, Client updated) {
        Client client = Client.findById(id);
        if (client == null) {
            return null;
        }

        client.email = updated.email;
        client.address = updated.address;
        client.phone = updated.phone;
        client.country = updated.country;

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
