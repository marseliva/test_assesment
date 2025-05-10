package nl.example.assignment.repository;

import nl.example.assignment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
      SELECT a 
        FROM Appointment a 
        JOIN FETCH a.patient 
       WHERE LOWER(a.reason) LIKE LOWER(CONCAT('%', :reason, '%'))
    """)
    List<Appointment> findByReasonWithPatient(@Param("reason") String reason);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Appointment a WHERE a.patient.ssn = :ssn")
    int deleteByPatientSsn(@Param("ssn") String ssn);

    Optional<Appointment> findTopByPatient_SsnOrderByDateDesc(String ssn);
}
