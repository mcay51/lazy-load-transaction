package tr.com.mcay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.mcay.entity.Task;
import tr.com.mcay.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public List<Task> getTasksByEmployeeId(Long employeeId) {
        return taskRepository.findByEmployeeId(employeeId);
    }
    
    public List<Task> getOverdueTasks() {
        return taskRepository.findByCompletedFalseAndDueDateBefore(LocalDate.now());
    }
    
    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }
    
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    
    @Transactional
    public Task markTaskAsCompleted(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setCompleted(true);
        return taskRepository.save(task);
    }
    
    // Lazy loading ile ilgili metotlar
    
    // Transaction içinde lazy loading
    @Transactional
    public Task getTaskWithEmployeeTransactional(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }
    
    // Transaction olmadan lazy loading (LazyInitializationException fırlatacak)
    public Task getTaskWithEmployeeNonTransactional(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        // Lazy loading'i açıkça tetikle
        task.getEmployee().getName(); // Bu satır LazyInitializationException fırlatacak
        
        return task;
    }
    
    // Join fetch ile lazy loading sorununu çözme
    public Task getTaskWithEmployeeFetch(Long id) {
        return taskRepository.findByIdWithEmployee(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }
    
    // Nested lazy loading ile ilgili metot (LazyInitializationException fırlatacak)
    @Transactional
    public Task getTaskWithEmployeeAndDepartment(Long id) {
        Task task = taskRepository.findByIdWithEmployee(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        // Burada transaction kapandıktan sonra department'a erişmeye çalışırsak LazyInitializationException alırız
        return task;
    }
    
    // Join fetch ile nested lazy loading sorununu çözme
    public Task getTaskWithEmployeeAndDepartmentFetch(Long id) {
        return taskRepository.findByIdWithEmployeeAndDepartment(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }
} 