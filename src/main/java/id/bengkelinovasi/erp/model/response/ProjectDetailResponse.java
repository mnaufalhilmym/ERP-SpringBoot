package id.bengkelinovasi.erp.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import id.bengkelinovasi.erp.entity.Storage;
import id.bengkelinovasi.erp.entity.Project;
import id.bengkelinovasi.erp.enumeration.ProjectStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectDetailResponse {

    private UUID id;

    private String name;

    private String category;

    private LocalDate startDate;

    private ProjectStatus status;

    private String client;

    private String clientEmail;

    private BigDecimal omzet;

    private BigDecimal netProfit;

    private List<ProjectDetailEmployeeResponse> employees;

    private UUID rootFolder;

    private List<ProjectDetailStorageResponse> storage;

    public static ProjectDetailResponse fromEntity(Project project,
            List<ProjectDetailEmployeeResponse> employees, Storage rootFolder,
            List<ProjectDetailStorageResponse> storage) {
        if (project == null || rootFolder == null) {
            return null;
        }
        return ProjectDetailResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .category(project.getCategory())
                .startDate(project.getStartDate())
                .status(project.getStatus())
                .client(project.getClient())
                .clientEmail(project.getClientEmail())
                .omzet(project.getOmzet())
                .netProfit(BigDecimal.valueOf(0))
                .employees(employees)
                .rootFolder(rootFolder.getId())
                .storage(storage)
                .build();
    }

}
