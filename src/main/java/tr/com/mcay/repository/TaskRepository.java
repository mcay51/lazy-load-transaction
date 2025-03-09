package tr.com.mcay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tr.com.mcay.entity.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByEmployeeId(Long employeeId);
    
    List<Task> findByCompletedFalseAndDueDateBefore(LocalDate date);
    
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.employee WHERE t.id = :id")
    Optional<Task> findByIdWithEmployee(Long id);
    
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.employee e LEFT JOIN FETCH e.department WHERE t.id = :id")
    Optional<Task> findByIdWithEmployeeAndDepartment(Long id);
} 