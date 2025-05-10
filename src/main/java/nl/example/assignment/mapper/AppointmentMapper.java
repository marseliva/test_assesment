package nl.example.assignment.mapper;

import lombok.experimental.UtilityClass;
import nl.example.assignment.model.Appointment;
import nl.example.assignment.model.Patient;
import nl.example.assignment.dto.AppointmentDto;
import nl.example.assignment.dto.PatientDto;

@UtilityClass
public class AppointmentMapper {

    public static AppointmentDto toDto(Appointment appointment) {
        return buildDto(appointment, null);
    }

    public static AppointmentDto toDtoWithPatient(Appointment appointment, Patient patient) {
        PatientDto patientDto = PatientMapper.toDto(patient);
        return buildDto(appointment, patientDto);
    }

    private static AppointmentDto buildDto(Appointment appointment, PatientDto patientDto) {
        return AppointmentDto.builder()
                .id(appointment.getId().toString())
                .reason(appointment.getReason())
                .date(appointment.getDate())
                .patient(patientDto)
                .build();
    }
}

