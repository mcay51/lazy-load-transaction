package tr.com.mcay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    
    private Long id;
    private String name;
    private String position;
    private Set<TaskDTO> tasks = new HashSet<>();
    
    public EmployeeDTO(Long id, String name, String position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }
} 