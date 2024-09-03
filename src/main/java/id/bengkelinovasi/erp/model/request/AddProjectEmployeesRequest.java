package id.bengkelinovasi.erp.model.request;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProjectEmployeesRequest {

    @JsonIgnore
    private UUID id;

    @NotNull(message = "employee_ids harus minimal 1 data")
    @Size(min = 1, message = "employee_ids harus minimal 1 data")
    private List<UUID> employeeIds;

}
