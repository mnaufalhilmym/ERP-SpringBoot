package id.bengkelinovasi.erp.model.response;

import java.util.UUID;

import id.bengkelinovasi.erp.entity.Storage;
import id.bengkelinovasi.erp.enumeration.StorageObjectType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectDetailStorageResponse {

    private StorageObjectType type;

    private UUID id;

    private String name;

    public static ProjectDetailStorageResponse fromEntity(Storage storage) {
        if (storage == null) {
            return null;
        }
        return ProjectDetailStorageResponse.builder()
                .type(storage.getType())
                .id(storage.getId())
                .name(storage.getName())
                .build();
    }

}
