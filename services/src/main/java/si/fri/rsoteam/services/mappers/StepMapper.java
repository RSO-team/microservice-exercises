package si.fri.rsoteam.services.mappers;

import si.fri.rsoteam.entities.StepEntity;
import si.fri.rsoteam.lib.dtos.StepDto;

public class StepMapper {
    public static StepDto entityToDto(StepEntity et) {
        StepDto stepDto = new StepDto();
        stepDto.id = et.getId();
        stepDto.description = et.getDescription();
        stepDto.name= et.getName();

        return stepDto;
    }

    public static StepEntity dtoToEntity(StepDto stepDto){
        StepEntity stepEntity = new StepEntity();
        stepEntity.setDescription(stepDto.description);
        stepEntity.setName(stepDto.name);

        return stepEntity;
    }
}
