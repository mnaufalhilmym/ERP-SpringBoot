package id.bengkelinovasi.erp.model.request;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddFileRequest {

    @JsonIgnore
    @NotNull
    private UUID actorUserId;

    @JsonIgnore
    @NotNull
    private MultipartFile file;

    @JsonIgnore
    private UUID folderId;

}
