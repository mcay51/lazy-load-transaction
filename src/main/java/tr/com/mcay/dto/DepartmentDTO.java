package tr.com.mcay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    
    private Long id;
    private String name;
    private Set<EmployeeDTO> employees = new HashSet<>();
    
    public DepartmentDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
} 