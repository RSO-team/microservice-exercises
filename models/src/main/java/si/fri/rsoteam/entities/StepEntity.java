package si.fri.rsoteam.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "step")
@NamedQuery(name = "Step.getAll", query = "SELECT e from StepEntity e")
public class StepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Integer id;

    @Size(min = 3, max = 50)
    private String name;

    @Size(min = 3, max = 200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private ExerciseEntity exercise;

    public ExerciseEntity getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseEntity exercise) {
        this.exercise = exercise;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
