package id.bengkelinovasi.erp.model.response;

import java.util.UUID;

import id.bengkelinovasi.erp.entity.Employee;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectDetailEmployeeResponse {

    private UUID id;

    private String name;

    public static ProjectDetailEmployeeResponse fromEntity(Employee employee) {
        if (employee == null) {
            return null;
        }
        return ProjectDetailEmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .build();
    }

}
