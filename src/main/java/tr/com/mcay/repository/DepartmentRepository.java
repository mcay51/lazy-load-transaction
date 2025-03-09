package tr.com.mcay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tr.com.mcay.dto.DepartmentDTO;
import tr.com.mcay.dto.EmployeeDTO;
import tr.com.mcay.dto.TaskDTO;
import tr.com.mcay.entity.Department;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    Optional<Department> findByName(String name);
    
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
    Optional<Department> findByIdWithEmployees(Long id);
    
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees")
    List<Department> findAllWithEmployees();
    
    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
    Optional<Department> findByIdWithEmployeesAndTasks(Long id);
    
    @Query("SELECT new tr.com.mcay.dto.DepartmentDTO(d.id, d.name) FROM Department d WHERE d.id = :id")
    Optional<DepartmentDTO> findDepartmentDTOById(Long id);
    
    @Query("SELECT new tr.com.mcay.dto.EmployeeDTO(e.id, e.name, e.position) FROM Employee e WHERE e.department.id = :departmentId")
    List<EmployeeDTO> findEmployeeDTOsByDepartmentId(Long departmentId);
    
    @Query("SELECT new tr.com.mcay.dto.TaskDTO(t.id, t.title, t.description, t.dueDate, t.completed) FROM Task t WHERE t.employee.id = :employeeId")
    List<TaskDTO> findTaskDTOsByEmployeeId(Long employeeId);
    
    @Query("SELECT d FROM Department d JOIN FETCH d.employees e JOIN FETCH e.tasks WHERE d.id = :id")
    Optional<Department> findByIdWithEmployeesAndTasksInOneQuery(Long id);
    
    @Query("SELECT new map(" +
           "d.id as departmentId, d.name as departmentName, " +
           "e.id as employeeId, e.name as employeeName, e.position as employeePosition, " +
           "t.id as taskId, t.title as taskTitle, t.description as taskDescription, " +
           "t.dueDate as taskDueDate, t.completed as taskCompleted) " +
           "FROM Department d JOIN d.employees e JOIN e.tasks t " +
           "WHERE d.id = :id")
    List<Map<String, Object>> findAllDTODataInOneQuery(Long id);
} 