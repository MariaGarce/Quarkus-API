package Maple.Resource;

import Maple.Entity.Client;
import Maple.Service.ClientService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    @Inject
    ClientService clientService;

    @POST
    public Response create(@Valid Client client) {
        Client created = clientService.create(client);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    public Response listAll() {
        List<Client> clients = clientService.findAll();
        return Response.ok(clients).build();
    }

    @GET
    @Path("/country/{country}")
    public Response getByCountry(@PathParam("country") String country) {
        List<Client> clients = clientService.findByCountry(country);
        return Response.ok(clients).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        Client client = clientService.findById(id);
        if (client == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(client).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid Client updated) {
        Client client = clientService.update(id, updated);
        if (client == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(client).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = clientService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}

