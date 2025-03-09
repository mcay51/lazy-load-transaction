package tr.com.mcay.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.mcay.dto.EmployeeDTO;
import tr.com.mcay.entity.Employee;
import tr.com.mcay.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Api(value = "Employee API", tags = {"Employee"})
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    @GetMapping
    @ApiOperation(value = "Tüm çalışanları listeler")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "ID'ye göre çalışan getirir")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/department/{departmentId}")
    @ApiOperation(value = "Departman ID'sine göre çalışanları listeler")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentId(@PathVariable Long departmentId) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartmentId(departmentId));
    }
    
    @PostMapping
    @ApiOperation(value = "Yeni çalışan oluşturur")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.saveEmployee(employee));
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "Var olan çalışanı günceller")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return employeeService.getEmployeeById(id)
                .map(existingEmployee -> {
                    employee.setId(id);
                    return ResponseEntity.ok(employeeService.saveEmployee(employee));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Çalışanı siler")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(employee -> {
                    employeeService.deleteEmployee(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Lazy loading test metotları
    
    @GetMapping("/{id}/with-tasks-transactional")
    @ApiOperation(value = "Transaction içinde lazy loading ile çalışan ve görevlerini getirir (N+1 problemi var)")
    public ResponseEntity<Employee> getEmployeeWithTasksTransactional(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeWithTasksTransactional(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-tasks-non-transactional")
    @ApiOperation(value = "Transaction olmadan lazy loading ile çalışan ve görevlerini getirir (LazyInitializationException)")
    public ResponseEntity<Employee> getEmployeeWithTasksNonTransactional(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeWithTasksNonTransactional(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-tasks-fetch")
    @ApiOperation(value = "Join fetch ile çalışan ve görevlerini getirir (N+1 problemi azaltılmış)")
    public ResponseEntity<Employee> getEmployeeWithTasksFetch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeWithTasksFetch(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-tasks-and-department")
    @ApiOperation(value = "Transaction içinde nested lazy loading ile çalışan, görevleri ve departmanını getirir (N+1 problemi var)")
    public ResponseEntity<Employee> getEmployeeWithTasksAndDepartment(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeWithTasksAndDepartment(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-tasks-and-department-fetch")
    @ApiOperation(value = "Join fetch ile çalışan, görevleri ve departmanını getirir (N+1 problemi azaltılmış)")
    public ResponseEntity<Employee> getEmployeeWithTasksAndDepartmentFetch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeWithTasksAndDepartmentFetch(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-tasks-batch-size")
    @ApiOperation(value = "Batch size kullanarak çalışan ve görevlerini getirir (N+1 problemi azaltılmış)")
    public ResponseEntity<Employee> getEmployeeWithTasksBatchSize(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeWithTasksBatchSize(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-tasks-dto")
    @ApiOperation(value = "DTO kullanarak çalışan ve görevlerini getirir (N+1 problemi var)")
    public ResponseEntity<EmployeeDTO> getEmployeeWithTasksDTO(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeWithTasksDTO(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-tasks-one-query")
    @ApiOperation(value = "Tek sorguda çalışan ve görevlerini getirir (N+1 problemi çözülmüş)")
    public ResponseEntity<Employee> getEmployeeWithTasksInOneQuery(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeWithTasksInOneQuery(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-tasks-one-query-dto")
    @ApiOperation(value = "Tek sorguda DTO olarak çalışan ve görevlerini getirir (N+1 problemi çözülmüş)")
    public ResponseEntity<EmployeeDTO> getEmployeeWithTasksInOneQueryDTO(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeWithTasksInOneQueryDTO(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 