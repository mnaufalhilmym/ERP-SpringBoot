package id.bengkelinovasi.erp.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class VerifyRequestResetPasswordRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String verificationToken;

    @NotBlank
    @Size(min = 8)
    private String newPassword;

}
