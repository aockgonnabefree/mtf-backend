package ku.cs.mtf_backend.controller;

import jakarta.validation.Valid;
import ku.cs.mtf_backend.dto.request.CreateEmployeePayload;
import ku.cs.mtf_backend.dto.request.UpdateEmployeePayload;
import ku.cs.mtf_backend.dto.response.EmployeeDetailResponse;
import ku.cs.mtf_backend.dto.response.EmployeeStatisticsResponse;
import ku.cs.mtf_backend.dto.response.EmployeeSummaryDTO;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.entity.Employee;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody CreateEmployeePayload payload) {
        try {
            Employee createdEmployee = employeeService.createEmployee(payload);
            Map<String, Object> response = Map.of(
                    "message", "Employee created successfully.",
                    "employeeId", createdEmployee.getPassportNumber()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{passportNo}")
    public ResponseEntity<?> updateEmployee(@PathVariable String passportNo,
                                            @Valid @RequestBody UpdateEmployeePayload payload) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(passportNo, payload);
            Map<String, Object> response = Map.of(
                    "message", "Employee updated successfully.",
                    "employeeId", updatedEmployee.getPassportNumber()
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            // กรณีหาลูกจ้าง หรือนายจ้างใหม่ไม่เจอ
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // กรณีข้อมูลขัดแย้งอื่นๆ ที่ไม่ใช่ Not Found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<EmployeeStatisticsResponse> getStatistics(
            @RequestParam(defaultValue = "30") Integer daysThreshold) {
        EmployeeStatisticsResponse statistics = employeeService.getStatistics(daysThreshold);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping
    public ResponseEntity<?> getEmployees(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "30") Integer daysThreshold) {
        try {
            PageResponse<EmployeeSummaryDTO> response = employeeService.getEmployeesWithPagination(page, size, nameContains, status, daysThreshold);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{passportNumber}")
    public ResponseEntity<?> getEmployeeById(@PathVariable String passportNumber) {
        try {
            EmployeeDetailResponse employee = employeeService.getEmployeeById(passportNumber);
            return ResponseEntity.ok(employee);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
