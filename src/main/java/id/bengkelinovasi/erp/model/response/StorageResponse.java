package id.bengkelinovasi.erp.model.response;

import java.io.InputStream;

import id.bengkelinovasi.erp.entity.Storage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StorageResponse {

    String name;

    String mimeType;

    InputStream fileStream;

    public static StorageResponse fromEntity(Storage storage, InputStream fileStream) {
        return StorageResponse.builder()
                .name(storage.getName())
                .mimeType(storage.getMimeType())
                .fileStream(fileStream)
                .build();
    }

}
