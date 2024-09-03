package id.bengkelinovasi.erp.model.response;

import java.time.OffsetDateTime;

import id.bengkelinovasi.erp.entity.UserSession;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponse {

    private String token;

    private OffsetDateTime expiredAt;

    public static SignInResponse fromEntity(UserSession userSession) {
        if (userSession == null) {
            return null;
        }
        return SignInResponse.builder()
                .token(userSession.getToken())
                .expiredAt(userSession.getExpiredAt())
                .build();
    }

}
