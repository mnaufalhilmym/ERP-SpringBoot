package id.bengkelinovasi.erp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import id.bengkelinovasi.erp.entity.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query(value = "SELECT s.* FROM subscriptions s JOIN companies c ON c.id = s.company_id JOIN users u ON u.company_id = c.id WHERE u.id = :id AND NOW() >= s.active_from AND NOW() < s.active_until ORDER BY s.active_until DESC LIMIT 1", nativeQuery = true)
    Optional<Subscription> findActiveByUserID(@Param("id") UUID id);

}
