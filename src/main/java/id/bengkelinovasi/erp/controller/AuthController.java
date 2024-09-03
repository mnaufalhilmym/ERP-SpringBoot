package id.bengkelinovasi.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import id.bengkelinovasi.erp.model.request.CheckTokenRequestResetPasswordRequest;
import id.bengkelinovasi.erp.model.request.RequestResetPasswordRequest;
import id.bengkelinovasi.erp.model.request.SignInRequest;
import id.bengkelinovasi.erp.model.request.SignUpRequest;
import id.bengkelinovasi.erp.model.request.VerifyRequestResetPasswordRequest;
import id.bengkelinovasi.erp.model.request.VerifySignUpRequest;
import id.bengkelinovasi.erp.model.response.SignInResponse;
import id.bengkelinovasi.erp.model.response.WebResponse;
import id.bengkelinovasi.erp.service.AuthService;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/api/auth/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> signUp(@RequestBody SignUpRequest request) {
        authService.signUp(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PostMapping(path = "/api/auth/signup/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> verifySignUp(@RequestBody VerifySignUpRequest request) {
        authService.verifySignUp(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PostMapping(path = "/api/auth/signin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<SignInResponse> signIn(@RequestHeader(value = "User-Agent") String userAgent,
            @RequestBody SignInRequest request) {
        request.setUserAgent(userAgent);
        SignInResponse signInResponse = authService.signIn(request);
        return WebResponse.<SignInResponse>builder().data(signInResponse).build();
    }

    @PostMapping(path = "/api/auth/request-reset-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> requestResetPassword(@RequestBody RequestResetPasswordRequest request) {
        authService.requestResetPassword(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PostMapping(path = "/api/auth/request-reset-password/check-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> checkTokenRequestResetPassword(
            @RequestBody CheckTokenRequestResetPasswordRequest request) {
        authService.checkTokenRequestResetPassword(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PostMapping(path = "/api/auth/request-reset-password/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> verifyRequestResetPassword(@RequestBody VerifyRequestResetPasswordRequest request) {
        authService.verifyRequestResetPassword(request);
        return WebResponse.<String>builder().data("OK").build();
    }

}
