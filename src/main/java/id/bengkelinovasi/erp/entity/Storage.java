package id.bengkelinovasi.erp.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

import id.bengkelinovasi.erp.enumeration.StorageObjectType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "storage")
public class Storage {

    @Id
    @Column(name = "id")
    private UUID id = UuidCreator.getTimeOrderedEpochFast();

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false, updatable = false)
    private StorageObjectType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Storage parentFolder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mime_type", updatable = false)
    private String mimeType;

    @Column(name = "size", nullable = false)
    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void prePersist() {
        if (type.equals(StorageObjectType.FOLDER)) {
            mimeType = null;
            size = 0L;
        }
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        if (type.equals(StorageObjectType.FOLDER)) {
            mimeType = null;
        }
        updatedAt = OffsetDateTime.now();
    }

}
