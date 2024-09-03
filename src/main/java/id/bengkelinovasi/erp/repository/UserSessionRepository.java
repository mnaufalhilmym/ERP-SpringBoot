package id.bengkelinovasi.erp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.bengkelinovasi.erp.entity.UserSession;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {

    Optional<UserSession> findByTokenAndUserAgent(String token, String userAgent);

    void deleteAllByUserId(UUID userId);

}
