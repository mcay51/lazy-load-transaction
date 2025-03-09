package tr.com.mcay.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.mcay.entity.Task;
import tr.com.mcay.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Api(value = "Task API", tags = {"Task"})
public class TaskController {
    
    private final TaskService taskService;
    
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    @GetMapping
    @ApiOperation(value = "Tüm görevleri listeler")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "ID'ye göre görev getirir")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/employee/{employeeId}")
    @ApiOperation(value = "Çalışan ID'sine göre görevleri listeler")
    public ResponseEntity<List<Task>> getTasksByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(taskService.getTasksByEmployeeId(employeeId));
    }
    
    @GetMapping("/overdue")
    @ApiOperation(value = "Gecikmiş görevleri listeler")
    public ResponseEntity<List<Task>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }
    
    @PostMapping
    @ApiOperation(value = "Yeni görev oluşturur")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.saveTask(task));
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "Var olan görevi günceller")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.getTaskById(id)
                .map(existingTask -> {
                    task.setId(id);
                    return ResponseEntity.ok(taskService.saveTask(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/complete")
    @ApiOperation(value = "Görevi tamamlandı olarak işaretler")
    public ResponseEntity<Task> markTaskAsCompleted(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.markTaskAsCompleted(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Görevi siler")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> {
                    taskService.deleteTask(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Lazy loading test metotları
    
    @GetMapping("/{id}/with-employee-transactional")
    @ApiOperation(value = "Transaction içinde lazy loading ile görev ve çalışanını getirir")
    public ResponseEntity<Task> getTaskWithEmployeeTransactional(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskWithEmployeeTransactional(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employee-non-transactional")
    @ApiOperation(value = "Transaction olmadan lazy loading ile görev ve çalışanını getirir (LazyInitializationException)")
    public ResponseEntity<Task> getTaskWithEmployeeNonTransactional(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskWithEmployeeNonTransactional(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employee-fetch")
    @ApiOperation(value = "Join fetch ile görev ve çalışanını getirir")
    public ResponseEntity<Task> getTaskWithEmployeeFetch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskWithEmployeeFetch(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employee-and-department")
    @ApiOperation(value = "Transaction içinde nested lazy loading ile görev, çalışanı ve departmanını getirir (LazyInitializationException)")
    public ResponseEntity<Task> getTaskWithEmployeeAndDepartment(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskWithEmployeeAndDepartment(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/with-employee-and-department-fetch")
    @ApiOperation(value = "Join fetch ile görev, çalışanı ve departmanını getirir")
    public ResponseEntity<Task> getTaskWithEmployeeAndDepartmentFetch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskWithEmployeeAndDepartmentFetch(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 