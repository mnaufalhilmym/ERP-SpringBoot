package id.bengkelinovasi.erp.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import id.bengkelinovasi.erp.entity.UserResetPassword;

@Repository
public interface UserResetPasswordRepository extends KeyValueRepository<UserResetPassword, String> {
}
