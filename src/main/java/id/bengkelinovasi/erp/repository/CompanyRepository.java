package id.bengkelinovasi.erp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.bengkelinovasi.erp.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
