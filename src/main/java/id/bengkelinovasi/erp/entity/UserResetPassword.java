package id.bengkelinovasi.erp.entity;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@KeySpace("user_reset_password")
public class UserResetPassword {

    @Id
    private String email;

    private String verificationToken = String.valueOf(new SecureRandom().nextInt(123456, 987655));

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long ttl = 15L;

}
