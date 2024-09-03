package id.bengkelinovasi.erp.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import id.bengkelinovasi.erp.entity.Company;
import id.bengkelinovasi.erp.entity.Employee;
import id.bengkelinovasi.erp.entity.User;
import id.bengkelinovasi.erp.model.request.CreateEmployeeRequest;
import id.bengkelinovasi.erp.repository.EmployeeRepository;
import id.bengkelinovasi.erp.repository.UserRepository;
import id.bengkelinovasi.erp.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Transactional
    public UUID create(CreateEmployeeRequest request) {
        {
            validationUtil.validate(request);

            User actorUser = userRepository.findById(request.getActorUserId())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

            Company company = actorUser.getCompany();

            Employee employee = new Employee();
            employee.setName(request.getName());
            employee.setEmail(request.getEmail());
            employee.setPhone(request.getPhone());
            employee.setCompany(company);

            employeeRepository.save(employee);

            return employee.getId();
        }
    }

}
