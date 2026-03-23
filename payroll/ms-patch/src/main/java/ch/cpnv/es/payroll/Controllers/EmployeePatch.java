package ch.cpnv.es.payroll.Controllers;

import ch.cpnv.es.payroll.Entities.Employee;
import ch.cpnv.es.payroll.Repositories.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeePatch {

    private final EmployeeRepository repository;

    EmployeePatch(EmployeeRepository repository) {
        this.repository = repository;
    }

    /*
     * curl sample:
     * curl -i -X PATCH localhost:8080/api/v1/employees/2 \
     *   -H "Content-type:application/json" \
     *   -d "{\"role\": \"Manager\"}"
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Employee> patchEmployee(
            @PathVariable Long id,
            @RequestBody Employee patch
    ) {
        return repository.findById(id)
                .map(existingEmployee -> {

                    // Partial update
                    if (patch.getName() != null) {
                        existingEmployee.setName(patch.getName());
                    }
                    if (patch.getRole() != null) {
                        existingEmployee.setRole(patch.getRole());
                    }

                    Employee saved = repository.save(existingEmployee);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
