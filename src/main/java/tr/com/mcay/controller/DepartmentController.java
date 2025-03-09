package tr.com.mcay.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.mcay.entity.Department;
import tr.com.mcay.service.DepartmentService;
import tr.com.mcay.dto.DepartmentDTO;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Api(value = "Department API", tags = {"Department"})
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }
    
    @GetMapping
    @ApiOperation(value = "Tüm departmanları listeler")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "ID'ye göre departman getirir")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @ApiOperation(value = "Yeni departman oluşturur")
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.saveDepartment(department));
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "Var olan departmanı günceller")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        return departmentService.getDepartmentById(id)
                .map(existingDepartment -> {
                    department.setId(id);
                    return ResponseEntity.ok(departmentService.saveDepartment(department));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Departmanı siler")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        return departmentService.getDepartmentById(id)
                .map(department -> {
                    departmentService.deleteDepartment(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Lazy loading test metotları
    
    @GetMapping("/{id}/with-employees-transactional")
    @ApiOperation(value = "Transaction içinde lazy loading ile departman ve çalışanlarını getirir")
    public ResponseEntity<Department> getDepartmentWithEmployeesTransactional(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentWithEmployeesTransactional(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employees-non-transactional")
    @ApiOperation(value = "Transaction olmadan lazy loading ile departman ve çalışanlarını getirir (LazyInitializationException)")
    public ResponseEntity<Department> getDepartmentWithEmployeesNonTransactional(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentWithEmployeesNonTransactional(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employees-fetch")
    @ApiOperation(value = "Join fetch ile departman ve çalışanlarını getirir")
    public ResponseEntity<Department> getDepartmentWithEmployeesFetch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentWithEmployeesFetch(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employees-and-tasks-fetch")
    @ApiOperation(value = "Join fetch ile departman, çalışanları ve görevlerini getirir")
    public ResponseEntity<Department> getDepartmentWithEmployeesAndTasksFetch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentWithEmployeesAndTasksFetch(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employees-and-tasks-dto")
    @ApiOperation(value = "DTO kullanarak departman, çalışanları ve görevlerini getirir (N+1 problemi olmadan)")
    public ResponseEntity<DepartmentDTO> getDepartmentWithEmployeesAndTasksDTO(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentWithEmployeesAndTasksDTO(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employees-and-tasks-one-query")
    @ApiOperation(value = "Tek sorguda departman, çalışanları ve görevlerini getirir (N+1 problemi olmadan)")
    public ResponseEntity<Department> getDepartmentWithEmployeesAndTasksInOneQuery(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentWithEmployeesAndTasksInOneQuery(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employees-and-tasks-one-query-dto")
    @ApiOperation(value = "Tek sorguda DTO olarak departman, çalışanları ve görevlerini getirir (N+1 problemi olmadan)")
    public ResponseEntity<DepartmentDTO> getDepartmentWithEmployeesAndTasksInOneQueryDTO(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentWithEmployeesAndTasksInOneQueryDTO(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 