package nl.example.assignment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class CreateAppointmentRequestBody {

    @NotBlank(message = "Patient name must not be blank")
    private String patientName;

    @NotBlank(message = "SSN must not be blank")
    private String ssn;

    @NotEmpty(message = "Appointment details list must not be empty")
    @Valid
    private List<AppointmentDetails> appointmentDetails;
}
