package id.bengkelinovasi.erp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import id.bengkelinovasi.erp.model.response.WebResponse;

@RestController
public class RootController {

    @GetMapping(path = "/")
    public WebResponse<String> root() {
        return WebResponse.<String>builder().data("OK").build();
    }
}
