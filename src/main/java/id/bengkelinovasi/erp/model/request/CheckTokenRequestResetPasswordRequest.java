package id.bengkelinovasi.erp.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckTokenRequestResetPasswordRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String verificationToken;

}
