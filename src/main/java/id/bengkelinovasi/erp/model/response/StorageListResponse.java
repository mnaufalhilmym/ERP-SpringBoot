package id.bengkelinovasi.erp.model.response;

import java.util.UUID;

import id.bengkelinovasi.erp.entity.Storage;
import id.bengkelinovasi.erp.enumeration.StorageObjectType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageListResponse {

    private StorageObjectType type;

    private UUID id;

    private UUID parentFolderId;

    private String name;

    public static StorageListResponse fromEntity(Storage storage, Storage parentStorage) {
        if (storage == null) {
            return null;
        }
        StorageListResponse StorageListResponse = new StorageListResponse();
        StorageListResponse.setType(storage.getType());
        StorageListResponse.setId(storage.getId());
        if (parentStorage != null) {
            StorageListResponse.setParentFolderId(parentStorage.getId());
        }
        StorageListResponse.setName(storage.getName());
        return StorageListResponse;
    }

}
