package tr.com.mcay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.mcay.dto.DepartmentDTO;
import tr.com.mcay.dto.EmployeeDTO;
import tr.com.mcay.dto.TaskDTO;
import tr.com.mcay.entity.Department;
import tr.com.mcay.repository.DepartmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    
    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }
    
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }
    
    @Transactional
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }
    
    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
    
    // Lazy loading ile ilgili metotlar
    
    // Transaction içinde lazy loading
    @Transactional
    public Department getDepartmentWithEmployeesTransactional(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }
    
    // Transaction olmadan lazy loading (LazyInitializationException fırlatacak)
    public Department getDepartmentWithEmployeesNonTransactional(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        // Lazy loading'i açıkça tetikle
        department.getEmployees().size(); // Bu satır LazyInitializationException fırlatacak
        
        return department;
    }
    
    // Join fetch ile lazy loading sorununu çözme
    public Department getDepartmentWithEmployeesFetch(Long id) {
        return departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }
    
    // Join fetch ile tüm ilişkileri (employees ve tasks) getirme
    @Transactional
    public Department getDepartmentWithEmployeesAndTasksFetch(Long id) {
        Department department = departmentRepository.findByIdWithEmployeesAndTasks(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        // Employees yüklendikten sonra, her bir employee için tasks'ları yükle
        department.getEmployees().forEach(employee -> {
            employee.getTasks().size(); // Eager loading için
        });
        
        return department;
    }
    
    // DTO kullanarak N+1 problemini çözme
    public DepartmentDTO getDepartmentWithEmployeesAndTasksDTO(Long id) {
        // Department DTO'yu getir
        DepartmentDTO departmentDTO = departmentRepository.findDepartmentDTOById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        // Employee DTO'ları getir
        List<EmployeeDTO> employeeDTOs = departmentRepository.findEmployeeDTOsByDepartmentId(id);
        
        // Her bir Employee için Task DTO'ları getir
        for (EmployeeDTO employeeDTO : employeeDTOs) {
            List<TaskDTO> taskDTOs = departmentRepository.findTaskDTOsByEmployeeId(employeeDTO.getId());
            employeeDTO.getTasks().addAll(taskDTOs);
        }
        
        // Employee DTO'ları Department DTO'ya ekle
        departmentDTO.getEmployees().addAll(employeeDTOs);
        
        return departmentDTO;
    }
    
    // Tek sorguda tüm verileri getirme
    public Department getDepartmentWithEmployeesAndTasksInOneQuery(Long id) {
        return departmentRepository.findByIdWithEmployeesAndTasksInOneQuery(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }
    
    // Tek sorguda tüm DTO'ları getirme
    public DepartmentDTO getDepartmentWithEmployeesAndTasksInOneQueryDTO(Long id) {
        List<Map<String, Object>> results = departmentRepository.findAllDTODataInOneQuery(id);
        
        if (results.isEmpty()) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        
        // Department DTO'yu oluştur
        Map<String, Object> firstRow = results.get(0);
        DepartmentDTO departmentDTO = new DepartmentDTO(
                ((Number) firstRow.get("departmentId")).longValue(),
                (String) firstRow.get("departmentName")
        );
        
        // Employee ve Task DTO'ları oluştur
        Map<Long, EmployeeDTO> employeeDTOMap = new HashMap<>();
        
        for (Map<String, Object> row : results) {
            // Employee DTO'yu al veya oluştur
            Long employeeId = ((Number) row.get("employeeId")).longValue();
            EmployeeDTO employeeDTO = employeeDTOMap.get(employeeId);
            
            if (employeeDTO == null) {
                employeeDTO = new EmployeeDTO(
                        employeeId,
                        (String) row.get("employeeName"),
                        (String) row.get("employeePosition")
                );
                employeeDTOMap.put(employeeId, employeeDTO);
                departmentDTO.getEmployees().add(employeeDTO);
            }
            
            // Task DTO'yu oluştur ve Employee'ye ekle
            TaskDTO taskDTO = new TaskDTO(
                    ((Number) row.get("taskId")).longValue(),
                    (String) row.get("taskTitle"),
                    (String) row.get("taskDescription"),
                    (LocalDate) row.get("taskDueDate"),
                    (Boolean) row.get("taskCompleted")
            );
            
            employeeDTO.getTasks().add(taskDTO);
        }
        
        return departmentDTO;
    }
} 