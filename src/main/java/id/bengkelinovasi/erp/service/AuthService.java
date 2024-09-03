package id.bengkelinovasi.erp.service;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import id.bengkelinovasi.erp.entity.Company;
import id.bengkelinovasi.erp.entity.Subscription;
import id.bengkelinovasi.erp.entity.User;
import id.bengkelinovasi.erp.entity.UserRegistration;
import id.bengkelinovasi.erp.entity.UserResetPassword;
import id.bengkelinovasi.erp.entity.UserSession;
import id.bengkelinovasi.erp.enumeration.SubscriptionType;
import id.bengkelinovasi.erp.enumeration.UserRole;
import id.bengkelinovasi.erp.model.request.CheckTokenRequestResetPasswordRequest;
import id.bengkelinovasi.erp.model.request.RequestResetPasswordRequest;
import id.bengkelinovasi.erp.model.request.SignInRequest;
import id.bengkelinovasi.erp.model.request.SignUpRequest;
import id.bengkelinovasi.erp.model.request.VerifyRequestResetPasswordRequest;
import id.bengkelinovasi.erp.model.request.VerifySignUpRequest;
import id.bengkelinovasi.erp.model.response.SignInResponse;
import id.bengkelinovasi.erp.repository.CompanyRepository;
import id.bengkelinovasi.erp.repository.SubscriptionRepository;
import id.bengkelinovasi.erp.repository.UserRegistrationRepository;
import id.bengkelinovasi.erp.repository.UserRepository;
import id.bengkelinovasi.erp.repository.UserResetPasswordRepository;
import id.bengkelinovasi.erp.repository.UserSessionRepository;
import id.bengkelinovasi.erp.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserResetPasswordRepository userResetPasswordRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private Argon2PasswordEncoder argon2PasswordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Transactional
    public void signUp(SignUpRequest request) {
        validationUtil.validate(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah terdaftar");
        }

        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setEmail(request.getEmail());
        userRegistration.setName(request.getName());
        userRegistration.setPassword(argon2PasswordEncoder.encode(request.getPassword()));

        userRegistrationRepository.save(userRegistration);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreply@erp.bengkelinovasi.id");
        mailMessage.setTo(userRegistration.getEmail());
        mailMessage.setSubject("Aktivasi Akun - Entity Resource Planning");
        mailMessage.setText(String.format(
                "Halo, %s.\nUntuk menyelesaikan pendaftaran Anda, silakan gunakan kode di bawah ini:\n\n%s\n\nKode ini hanya berlaku selama 15 menit. Terima kasih.",
                userRegistration.getName(), userRegistration.getVerificationToken()));
        javaMailSender.send(mailMessage);
    }

    @Transactional
    public void verifySignUp(VerifySignUpRequest request) {
        validationUtil.validate(request);

        UserRegistration userRegistration = userRegistrationRepository.findById(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registrasi tidak ditemukan"));
        if (!userRegistration.getVerificationToken().equals(request.getVerificationToken())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token verifikasi salah");
        }

        userRegistrationRepository.delete(userRegistration);

        Company company = new Company();
        company.setName("Perusahaan " + userRegistration.getName());

        companyRepository.save(company);

        Subscription subscription = new Subscription();
        subscription.setActiveFrom(OffsetDateTime.now());
        subscription.setActiveUntil(OffsetDateTime.now().plusDays(14));
        subscription.setType(SubscriptionType.TRIAL);
        subscription.setCompany(company);

        subscriptionRepository.save(subscription);

        User user = new User();
        user.setName(userRegistration.getName());
        user.setEmail(userRegistration.getEmail());
        user.setPassword(userRegistration.getPassword());
        user.setRole(UserRole.SUPERADMIN);
        user.setCompany(company);

        userRepository.save(user);

        companyRepository.flush();
        subscriptionRepository.flush();
        userRepository.flush();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreply@erp.bengkelinovasi.id");
        mailMessage.setTo(userRegistration.getEmail());
        mailMessage.setSubject("Aktivasi Akun Berhasil - Entity Resource Planning");
        mailMessage.setText(
                "Selamat! Akun Entity Resource Planning Anda telah berhasil diaktifkan. Kini Anda dapat masuk menggunakan akun Anda melalui situs web.");
        javaMailSender.send(mailMessage);
    }

    @Transactional
    public SignInResponse signIn(SignInRequest request) {
        validationUtil.validate(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email atau kata sandi salah"));

        if (!argon2PasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email atau kata sandi salah");
        }

        UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.setUserAgent(request.getUserAgent());

        userSessionRepository.save(userSession);

        return SignInResponse.fromEntity(userSession);
    }

    @Transactional
    public void requestResetPassword(RequestResetPasswordRequest request) {
        validationUtil.validate(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email tidak terdaftar"));

        UserResetPassword userResetPassword = new UserResetPassword();
        userResetPassword.setEmail(user.getEmail());

        userResetPasswordRepository.save(userResetPassword);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreply@erp.bengkelinovasi.id");
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Verifikasi Kata Sandi Reset Akun - Entity Resource Planning");
        mailMessage.setText(String.format(
                "Halo, %s.\nUntuk memverifikasi permintaan pengaturan ulang kata sandi Anda, silakan gunakan kode berikut:\n\n%s\n\nKode ini hanya berlaku selama 15 menit. Terima kasih.",
                user.getName(), userResetPassword.getVerificationToken()));
        javaMailSender.send(mailMessage);
    }

    @Transactional(readOnly = true)
    public void checkTokenRequestResetPassword(CheckTokenRequestResetPasswordRequest request) {
        validationUtil.validate(request);

        UserResetPassword userResetPassword = userResetPasswordRepository.findById(request.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Permintaan pengaturan ulang kata sandi tidak ditemukan"));
        if (!userResetPassword.getVerificationToken().equals(request.getVerificationToken())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token verifikasi salah");
        }
    }

    @Transactional
    public void verifyRequestResetPassword(VerifyRequestResetPasswordRequest request) {
        validationUtil.validate(request);

        UserResetPassword userResetPassword = userResetPasswordRepository.findById(request.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Permintaan pengaturan ulang kata sandi tidak ditemukan"));
        if (!userResetPassword.getVerificationToken().equals(request.getVerificationToken())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token verifikasi salah");
        }

        userResetPasswordRepository.delete(userResetPassword);

        User user = userRepository.findByEmail(userResetPassword.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));
        user.setPassword(argon2PasswordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        userSessionRepository.deleteAllByUserId(user.getId());

        userRepository.flush();
        userSessionRepository.flush();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreply@erp.bengkelinovasi.id");
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Reset Kata Sandi Akun Berhasil - Entity Resource Planning");
        mailMessage.setText(String.format(
                "Halo, %s.\nAnda baru saja menyetel ulang kata sandi pada akun Enterprise Resource Planning Anda. Semua perangkat yang Anda gunakan telah otomatis keluar dari akun Anda. Kini Anda dapat masuk ke akun Anda melalui situs web menggunakan kata sandi baru Anda.\nTerima kasih.",
                user.getName()));
        javaMailSender.send(mailMessage);
    }

}
