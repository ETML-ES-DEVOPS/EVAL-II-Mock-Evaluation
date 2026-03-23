package ch.cpnv.es.payroll.Controllers;

import ch.cpnv.es.payroll.Entities.Employee;
import ch.cpnv.es.payroll.Repositories.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeePatchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void given_an_empty_employee_database() {
        employeeRepository.deleteAll();
    }

    @Test
    void when_patching_existing_employee_then_employee_is_updated() throws Exception {
        // GIVEN
        Employee existingEmployee =
                employeeRepository.save(new Employee("Doe", "Supervisor"));

        // Partial update payload
        String patchPayload = objectMapper.writeValueAsString(
                new Employee(null, "Manager")
        );

        // WHEN / THEN
        mockMvc.perform(
                        patch("/api/v1/employees/{id}", existingEmployee.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(patchPayload)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingEmployee.getId()))
                .andExpect(jsonPath("$.name").value("Doe"))
                .andExpect(jsonPath("$.role").value("MANAGER"));

        // THEN (database)
        Employee updatedEmployee =
                employeeRepository.findById(existingEmployee.getId()).orElseThrow();

        assertThat(updatedEmployee.getName()).isEqualTo("Doe");
        assertThat(updatedEmployee.getRole()).isEqualTo("MANAGER");
    }

    @Test
    void when_patching_non_existing_employee_then_not_found_is_returned() throws Exception {
        // GIVEN
        String patchPayload = objectMapper.writeValueAsString(
                new Employee(null, "Manager")
        );

        // WHEN / THEN
        mockMvc.perform(
                        patch("/api/v1/employees/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(patchPayload)
                )
                .andExpect(status().isNotFound());
    }
}
