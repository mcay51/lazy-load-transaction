package tr.com.mcay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.mcay.dto.EmployeeDTO;
import tr.com.mcay.dto.TaskDTO;
import tr.com.mcay.entity.Employee;
import tr.com.mcay.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
    
    public List<Employee> getEmployeesByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }
    
    @Transactional
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
    
    // Lazy loading ile ilgili metotlar
    
    // Transaction içinde lazy loading (N+1 problemi var)
    @Transactional
    public Employee getEmployeeWithTasksTransactional(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        // Lazy loading'i tetikle
        employee.getTasks().size();
        
        return employee;
    }
    
    // Transaction olmadan lazy loading (LazyInitializationException fırlatacak)
    public Employee getEmployeeWithTasksNonTransactional(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        // Lazy loading'i açıkça tetikle
        employee.getTasks().size(); // Bu satır LazyInitializationException fırlatacak
        
        return employee;
    }
    
    // Join fetch ile lazy loading sorununu çözme (N+1 problemi azaltılmış)
    public Employee getEmployeeWithTasksFetch(Long id) {
        return employeeRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
    
    // Batch size ile lazy loading sorununu çözme (N+1 problemi azaltılmış)
    @Transactional
    public Employee getEmployeeWithTasksBatchSize(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        // Batch size sayesinde, tüm task'lar tek bir sorguda getirilecek
        employee.getTasks().size();
        
        return employee;
    }
    
    // Nested lazy loading ile ilgili metot (N+1 problemi var)
    @Transactional
    public Employee getEmployeeWithTasksAndDepartment(Long id) {
        Employee employee = employeeRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        // Department'ı lazy loading ile getir
        employee.getDepartment().getName();
        
        return employee;
    }
    
    // Join fetch ile nested lazy loading sorununu çözme (N+1 problemi azaltılmış)
    public Employee getEmployeeWithTasksAndDepartmentFetch(Long id) {
        return employeeRepository.findByIdWithTasksAndDepartment(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
    
    // DTO kullanarak N+1 problemini gösterme (N+1 problemi var)
    public EmployeeDTO getEmployeeWithTasksDTO(Long id) {
        // Employee DTO'yu getir
        EmployeeDTO employeeDTO = employeeRepository.findEmployeeDTOById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        // Task DTO'ları getir (N+1 problemi burada oluşur)
        List<TaskDTO> taskDTOs = employeeRepository.findTaskDTOsByEmployeeId(id);
        employeeDTO.getTasks().addAll(taskDTOs);
        
        return employeeDTO;
    }
    
    // Tek sorguda tüm verileri getirme (N+1 problemi çözülmüş)
    public Employee getEmployeeWithTasksInOneQuery(Long id) {
        return employeeRepository.findByIdWithTasksInOneQuery(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
    
    // Tek sorguda tüm DTO'ları getirme (N+1 problemi çözülmüş)
    public EmployeeDTO getEmployeeWithTasksInOneQueryDTO(Long id) {
        List<Map<String, Object>> results = employeeRepository.findAllDTODataInOneQuery(id);
        
        if (results.isEmpty()) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        
        // Employee DTO'yu oluştur
        Map<String, Object> firstRow = results.get(0);
        EmployeeDTO employeeDTO = new EmployeeDTO(
                ((Number) firstRow.get("employeeId")).longValue(),
                (String) firstRow.get("employeeName"),
                (String) firstRow.get("employeePosition")
        );
        
        // Task DTO'ları oluştur
        for (Map<String, Object> row : results) {
            TaskDTO taskDTO = new TaskDTO(
                    ((Number) row.get("taskId")).longValue(),
                    (String) row.get("taskTitle"),
                    (String) row.get("taskDescription"),
                    (LocalDate) row.get("taskDueDate"),
                    (Boolean) row.get("taskCompleted")
            );
            
            employeeDTO.getTasks().add(taskDTO);
        }
        
        return employeeDTO;
    }
} 