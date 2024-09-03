package id.bengkelinovasi.erp.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProjectRequest {

    @JsonIgnore
    @NotNull
    private UUID actorUserId;

    @NotBlank
    private String name;

    private String client;

    @Email
    private String clientEmail;

    private String category;

    private BigDecimal omzet;

    private LocalDate startDate;

}
