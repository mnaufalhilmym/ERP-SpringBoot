package id.bengkelinovasi.erp.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import id.bengkelinovasi.erp.entity.UserRegistration;

@Repository
public interface UserRegistrationRepository extends KeyValueRepository<UserRegistration, String> {
}
