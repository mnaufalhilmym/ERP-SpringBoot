package id.bengkelinovasi.erp.configuration;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import id.bengkelinovasi.erp.repository.UserRepository;

@Configuration
public class ApplicationConfiguration {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Argon2Configuration argon2Configuration;

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findById(UUID.fromString(username))
                .orElseThrow(() -> new UsernameNotFoundException("Pengguna tidak ditemukan"));
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(argon2Configuration.passwordEncoder());
        return authProvider;
    }

}
