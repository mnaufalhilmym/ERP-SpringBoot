package id.bengkelinovasi.erp.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.bengkelinovasi.erp.enumeration.ProjectStatus;
import id.bengkelinovasi.erp.model.request.AddProjectEmployeesRequest;
import id.bengkelinovasi.erp.model.request.CreateProjectRequest;
import id.bengkelinovasi.erp.model.request.GetProjectListRequest;
import id.bengkelinovasi.erp.model.request.GetProjectDetailByIdRequest;
import id.bengkelinovasi.erp.model.request.SetProjectEmployeesRequest;
import id.bengkelinovasi.erp.model.response.ProjectListResponse;
import id.bengkelinovasi.erp.model.response.ProjectDetailResponse;
import id.bengkelinovasi.erp.model.response.WebPaginationResponse;
import id.bengkelinovasi.erp.model.response.WebResponse;
import id.bengkelinovasi.erp.service.ProjectService;

@RestController
public class ProjectController {

        @Autowired
        private ProjectService projectService;

        @PostMapping(path = "/api/project", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public WebResponse<UUID> createProject(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody CreateProjectRequest request) {
                UUID actorUserId = UUID.fromString(userDetails.getUsername());
                request.setActorUserId(actorUserId);
                UUID projectId = projectService.create(request);
                return WebResponse.<UUID>builder().data(projectId).build();
        }

        @GetMapping(path = "/api/projects", produces = MediaType.APPLICATION_JSON_VALUE)
        public WebResponse<List<ProjectListResponse>> getProjects(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(name = "start_date_from", required = false) LocalDate startDateFrom,
                        @RequestParam(name = "start_date_to", required = false) LocalDate startDateTo,
                        @RequestParam(name = "client", required = false) String client,
                        @RequestParam(name = "qclient", required = false) String qClient,
                        @RequestParam(name = "client_email", required = false) String clientEmail,
                        @RequestParam(name = "qclient_email", required = false) String qClientEmail,
                        @RequestParam(name = "name", required = false) String name,
                        @RequestParam(name = "qname", required = false) String qName,
                        @RequestParam(name = "status", required = false) ProjectStatus status,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size) {
                UUID actorUserId = UUID.fromString(userDetails.getUsername());

                GetProjectListRequest request = GetProjectListRequest.builder()
                                .actorUserId(actorUserId)
                                .startDateFrom(startDateFrom)
                                .startDateTo(startDateTo)
                                .client(client)
                                .qClient(qClient)
                                .clientEmail(clientEmail)
                                .qClientEmail(qClientEmail)
                                .name(name)
                                .qName(qName)
                                .status(status)
                                .page(page - 1)
                                .size(size)
                                .build();

                Page<ProjectListResponse> ProjectListResponse = projectService.getMany(request);

                return WebResponse.<List<ProjectListResponse>>builder()
                                .data(ProjectListResponse.getContent())
                                .pagination(WebPaginationResponse.builder()
                                                .size(ProjectListResponse.getSize())
                                                .total(ProjectListResponse.getTotalElements())
                                                .page(ProjectListResponse.getNumber())
                                                .totalPages(ProjectListResponse.getTotalPages())
                                                .build())
                                .build();
        }

        @GetMapping(path = "/api/project/{id}")
        public WebResponse<ProjectDetailResponse> getProjectDetail(@AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable("id") UUID id) {
                UUID actorUserId = UUID.fromString(userDetails.getUsername());

                GetProjectDetailByIdRequest request = GetProjectDetailByIdRequest.builder()
                                .actorUserId(actorUserId)
                                .id(id)
                                .build();

                ProjectDetailResponse projectDetailResponse = projectService.getDetailById(request);

                return WebResponse.<ProjectDetailResponse>builder().data(projectDetailResponse).build();
        }

        @PostMapping(path = "/api/project/{id}/employee")
        public WebResponse<String> addProjectEmployees(
                        @PathVariable("id") UUID id,
                        @RequestBody AddProjectEmployeesRequest request) {
                {
                        request.setId(id);
                        projectService.addEmployees(request);
                        return WebResponse.<String>builder().data("OK").build();
                }
        }

        @PutMapping(path = "/api/project/{id}/employee")
        public WebResponse<String> setProjectEmployees(
                        @PathVariable("id") UUID id,
                        @RequestBody SetProjectEmployeesRequest request) {
                {
                        request.setId(id);
                        projectService.setEmployees(request);
                        return WebResponse.<String>builder().data("OK").build();
                }
        }

}
