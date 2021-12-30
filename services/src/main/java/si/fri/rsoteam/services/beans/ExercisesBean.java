package si.fri.rsoteam.services.beans;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rsoteam.entities.ExerciseEntity;
import si.fri.rsoteam.lib.dtos.ExerciseDto;
import si.fri.rsoteam.services.mappers.StepMapper;
import si.fri.rsoteam.services.mappers.ExerciseMapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class ExercisesBean {
    private Logger log = Logger.getLogger(ExercisesBean.class.getName());

    @Inject
    private EntityManager em;
    @Inject
    private StepsBean stepsBean;


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

    public ExerciseDto createExercise(ExerciseDto exerciseDto) {
        ExerciseEntity exerciseEntity = ExerciseMapper.dtoToEntity(exerciseDto);
        this.beginTx();
        em.persist(exerciseEntity);
        this.commitTx();

        return ExerciseMapper.entityToDto(exerciseEntity);
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
