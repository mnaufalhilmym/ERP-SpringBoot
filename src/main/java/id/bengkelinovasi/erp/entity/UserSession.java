package id.bengkelinovasi.erp.entity;

import java.time.OffsetDateTime;

import com.github.f4b6a3.uuid.UuidCreator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @Column(name = "token")
    private String token = UuidCreator.getRandomBasedFast().toString().replace("-", "");

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "user_agent", nullable = false, updatable = false)
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private OffsetDateTime expiredAt;

    @PrePersist
    protected void prePersist() {
        createdAt = OffsetDateTime.now();
        expiredAt = createdAt.plusDays(3);
    }

    public void extendExpirationTime() {
        expiredAt = OffsetDateTime.now().plusDays(3);
    }
}
