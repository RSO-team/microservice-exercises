package si.fri.rsoteam.api.v1.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rsoteam.lib.dtos.ExerciseDto;
import si.fri.rsoteam.services.beans.ExercisesBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.Optional;

@ApplicationScoped
@Path("/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExercisesResource {

    private Logger log = LogManager.getLogger(ExercisesResource.class.getName());

    @Inject
    @DiscoverService(value = "basketball-videos")
    private Provider<Optional<WebTarget>> url;

    @Inject
    private ExercisesBean exercisesBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Operation(summary = "Get list of exercises", description = "Returns list of exercises.")
    @APIResponses({
            @APIResponse(
                    description = "list of exercises",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ExerciseDto.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )
    })
    @Log(LogParams.METRICS)
    public Response getExercises() {
        return Response.ok(exercisesBean.getAllExercises()).build();
    }

    @GET
    @Path("/basketball")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Get url for videos service", description = "Discover video service.")
    @APIResponses({
            @APIResponse(
                    description = "Successfully discover video service. ",
                    responseCode = "200"
            )
    })
    @Log(LogParams.METRICS)
    public Response getBasket() {
        Optional<WebTarget> optionalUrl = url.get();
        if (optionalUrl.isPresent()) {
            return Response.ok(optionalUrl.get().getUri().toString()).build();
        } else {
            return Response.noContent().build();
        }
    }

    @GET
    @Operation(summary = "Get exercise by id", description = "Returns a specific exercise by id")
    @APIResponses({
            @APIResponse(
                    description = "specific exercise",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ExerciseDto.class, type = SchemaType.ARRAY))
            )
    })
    @Path("/{objectId}")
    @Log(LogParams.METRICS)
    public Response getExerciseById(@PathParam("objectId") Integer id) {
        return Response.ok(exercisesBean.getExercise(id)).build();
    }

    @POST
    @Operation(summary = "Create new exercise", description = "Creates a new exercise")
    @APIResponses({
            @APIResponse(
                    description = "Successfully created exercise",
                    responseCode = "201"
            )
    })
    @Log(LogParams.METRICS)
    public Response createExercise(ExerciseDto exerciseDto) throws IOException {
        return Response.status(201).entity(exercisesBean.createExercise(exerciseDto)).build();
    }

    @PUT
    @Path("{objectId}")
    @Operation(summary = "Updates specific exercise", description = "Updates specific exercise")
    @APIResponses({
            @APIResponse(
                    description = "Successfully created exercise",
                    responseCode = "201"
            )
    })
    @Log(LogParams.METRICS)
    public Response updateExercise(@PathParam("objectId") Integer id, ExerciseDto eventDto) {
        return Response.status(201).entity(exercisesBean.updateExercise(eventDto, id)).build();
    }

    @DELETE
    @Operation(summary = "Delete specific exercise", description = "Deletes specific exercise")
    @APIResponses({
            @APIResponse(
                    description = "Successfully created exercise",
                    responseCode = "204"
            )
    })
    @Path("{objectId}")
    @Log(LogParams.METRICS)
    public Response deleteEvent(@PathParam("objectId") Integer id) {
        exercisesBean.deleteExercise(id);
        return Response.status(204).build();
    }
}
