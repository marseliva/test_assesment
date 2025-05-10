package nl.example.assignment.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a patient entity in the system.
 *
 * <p><strong>Changelog and motivations:</strong>
 * <ul>
 *   <li><strong>UUID identifier:</strong> Switched the primary key type from {@code Long} to {@code UUID} with
 *       {@code @GeneratedValue} and {@code @Column(columnDefinition = "uuid", updatable = false, nullable = false)}
 *       to achieve globally unique IDs and enhance portability across databases.</li>
 *   <li><strong>SSN uniqueness & indexing:</strong> Enforced uniqueness on the {@code ssn} field via
 *       {@code @Column(unique = true, updatable = false)} and added a database {@code @Index}
 *       on {@code ssn} to optimize lookups by social security number.</li>
 *   <li><strong>Audit timestamps:</strong> Introduced {@code createdDate} and {@code modifiedDate}
 *       fields with Hibernate's {@code @CreationTimestamp} and {@code @UpdateTimestamp}
 *       to automatically capture creation and modification times.</li>
 *   <li><strong>Lombok integration:</strong> Added {@code @Getter}, {@code @Setter}, and
 *       {@code @NoArgsConstructor} to reduce boilerplate code for accessors and constructors.</li>
 *   <li><strong>Field constraints:</strong> Marked {@code name} and {@code ssn} as {@code nullable = false}
 *       to ensure data integrity at the database level.</li>
 *   <li><strong>Relationship cleanup:</strong> Removed the previous bi-directional {@code List<Appointment> appointments}
 *       mapping to simplify the model and avoid potential lazy-loading pitfalls when not needed.</li>
 *   <li><strong>Entity equality:</strong> Retained {@code equals} and {@code hashCode} methods based on
 *       the immutable {@code ssn} field to maintain consistent behavior in collections.</li>
 * </ul>
 */
@Entity
@Table(
        name = "patient",
        indexes = @Index(name = "idx_patient_ssn", columnList = "ssn", unique = true)
)
@Getter
@Setter
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "ssn", nullable = false, unique = true, updatable = false)
    private String ssn;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    public Patient(String name, String ssn) {
        this.name = name;
        this.ssn = ssn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(ssn, patient.ssn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ssn);
    }
}