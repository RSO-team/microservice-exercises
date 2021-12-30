package si.fri.rsoteam.services.beans;

import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rsoteam.entities.StepEntity;
import si.fri.rsoteam.entities.StepEntity;
import si.fri.rsoteam.lib.dtos.StepDto;
import si.fri.rsoteam.services.mappers.StepMapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class StepsBean {
    private Logger log = Logger.getLogger(StepsBean.class.getName());

    @Inject
    private EntityManager em;

    @Timed
    public StepDto getStep(Integer id){
        return StepMapper.entityToDto(em.find(StepEntity.class,id));
    }

    @Timed
    public List<StepDto> getAllSteps(){
        return em.createNamedQuery("Step.getAll", StepEntity.class).getResultList().stream().map(StepMapper::entityToDto).collect(Collectors.toList());
    }

    public void deleteStep(Integer id) {
        StepEntity stepEntity = em.find(StepEntity.class, id);
        if ( stepEntity != null) {
            this.beginTx();
            em.remove(stepEntity);
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
