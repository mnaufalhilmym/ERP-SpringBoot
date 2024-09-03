package id.bengkelinovasi.erp.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import id.bengkelinovasi.erp.model.request.GetUserByIdRequest;
import id.bengkelinovasi.erp.model.response.UserResponse;
import id.bengkelinovasi.erp.model.response.WebResponse;
import id.bengkelinovasi.erp.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/api/user/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> getOwnUser(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        GetUserByIdRequest request = new GetUserByIdRequest(userId);
        UserResponse userResponse = userService.getById(request);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

}
