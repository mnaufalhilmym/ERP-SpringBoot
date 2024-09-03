package id.bengkelinovasi.erp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import id.bengkelinovasi.erp.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {

    Optional<Project> findByCompanyIdAndId(UUID companyId, UUID id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM project_employees WHERE project_id = :projectId", nativeQuery = true)
    void deleteAllProjectEmployeesByProjectId(UUID projectId);

}
