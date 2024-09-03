package id.bengkelinovasi.erp.model.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFolderRequest {

    @JsonIgnore
    @NotNull
    private UUID actorUserId;

    private UUID parentId;

    private UUID projectId;

    @NotBlank
    private String name;

}
