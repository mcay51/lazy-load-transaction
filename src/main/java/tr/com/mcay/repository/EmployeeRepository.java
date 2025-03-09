package tr.com.mcay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tr.com.mcay.dto.EmployeeDTO;
import tr.com.mcay.dto.TaskDTO;
import tr.com.mcay.entity.Employee;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    List<Employee> findByDepartmentId(Long departmentId);
    
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.tasks WHERE e.id = :id")
    Optional<Employee> findByIdWithTasks(Long id);
    
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithDepartment(Long id);
    
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.tasks LEFT JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithTasksAndDepartment(Long id);
    
    // DTO metotları
    @Query("SELECT new tr.com.mcay.dto.EmployeeDTO(e.id, e.name, e.position) FROM Employee e WHERE e.id = :id")
    Optional<EmployeeDTO> findEmployeeDTOById(Long id);
    
    @Query("SELECT new tr.com.mcay.dto.TaskDTO(t.id, t.title, t.description, t.dueDate, t.completed) FROM Task t WHERE t.employee.id = :employeeId")
    List<TaskDTO> findTaskDTOsByEmployeeId(Long employeeId);
    
    // Tek sorguda tüm verileri getiren metotlar
    @Query("SELECT e FROM Employee e JOIN FETCH e.tasks WHERE e.id = :id")
    Optional<Employee> findByIdWithTasksInOneQuery(Long id);
    
    @Query("SELECT new map(" +
           "e.id as employeeId, e.name as employeeName, e.position as employeePosition, " +
           "t.id as taskId, t.title as taskTitle, t.description as taskDescription, " +
           "t.dueDate as taskDueDate, t.completed as taskCompleted) " +
           "FROM Employee e JOIN e.tasks t " +
           "WHERE e.id = :id")
    List<Map<String, Object>> findAllDTODataInOneQuery(Long id);
} 