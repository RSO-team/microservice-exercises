package si.fri.rsoteam.services.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rsoteam.entities.ExerciseEntity;
import si.fri.rsoteam.lib.dtos.ExerciseDto;
import si.fri.rsoteam.lib.dtos.VideoDto;
import si.fri.rsoteam.services.config.RestConfig;
import si.fri.rsoteam.services.mappers.StepMapper;
import si.fri.rsoteam.services.mappers.ExerciseMapper;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequestScoped
public class ExercisesBean {
    private Logger log = LogManager.getLogger(ExercisesBean.class.getName());

    @Inject
    private EntityManager em;
    @Inject
    private StepsBean stepsBean;

    @Inject
    private RestConfig restConfig;

    @Inject
    @DiscoverService(value = "basketball-videos")
    private Optional<WebTarget> videosServiceUrl;

    private Client httpClient;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    @Timed
    public ExerciseDto getExercise(Integer id){
        return ExerciseMapper.entityToDto(em.find(ExerciseEntity.class,id));
    }

    @Timed
    public List<ExerciseDto> getAllExercises(){
        return em.createNamedQuery("Exercise.getAll",ExerciseEntity.class)
                .getResultList()
                .stream()
                .map(ExerciseMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public ExerciseDto createExercise(ExerciseDto exerciseDto) throws IOException {
        ExerciseEntity exerciseEntity = ExerciseMapper.dtoToEntity(exerciseDto);
        this.beginTx();
        em.persist(exerciseEntity);
        this.commitTx();

        if (exerciseDto.video != null) {
            createVideo(exerciseDto.video);
        }

        return ExerciseMapper.entityToDto(exerciseEntity);
    }

    private void createVideo(VideoDto videoDto) throws IOException {
        if (videosServiceUrl.isPresent()) {

            String host = String.format("%s/v1/videos",
                    videosServiceUrl.get().getUri());

            ObjectWriter ow = new ObjectMapper().writer();
            byte[] object = ow.writeValueAsBytes(videoDto);

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
            RequestBody body = RequestBody.create(object, mediaType);
            Request request = new Request.Builder()
                    .url(host)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("apiToken", String.format("%s", restConfig.getApiToken()))
                    .build();
            okhttp3.Response response = client.newCall(request).execute();

            log.info(response.toString());
        }
    }

    public ExerciseDto updateExercise(ExerciseDto exerciseDto, Integer id) {
        this.beginTx();

        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, id);
        exerciseEntity.setName(exerciseDto.name);
        exerciseEntity.setDescription(exerciseDto.description);
        exerciseEntity.setSteps(exerciseDto.steps.stream().map(StepMapper::dtoToEntity).collect(Collectors.toList()));
        exerciseEntity.getSteps().forEach(stepEntity -> stepEntity.setExercise(exerciseEntity));
        em.persist(exerciseEntity);
        this.commitTx();

        return ExerciseMapper.entityToDto(exerciseEntity);
    }

    public void deleteExercise(Integer id) {
        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, id);
        if (exerciseEntity != null) {

            //  em.createQuery("DELETE FROM exercise_step WHERE exercise_step.id IN ( SELECT exercise_step.id from exercise_step where exercise_step.exercise_id = ?1)")
            //                  .setParameter(1,exerciseEntity.getId()).getResultList();

            exerciseEntity.getSteps().forEach(stepEntity -> stepsBean.deleteStep(stepEntity.getId()));
            this.beginTx();
            em.remove(exerciseEntity);
            this.commitTx();
        }
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
