package Maple.Service;

import Maple.Dto.CountryDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "restcountries-api")
public interface RestCountriesClient {

    @GET
    @Path("/alpha/{code}")
    List<CountryDto> getCountryByCode(@PathParam("code") String code);
}
