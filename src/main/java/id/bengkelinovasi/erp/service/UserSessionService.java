package id.bengkelinovasi.erp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.bengkelinovasi.erp.entity.UserSession;
import id.bengkelinovasi.erp.repository.UserSessionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Transactional(readOnly = true)
    public Optional<UserSession> getByTokenAndUserAgent(String token, String userAgent) {
        return userSessionRepository.findByTokenAndUserAgent(token, userAgent);
    }

    @Transactional
    public void extendExpirationTime(UserSession userSession) {
        userSession.extendExpirationTime();
        userSessionRepository.save(userSession);
    }

}
