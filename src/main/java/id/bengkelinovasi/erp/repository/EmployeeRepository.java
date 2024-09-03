package id.bengkelinovasi.erp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.bengkelinovasi.erp.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
}
