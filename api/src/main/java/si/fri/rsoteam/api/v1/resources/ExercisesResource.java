package si.fri.rsoteam.api.v1.resources;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
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
import java.net.URL;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExercisesResource {

    private Logger log = Logger.getLogger(ExercisesResource.class.getName());

    @Inject
    @DiscoverService(value = "basketball-videos", environment = "dev", version = "1.0.0")
    private Provider<Optional<WebTarget>> url;

    @Inject
    private ExercisesBean exercisesBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getExercises() {
        return Response.ok(exercisesBean.getAllExercises()).build();
    }

    @GET
    @Path("/basketball")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getBasket() {
        Optional<WebTarget> optionalUrl=url.get();
        if(optionalUrl.isPresent()){
            return Response.ok(optionalUrl.get().getUri().toString()).build();
        }else{
            return Response.noContent().build();
        }
    }


    @GET
    @Path("/{objectId}")
    public Response getExerciseById(@PathParam("objectId") Integer id) {
        return Response.ok(exercisesBean.getExercise(id)).build();
    }

    @POST
    public Response createExercise(ExerciseDto exerciseDto) {
        return Response.status(201).entity(exercisesBean.createExercise(exerciseDto)).build();
    }

    @PUT
    @Path("{objectId}")
    public Response updateExercise(@PathParam("objectId") Integer id, ExerciseDto eventDto) {
        return Response.status(201).entity(exercisesBean.updateExercise(eventDto, id)).build();
    }

    @DELETE
    @Path("{objectId}")
    public Response deleteEvent(@PathParam("objectId") Integer id) {
        exercisesBean.deleteExercise(id);
        return Response.status(204).build();
    }
}
