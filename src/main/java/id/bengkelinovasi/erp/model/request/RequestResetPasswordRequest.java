package id.bengkelinovasi.erp.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RequestResetPasswordRequest {

    @NotBlank
    @Email
    private String email;

}
