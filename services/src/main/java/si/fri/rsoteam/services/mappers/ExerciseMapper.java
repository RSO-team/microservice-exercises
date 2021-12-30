package si.fri.rsoteam.services.mappers;

import si.fri.rsoteam.entities.ExerciseEntity;
import si.fri.rsoteam.lib.dtos.ExerciseDto;

import java.util.stream.Collectors;

public class ExerciseMapper {
    public static ExerciseDto entityToDto(ExerciseEntity et) {
        ExerciseDto exerciseDto = new ExerciseDto();
        exerciseDto.id = et.getId();
        exerciseDto.name = et.getName();
        exerciseDto.description = et.getDescription();
        exerciseDto.steps = et.getSteps().stream().map(StepMapper::entityToDto).collect(Collectors.toList());

        return exerciseDto;
    }

    public static ExerciseEntity dtoToEntity(ExerciseDto exerciseDto) {
        ExerciseEntity exerciseEntity = new ExerciseEntity();
        exerciseEntity.setName(exerciseDto.name);
        exerciseEntity.setDescription(exerciseDto.description);
        exerciseEntity.setSteps(exerciseDto.steps.stream().map(StepMapper::dtoToEntity).collect(Collectors.toList()));
        exerciseEntity.getSteps().forEach(stepEntity -> stepEntity.setExercise(exerciseEntity));
        return  exerciseEntity;
    }
}
