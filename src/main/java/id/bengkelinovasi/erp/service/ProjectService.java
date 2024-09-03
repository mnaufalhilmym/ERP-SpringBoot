package id.bengkelinovasi.erp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import id.bengkelinovasi.erp.entity.Company;
import id.bengkelinovasi.erp.entity.Employee;
import id.bengkelinovasi.erp.entity.Storage;
import id.bengkelinovasi.erp.entity.Project;
import id.bengkelinovasi.erp.entity.User;
import id.bengkelinovasi.erp.enumeration.ProjectStatus;
import id.bengkelinovasi.erp.enumeration.StorageObjectType;
import id.bengkelinovasi.erp.model.request.AddProjectEmployeesRequest;
import id.bengkelinovasi.erp.model.request.CreateProjectRequest;
import id.bengkelinovasi.erp.model.request.GetProjectListRequest;
import id.bengkelinovasi.erp.model.request.GetProjectDetailByIdRequest;
import id.bengkelinovasi.erp.model.request.SetProjectEmployeesRequest;
import id.bengkelinovasi.erp.model.response.ProjectListResponse;
import id.bengkelinovasi.erp.model.response.ProjectDetailEmployeeResponse;
import id.bengkelinovasi.erp.model.response.ProjectDetailResponse;
import id.bengkelinovasi.erp.model.response.ProjectDetailStorageResponse;
import id.bengkelinovasi.erp.repository.EmployeeRepository;
import id.bengkelinovasi.erp.repository.ProjectRepository;
import id.bengkelinovasi.erp.repository.StorageRepository;
import id.bengkelinovasi.erp.repository.UserRepository;
import id.bengkelinovasi.erp.util.ValidationUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Transactional
    public UUID create(CreateProjectRequest request) {
        validationUtil.validate(request);

        User actorUser = userRepository.findById(request.getActorUserId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

        Project project = new Project();
        project.setCompany(actorUser.getCompany());
        project.setName(request.getName());
        project.setClient(request.getClient());
        project.setClientEmail(request.getClientEmail());
        project.setCategory(request.getCategory());
        project.setOmzet(request.getOmzet());
        project.setStartDate(request.getStartDate());
        project.setStatus(ProjectStatus.ENTERED);

        projectRepository.save(project);

        Storage storage = new Storage();
        storage.setType(StorageObjectType.FOLDER);
        storage.setProject(project);
        storage.setName("Folder Project " + project.getName());
        storage.setUser(actorUser);

        storageRepository.save(storage);

        return project.getId();
    }

    @Transactional(readOnly = true)
    public Page<ProjectListResponse> getMany(GetProjectListRequest request) {
        validationUtil.validate(request);

        User actorUser = userRepository.findById(request.getActorUserId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

        Company actorCompany = actorUser.getCompany();

        Specification<Project> specification = (root, query, builder) -> {
            if (query == null) {
                throw new IllegalArgumentException("CriteriaQuery cannot be null");
            }

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.equal(root.get("company").get("id"), actorCompany.getId()));

            if (request.getStartDateFrom() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("start_date"), request.getStartDateFrom()));
            }

            if (request.getStartDateTo() != null) {
                predicates.add(builder.lessThan(root.get("start_date"), request.getStartDateTo()));
            }

            if (request.getClient() != null) {
                predicates.add(builder.equal(builder.lower(root.get("client")), request.getClient().toLowerCase()));
            }

            if (request.getQClient() != null) {
                predicates.add(builder.like(builder.lower(root.get("client")),
                        "%" + request.getQClient().toLowerCase() + "%"));
            }

            if (request.getClientEmail() != null) {
                predicates.add(
                        builder.equal(builder.lower(root.get("client_email")), request.getClientEmail().toLowerCase()));
            }

            if (request.getQClientEmail() != null) {
                predicates.add(builder.like(builder.lower(root.get("client_email")),
                        "%" + request.getQClientEmail().toLowerCase() + "%"));
            }

            if (request.getName() != null) {
                predicates.add(builder.equal(builder.lower(root.get("name")), request.getName().toLowerCase()));
            }

            if (request.getQName() != null) {
                predicates.add(
                        builder.like(builder.lower(root.get("name")), "%" + request.getQName().toLowerCase() + "%"));
            }

            if (request.getStatus() != null) {
                predicates.add(builder.equal(root.get("status"), request.getStatus()));
            }

            query.where(predicates.toArray(new Predicate[] {}));
            query.orderBy(builder.desc(root.get("id")));

            return query.getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Project> projects = projectRepository.findAll(specification, pageable);
        List<ProjectListResponse> ProjectListResponses = projects.getContent().stream()
                .map(ProjectListResponse::fromEntity).toList();

        return new PageImpl<>(ProjectListResponses, pageable, projects.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse getDetailById(GetProjectDetailByIdRequest request) {
        validationUtil.validate(request);

        User actorUser = userRepository.findById(request.getActorUserId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

        Company company = actorUser.getCompany();

        Project project = projectRepository.findByCompanyIdAndId(company.getId(), request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proyek tidak ditemukan"));

        List<ProjectDetailEmployeeResponse> projectDetailEmployeeResponses = project.getEmployees().stream()
                .map(ProjectDetailEmployeeResponse::fromEntity).toList();

        Storage rootStorage = storageRepository.findByProjectIdAndParentFolderId(project.getId(), null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Penyimpanan tidak ditemukan"));
        List<Storage> storageList = storageRepository.findAllByProjectIdAndParentFolderId(project.getId(),
                rootStorage.getId());

        List<ProjectDetailStorageResponse> projectDetailStorageResponses = storageList.stream()
                .map(ProjectDetailStorageResponse::fromEntity).toList();

        return ProjectDetailResponse.fromEntity(project, projectDetailEmployeeResponses, rootStorage,
                projectDetailStorageResponses);
    }

    @Transactional
    public void addEmployees(AddProjectEmployeesRequest request) {
        validationUtil.validate(request);

        Project project = projectRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Proyek tidak ditemukan"));

        Set<Employee> projectEmployees = project.getEmployees();

        Set<Employee> employeesToAdd = employeeRepository.findAllById(request.getEmployeeIds()).stream()
                .filter((employee) -> !projectEmployees.contains(employee)).collect(Collectors.toSet());

        project.getEmployees().addAll(employeesToAdd);

        projectRepository.save(project);
    }

    @Transactional
    public void setEmployees(SetProjectEmployeesRequest request) {
        validationUtil.validate(request);

        Project project = projectRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Proyek tidak ditemukan"));

        projectRepository.deleteAllProjectEmployeesByProjectId(project.getId());

        Set<Employee> employees = new HashSet<>(employeeRepository.findAllById(request.getEmployeeIds()));
        if (employees.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pekerja tidak ditemukan");
        }

        project.setEmployees(employees);

        projectRepository.save(project);
    }

}
