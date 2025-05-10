package nl.example.assignment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import nl.example.assignment.dto.CreateAppointmentRequestBody;
import nl.example.assignment.service.AppointmentService;
import nl.example.assignment.dto.AppointmentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Appointment resources.
 *
 * <p>Exposes CRUD-style operations for bulk creation, lookup, deletion,
 * and retrieval of the most recent appointment for a given patient.</p>
 *
 * <h2>Endpoints</h2>
 * <ul>
 *   <li><strong>POST /api/appointments/bulk</strong><br>
 *       Creates multiple appointments in one call.<br>
 *       Request body: {@link CreateAppointmentRequestBody}<br>
 *       Response: List of created {@link AppointmentDto}</li>
 *
 *   <li><strong>GET /api/appointments?reason={reason}</strong><br>
 *       Finds all appointments matching the given reason.<br>
 *       Query param: <em>reason</em> (must not be blank; trimmed automatically)<br>
 *       Response: List of {@link AppointmentDto}</li>
 *
 *   <li><strong>DELETE /api/appointments?ssn={ssn}</strong><br>
 *       Deletes all appointments for the patient identified by SSN.<br>
 *       Query param: <em>ssn</em> (must not be blank; trimmed automatically)<br>
 *       Response: JSON map containing:<br>
 *       &nbsp;&nbsp;•<code>deletedCount</code>– number of records removed<br>
 *       &nbsp;&nbsp;•<code>ssn</code>– normalized SSN string</li>
 *
 *   <li><strong>GET /api/appointments/latest?ssn={ssn}</strong><br>
 *       Retrieves the most recent appointment for the given patient SSN.<br>
 *       Query param: <em>ssn</em> (must not be blank; trimmed automatically)<br>
 *       Response: Single {@link AppointmentDto}</li>
 * </ul>
 *
 * <h2>Security & Validation</h2>
 * <ul>
 *   <li>All methods are secured to users with the ROLE_DOCTOR authority
 *       via <code>@PreAuthorize("hasRole('DOCTOR')")</code>.</li>
 *   <li>Controller-level <code>@Validated</code> enforces<br>
 *       - <code>@NotBlank</code> on request parameters<br>
 *       - <code>@Valid</code> on request bodies</li>
 * </ul>
 *
 * <h2>Change Log</h2>
 * <ul>
 *       – Replace entity usage with DTO<br>
 *       – Ensured all <code>@RequestParam</code> inputs are <code>trim()</code>‑ed and validated.<br>
 *       – Standardized response formats for bulk create, delete, and retrieval endpoints.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/bulk")
    public ResponseEntity<List<AppointmentDto>> createBulk(
            @RequestBody @Valid CreateAppointmentRequestBody request) {
        List<AppointmentDto> created = appointmentService.createBulkAppointments(request);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getByReason(
            @RequestParam @NotBlank(message = "Reason must not be blank") String reason) {
        List<AppointmentDto> found = appointmentService.findAppointmentsByReason(reason.trim());
        return ResponseEntity.ok(found);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteBySsn(
            @RequestParam @NotBlank(message = "SSN must not be blank") String ssn) {
        long deletedCount = appointmentService.deleteAppointmentsForPatient(ssn.trim());
        Map<String, Object> response = new HashMap<>();
        response.put("deletedCount", deletedCount);
        response.put("ssn", ssn.trim());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/latest")
    public ResponseEntity<AppointmentDto> getLatest(
            @RequestParam @NotBlank(message = "SSN must not be blank") String ssn) {
        AppointmentDto latest = appointmentService.getLatestAppointmentForPatient(ssn.trim());
        return ResponseEntity.ok(latest);
    }
}
