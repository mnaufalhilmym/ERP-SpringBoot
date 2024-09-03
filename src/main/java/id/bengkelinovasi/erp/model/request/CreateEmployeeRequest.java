package id.bengkelinovasi.erp.model.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import id.bengkelinovasi.erp.validator.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmployeeRequest {

    @JsonIgnore
    @NotNull
    private UUID actorUserId;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Phone
    private String phone;

}
