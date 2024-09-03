package id.bengkelinovasi.erp.model.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetProjectDetailByIdRequest {

    @JsonIgnore
    @NotNull
    private UUID actorUserId;

    @JsonIgnore
    @NotNull
    private UUID id;

}
