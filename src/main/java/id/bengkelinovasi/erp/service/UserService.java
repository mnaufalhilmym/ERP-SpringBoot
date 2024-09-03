package id.bengkelinovasi.erp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import id.bengkelinovasi.erp.entity.Company;
import id.bengkelinovasi.erp.entity.User;
import id.bengkelinovasi.erp.model.request.GetUserByIdRequest;
import id.bengkelinovasi.erp.model.response.UserResponse;
import id.bengkelinovasi.erp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getById(GetUserByIdRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

        Company company = user.getCompany();

        return UserResponse.fromEntity(user, company);
    }

}
