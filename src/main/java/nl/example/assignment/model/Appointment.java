package nl.example.assignment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Represents an appointment entity in the system.
 *
 * <p><strong>Changelog and motivations:</strong>
 * <ul>
 *   <li><strong>UUID identifier:</strong> Changed the primary key type to {@code UUID} with
 *       {@code @GeneratedValue} and {@code @Column(columnDefinition = "uuid", updatable = false, nullable = false)}
 *       to ensure globally unique identifiers and database portability.</li>
 *   <li><strong>Audit timestamps:</strong> Added {@code createdDate} and {@code modifiedDate} fields
 *       with Hibernate's {@code @CreationTimestamp} and {@code @UpdateTimestamp} annotations
 *       to automatically track record creation and updates without manual handling.</li>
 *   <li><strong>Lombok integration:</strong> Introduced {@code @Getter}, {@code @Setter}, and
 *       {@code @NoArgsConstructor} annotations to reduce boilerplate code for getters, setters,
 *       and the default constructor.</li>
 *   <li><strong>Lazy-loaded relationship:</strong> Configured the {@code patient} association
 *       as {@code @ManyToOne(fetch = FetchType.LAZY)} to avoid unnecessary loading of the
 *       entire patient object when fetching appointments.</li>
 *   <li><strong>Custom constructor:</strong> Added a parameterized constructor to initialize
 *       the {@code reason}, {@code date}, and {@code patient} fields conveniently.
 *   </li>
 *   <li><strong>Entity equality:</strong> Overrode {@code equals} and {@code hashCode}
 *       based solely on the {@code id} field to ensure consistent behavior in
 *       collections and Hibernate contexts.</li>
 * </ul>
 */
@Entity
@Table(name = "appointment")
@Setter
@Getter
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    private String reason;
    private LocalDateTime date;
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @UpdateTimestamp
    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public Appointment(String reason, LocalDateTime date, Patient patient) {
        this.reason = reason;
        this.date = date;
        this.patient = patient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
