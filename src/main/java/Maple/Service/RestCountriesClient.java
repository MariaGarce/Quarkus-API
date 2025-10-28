package Maple.Service;

import Maple.Dto.CountryDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * MicroProfile REST Client for the RestCountries API
 * Provides access to country information including demonyms
 * 
 * Base URL: https://restcountries.com/v3.1
 * Documentation: https://restcountries.com
 */
@RegisterRestClient(configKey = "restcountries-api")
public interface RestCountriesClient {

    /**
     * Retrieves country information by ISO 3166-1 country code
     * 
     * @param code ISO country code (2 or 3 characters, e.g., "US", "ESP")
     * @return List of country data including demonyms in multiple languages
     */
    @GET
    @Path("/alpha/{code}")
    List<CountryDto> getCountryByCode(@PathParam("code") String code);
}
