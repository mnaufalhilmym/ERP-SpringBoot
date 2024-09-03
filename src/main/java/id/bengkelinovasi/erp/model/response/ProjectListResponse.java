package id.bengkelinovasi.erp.model.response;

import java.time.LocalDate;
import java.util.UUID;

import id.bengkelinovasi.erp.entity.Project;
import id.bengkelinovasi.erp.enumeration.ProjectStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectListResponse {

    private UUID id;

    private LocalDate startDate;

    private String client;

    private String clientEmail;

    private String name;

    private String category;

    private ProjectStatus status;

    public static ProjectListResponse fromEntity(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectListResponse.builder()
                .id(project.getId())
                .startDate(project.getStartDate())
                .client(project.getClient())
                .clientEmail(project.getClientEmail())
                .name(project.getName())
                .category(project.getCategory())
                .status(project.getStatus())
                .build();
    }

}
