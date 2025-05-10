package nl.example.assignment.mapper;

import lombok.experimental.UtilityClass;
import nl.example.assignment.model.Patient;
import nl.example.assignment.dto.PatientDto;

@UtilityClass
public class PatientMapper {

    public static PatientDto toDto(final Patient patient) {
        return PatientDto.builder()
                .name(patient.getName())
                .ssn(patient.getSsn())
                .build();
    }
}
