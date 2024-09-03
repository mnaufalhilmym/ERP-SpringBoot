package id.bengkelinovasi.erp.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import id.bengkelinovasi.erp.model.request.CreateEmployeeRequest;
import id.bengkelinovasi.erp.model.response.WebResponse;
import id.bengkelinovasi.erp.service.EmployeeService;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping(path = "/api/employee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UUID> createEmployee(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateEmployeeRequest request) {
        UUID actorUserId = UUID.fromString(userDetails.getUsername());
        request.setActorUserId(actorUserId);
        UUID employeeId = employeeService.create(request);
        return WebResponse.<UUID>builder().data(employeeId).build();
    }

}
