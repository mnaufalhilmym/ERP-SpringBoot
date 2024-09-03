package id.bengkelinovasi.erp.model.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStorageListRequest {

    @JsonIgnore
    @NotNull
    private UUID actorUserId;

    @JsonIgnore
    private UUID parentFolderId;

    @JsonIgnore
    private UUID projectId;

    @JsonIgnore
    private int page;

    @JsonIgnore
    private int size;

}
