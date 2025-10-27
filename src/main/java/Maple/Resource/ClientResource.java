package Maple.Resource;

import Maple.Dto.ClientDto;
import Maple.Entity.Client;
import Maple.Service.ClientService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * REST API endpoints for Client management
 * All endpoints include exception handling for robustness
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    @Inject
    ClientService clientService;

    /**
     * Create a new customer
     * Demonym is auto-populated from RestCountries API
     * 
     * @param clientDto Client data (without ID and demonym)
     * @return 201 Created with the created client including ID and demonym
     */
    @POST
    public Response create(@Valid ClientDto clientDto) {
        try {
            Client entity = Client.toEntity(clientDto);
            Client created = clientService.create(entity);
            return Response.status(Response.Status.CREATED).entity(new ClientDto(created)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error creating client: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get all existing customers
     * 
     * @return 200 OK with list of all clients
     */
    @GET
    public Response listAll() {
        try {
            List<Client> clients = clientService.findAll();
            List<ClientDto> clientDtos = new ArrayList<ClientDto>();
            for (Client client : clients) {
                clientDtos.add(new ClientDto(client));
            }
            return Response.ok(clientDtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving clients: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get all existing customers who belong to a specific country
     * 
     * @param country ISO 3166-1 country code (2-3 characters)
     * @return 200 OK with filtered list of clients
     */
    @GET
    @Path("/country/{country}")
    public Response getByCountry(@PathParam("country") String country) {
        try {
            List<Client> clients = clientService.findByCountry(country);
            List<ClientDto> clientDtos = new ArrayList<ClientDto>();
            for (Client client : clients) {
                clientDtos.add(new ClientDto(client));
            }
            return Response.ok(clientDtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving clients by country: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get a specific customer by their identifier
     * 
     * @param id Client UUID
     * @return 200 OK with client data, or 404 Not Found
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        try {
            Client client = clientService.findById(id);
            if (client == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Client not found"))
                        .build();
            }
            return Response.ok(new ClientDto(client)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error retrieving client: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Update an existing customer
     * Only allows modification of email, address, phone, and country
     * Demonym is auto-updated when country changes
     * 
     * @param id Client UUID
     * @param updatedDto Updated client data (without id/demonym)
     * @return 200 OK with updated client, or 404 Not Found
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid ClientDto updatedDto) {
        try {
            Client client = clientService.update(id, Client.toEntity(updatedDto));
            if (client == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Client not found"))
                        .build();
            }
            return Response.ok(new ClientDto(client)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error updating client: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete a customer by their identifier
     * 
     * @param id Client UUID
     * @return 204 No Content on success, or 404 Not Found
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        try {
            boolean deleted = clientService.delete(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Client not found"))
                        .build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error deleting client: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Simple error response class
     */
    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
