package erenes.org.narration;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/narration")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "narration")
public class NarrationResource {

    @Inject
    NarrationService service;

    @POST
    public Response narrate(Fight fight) throws Exception {
        String narration = service.narrate(fight);
        return Response.status(Response.Status.CREATED).entity(narration).build();
    }

    @GET
    @Path("/hello")
    public String hello() {
        return "Hello from Quarkus REST";
    }
}
