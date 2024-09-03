package id.bengkelinovasi.erp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import id.bengkelinovasi.erp.entity.Storage;

@Repository
public interface StorageRepository extends JpaRepository<Storage, UUID>, JpaSpecificationExecutor<Storage> {

    Optional<Storage> findByProjectIdAndParentFolderId(UUID projectId, UUID parentFolderId);

    List<Storage> findAllByProjectIdAndParentFolderId(UUID projectId, UUID parentFolderId);

    List<Storage> findAllByParentFolderId(UUID parentFolderId);

}
