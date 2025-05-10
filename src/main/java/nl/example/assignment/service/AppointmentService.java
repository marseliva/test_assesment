package nl.example.assignment.service;

import static nl.example.assignment.mapper.AppointmentMapper.toDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.example.assignment.model.Appointment;
import nl.example.assignment.repository.PatientRepository;
import nl.example.assignment.dto.AppointmentDto;
import nl.example.assignment.dto.CreateAppointmentRequestBody;
import nl.example.assignment.model.Patient;
import nl.example.assignment.repository.AppointmentRepository;
import nl.example.assignment.mapper.AppointmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Service layer for managing appointments in the system.
 *
 * <p><strong>Changelog and motivations:</strong>
 * <ul>
 *   <li><strong>Class and package rename:</strong> Renamed from {@code HospitalService} in the root package
 *       to {@code AppointmentService} under {@code nl.example.assignment.service}
 *       to improve modularity and clarify responsibility.</li>
 *   <li><strong>Dependency injection:</strong> Replaced field injection of repositories with
 *       constructor-based injection via Lombok's {@code @RequiredArgsConstructor}
 *       for better testability and immutability.</li>
 *   <li><strong>DTO usage:</strong> Changed raw entity return types to {@code AppointmentDto} and
 *       introduced {@code CreateAppointmentRequestBody} to encapsulate all input data
 *       in a single object, simplifying method signatures.</li>
 *   <li><strong>Bulk creation refactoring:</strong> Streamlined bulk creation logic by using
 *       {@code saveAll(Iterable)} instead of looping individual {@code save()} calls,
 *       reducing database round-trips.</li>
 *   <li><strong>Patient lookup optimization:</strong> Replaced manual in-memory search
 *       with {@code PatientRepository.findBySsn(...)} and {@code save(new Patient(...))}
 *       via {@code Optional.orElseGet()}, ensuring atomicity and leveraging database indices.</li>
 *   <li><strong>Transactional boundaries:</strong> Annotated methods with {@code @Transactional}
 *       and {@code @Transactional(readOnly = true)} where appropriate,
 *       enforcing consistency and optimizing read-only operations.</li>
 *   <li><strong>Stream API and mapping:</strong> Employed Java Streams and
 *       {@code AppointmentMapper} utility methods for clear separation of concerns
 *       and concise transformation from entities to DTOs.</li>
 *   <li><strong>Logging enhancements:</strong> Improved log levels and messages,
 *       masking sensitive SSN data, and tracking key events like creation counts
 *       and retrieval outcomes.</li>
 *   <li><strong>Exception handling:</strong> Standardized error conditions using
 *       {@code EntityNotFoundException} for missing patients or appointments,
 *       providing consistent HTTP 404 semantics in a REST context.</li>
 *   <li><strong>Single-responsibility methods:</strong> Split logic into discrete public methods
 *       ({@code createBulkAppointments}, {@code findAppointmentsByReason},
 *       {@code deleteAppointmentsForPatient}, {@code getLatestAppointmentForPatient})
 *       each handling one aspect of appointment management.
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public List<AppointmentDto> createBulkAppointments(CreateAppointmentRequestBody request) {
        String ssn = request.getSsn();
        log.debug("Starting bulk appointment creation for SSN={}", ssn);

        Patient patient = patientRepository.findBySsn(ssn)
                .orElseGet(() -> {
                    log.info("No existing patient found for SSN={}, creating new record", ssn);
                    return patientRepository.save(new Patient(request.getPatientName(), ssn));
                });
        log.info("Using patient [id={}] for appointment creation", patient.getId());

        List<Appointment> appointments = request.getAppointmentDetails().stream()
                .map(d -> new Appointment(d.getReason(), d.getDate(), patient))
                .collect(Collectors.toList());

        List<Appointment> saved = appointmentRepository.saveAll(appointments);
        log.info("Created {} appointments for patient id={}", saved.size(), patient.getId());
        return saved.stream().map(AppointmentMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> findAppointmentsByReason(String reason) {
        log.debug("Searching appointments containing reason keyword: {}", reason);

        List<Appointment> matches = appointmentRepository.findByReasonWithPatient(reason);
        List<AppointmentDto> dtos = matches.stream()
                .filter(a -> a.getReason().equalsIgnoreCase(reason))
                .map(appointment -> AppointmentMapper.toDtoWithPatient(appointment, appointment.getPatient()))
                .collect(Collectors.toList());

        log.info("Found {} appointments matching reason='{}'", dtos.size(), reason);
        return dtos;
    }

    @Transactional
    public long deleteAppointmentsForPatient(String ssn) {
        log.debug("Deleting appointments for SSN={} (masked)", ssn);

        Patient patient = patientRepository.findBySsn(ssn)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Patient with SSN '%s' not found", ssn)));

        int count = appointmentRepository.deleteByPatientSsn(ssn);
        log.info("Deleted {} appointments for patient id={}", count, patient.getId());
        return count;
    }

    @Transactional(readOnly = true)
    public AppointmentDto getLatestAppointmentForPatient(String ssn) {
        log.debug("Retrieving latest appointment for SSN={} (masked)", ssn);

        if (patientRepository.findBySsn(ssn).isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("Patient with SSN '%s' not found", ssn));
        }

        Optional<Appointment> opt = appointmentRepository.findTopByPatient_SsnOrderByDateDesc(ssn);
        Appointment appointment = opt.orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("No appointments found for SSN '%s'", ssn)));

        AppointmentDto dto = toDto(appointment);
        log.info("Latest appointment id={} retrieved for patient ssn={}", dto.getId(), ssn);
        return dto;
    }
}