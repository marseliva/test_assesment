package nl.example.assignment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class AppointmentDto {

    private String id;
    private String reason;
    private LocalDateTime date;
    private PatientDto patient;
}
