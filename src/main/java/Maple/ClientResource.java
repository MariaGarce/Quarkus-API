package Maple;

import jakarta.transaction.Transactional;
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

    @POST
    @Transactional
    public Response create(@Valid Client client) {
        client.persist();
        return Response.status(Response.Status.CREATED).entity(client).build();
    }

    @GET
    @Path("/all")
    public Response listAll() {
        return Response.ok(Client.listAll()).build();
    }

    @GET
    @Path("/country/{country}")
    public List<Client> getByCountry(@PathParam("country") String country) {
        return Client.list("country", country);
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        Client client = Client.findById(id);
        if (client == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(client).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") UUID id, @Valid Client updated) {
        Client client = Client.findById(id);
        if (client == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        client.email = updated.email;
        client.address = updated.address;
        client.phone = updated.phone;
        client.country = updated.country;

        client.persist();
        return Response.ok(client).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = Client.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
