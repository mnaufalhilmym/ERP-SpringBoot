package id.bengkelinovasi.erp.model.request;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import id.bengkelinovasi.erp.enumeration.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetProjectListRequest {

    @JsonIgnore
    @NotNull
    private UUID actorUserId;

    @JsonIgnore
    private LocalDate startDateFrom;

    @JsonIgnore
    private LocalDate startDateTo;

    @JsonIgnore
    private String client;

    @JsonIgnore
    private String qClient;

    @JsonIgnore
    private String clientEmail;

    @JsonIgnore
    private String qClientEmail;

    @JsonIgnore
    private String name;

    @JsonIgnore
    private String qName;

    @JsonIgnore
    private ProjectStatus status;

    @JsonIgnore
    private int page;

    @JsonIgnore
    private int size;

}
