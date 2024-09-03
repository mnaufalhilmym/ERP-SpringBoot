package id.bengkelinovasi.erp.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

import id.bengkelinovasi.erp.enumeration.SubscriptionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @Column(name = "id")
    private UUID id = UuidCreator.getTimeOrderedEpochFast();

    @Column(name = "active_from", nullable = false, updatable = false)
    private OffsetDateTime activeFrom;

    @Column(name = "active_until", nullable = false, updatable = false)
    private OffsetDateTime activeUntil;

    @Column(name = "type", nullable = false, updatable = false)
    private SubscriptionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, updatable = false)
    private Company company;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = true, updatable = false)
    private Payment payment;

}
