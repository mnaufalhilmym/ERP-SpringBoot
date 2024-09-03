package id.bengkelinovasi.erp.model.response;

import java.util.UUID;

import id.bengkelinovasi.erp.entity.Company;
import id.bengkelinovasi.erp.entity.User;
import id.bengkelinovasi.erp.enumeration.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private UUID id;

    private String name;

    private String email;

    private String photoUrl;

    private UserRole role;

    private UUID companyId;

    private String companyName;

    public static UserResponse fromEntity(User user, Company company) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .photoUrl(user.getPhotoUrl())
                .role(user.getRole())
                .companyId(company.getId())
                .companyName(company.getName())
                .build();
    }

}
