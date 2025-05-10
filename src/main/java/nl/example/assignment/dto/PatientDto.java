package nl.example.assignment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PatientDto {

    private String name;
    private String ssn;
}
