package si.fri.rsoteam.lib.dtos;

import java.util.List;

public class ExerciseDto {
    public Integer id;
    public String name;
    public String description;
    public List<StepDto> steps;
    public VideoDto video;

    public ExerciseDto() {
    }
}
