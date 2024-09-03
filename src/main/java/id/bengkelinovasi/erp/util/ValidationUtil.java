package id.bengkelinovasi.erp.util;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Component
public class ValidationUtil {

    @Autowired
    private Validator validator;

    public void validate(Object request) {
        Set<ConstraintViolation<Object>> constraintViolatons = validator.validate(request);
        if (constraintViolatons.size() != 0) {
            throw new ConstraintViolationException(constraintViolatons);
        }
    }

}
