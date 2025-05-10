package nl.example.assignment.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.example.assignment.AssignmentApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = AssignmentApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@ActiveProfiles("test")
class AppointmentControllerIntegrationTest {

    private static final String BULK_REQUEST = """
            {
              "ssn": "123-45-6789",
              "patientName": "John Doe",
              "appointmentDetails": [
                { "reason": "Checkup", "date": "2025-06-01T12:00:00" },
                { "reason": "Follow-up", "date": "2025-06-01T11:00:00" }
              ]
            }
            """;
    private static final String BULK_URL = "/api/appointments/bulk";
    private static final String GET_URL = "/api/appointments";
    private static final String LATEST_URL = "/api/appointments/latest";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenCreateBulkAppointments_thenReturnsCreatedDtos() throws Exception {

        mockMvc.perform(post(BULK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BULK_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].reason", is("Checkup")))
                .andExpect(jsonPath("$[0].date", is("2025-06-01T12:00:00")))
                .andExpect(jsonPath("$[1].reason", is("Follow-up")))
                .andExpect(jsonPath("$[1].date", is("2025-06-01T11:00:00")));
    }

    @Test
    void whenGetByReason_thenReturnsOnlyMatchingAppointments() throws Exception {

        mockMvc.perform(post(BULK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BULK_REQUEST))
                .andExpect(status().isOk());

        mockMvc.perform(get(GET_URL)
                        .param("reason", "Checkup"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].reason", everyItem(is("Checkup"))))
                .andExpect(jsonPath("$[*].date",
                        containsInAnyOrder("2025-06-01T12:00:00")));
    }

    @Test
    void whenDeleteBySsn_thenReturnsDeletedCountAndSsn() throws Exception {
        String bulkRequest = """
                {
                  "ssn": "222-33-4444",
                  "patientName": "Alice Smith",
                  "appointmentDetails": [
                    { "reason": "Dental", "date": "2025-07-10T09:30:00" },
                    { "reason": "Vision", "date": "2025-07-11T10:30:00" }
                  ]
                }
                """;

        mockMvc.perform(post(BULK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bulkRequest))
                .andExpect(status().isOk());

        mockMvc.perform(delete(GET_URL)
                        .param("ssn", "222-33-4444"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount", is(2)))
                .andExpect(jsonPath("$.ssn", is("222-33-4444")));
    }

    @Test
    void whenGetLatest_thenReturnsMostRecentAppointment() throws Exception {
        String bulkRequest = """
                {
                  "ssn": "555-66-7777",
                  "patientName": "Bob Brown",
                  "appointmentDetails": [
                    { "reason": "Initial",     "date": "2025-08-01T08:00:00" },
                    { "reason": "Follow-up",   "date": "2025-08-05T09:30:00" },
                    { "reason": "Final check", "date": "2025-08-03T11:15:00" }
                  ]
                }
                """;

        mockMvc.perform(post(BULK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bulkRequest))
                .andExpect(status().isOk());

        mockMvc.perform(get(LATEST_URL)
                        .param("ssn", "555-66-7777"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason", is("Follow-up")))
                .andExpect(jsonPath("$.date", is("2025-08-05T09:30:00")));
    }
}
