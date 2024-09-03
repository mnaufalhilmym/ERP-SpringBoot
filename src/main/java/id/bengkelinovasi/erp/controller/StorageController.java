package id.bengkelinovasi.erp.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import id.bengkelinovasi.erp.model.request.AddFileRequest;
import id.bengkelinovasi.erp.model.request.CreateFolderRequest;
import id.bengkelinovasi.erp.model.request.DeleteStorageRequest;
import id.bengkelinovasi.erp.model.request.GetStorageListRequest;
import id.bengkelinovasi.erp.model.request.GetStorageRequest;
import id.bengkelinovasi.erp.model.response.StorageListResponse;
import id.bengkelinovasi.erp.model.response.StorageResponse;
import id.bengkelinovasi.erp.model.response.WebPaginationResponse;
import id.bengkelinovasi.erp.model.response.WebResponse;
import id.bengkelinovasi.erp.service.StorageService;

@RestController
public class StorageController {

        @Autowired
        private StorageService storageService;

        @PostMapping(path = "/api/storage/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public WebResponse<UUID> addFile(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam("file") MultipartFile file,
                        @RequestParam(name = "folder_id", required = false) UUID folderId) {
                if (file.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File harus ada");
                }

                UUID actorUserId = UUID.fromString(userDetails.getUsername());

                AddFileRequest addFileRequest = AddFileRequest.builder()
                                .actorUserId(actorUserId)
                                .file(file)
                                .folderId(folderId)
                                .build();

                UUID id = storageService.addFile(addFileRequest);

                return WebResponse.<UUID>builder().data(id).build();
        }

        @PostMapping(path = "/api/storage/folder", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public WebResponse<UUID> addFolder(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody CreateFolderRequest request) {
                UUID actorUserId = UUID.fromString(userDetails.getUsername());
                request.setActorUserId(actorUserId);
                UUID folderId = storageService.createFolder(request);
                return WebResponse.<UUID>builder().data(folderId).build();
        }

        @DeleteMapping(path = "/api/storage/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public WebResponse<UUID> deleteStorage(@AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable("id") UUID id) {
                UUID actorUserId = UUID.fromString(userDetails.getUsername());
                DeleteStorageRequest request = DeleteStorageRequest.builder().actorUserId(actorUserId).id(id).build();
                UUID storageId = storageService.delete(request);
                return WebResponse.<UUID>builder().data(storageId).build();
        }

        @GetMapping(path = "/api/storage", produces = MediaType.APPLICATION_JSON_VALUE)
        public WebResponse<List<StorageListResponse>> getStorageList(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(name = "parent_folder_id", required = false) UUID parentFolderId,
                        @RequestParam(name = "project_id", required = false) UUID projectId,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size) {
                UUID actorUserId = UUID.fromString(userDetails.getUsername());

                GetStorageListRequest request = GetStorageListRequest.builder()
                                .actorUserId(actorUserId)
                                .parentFolderId(parentFolderId)
                                .projectId(projectId)
                                .page(page - 1)
                                .size(size)
                                .build();

                Page<StorageListResponse> ProjectListResponse = storageService.getMany(request);

                return WebResponse.<List<StorageListResponse>>builder()
                                .data(ProjectListResponse.getContent())
                                .pagination(WebPaginationResponse.builder()
                                                .size(ProjectListResponse.getSize())
                                                .total(ProjectListResponse.getTotalElements())
                                                .page(ProjectListResponse.getNumber())
                                                .totalPages(ProjectListResponse.getTotalPages())
                                                .build())
                                .build();
        }

        @GetMapping(path = "/api/storage/{id}")
        public ResponseEntity<InputStreamResource> getStorage(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable("id") UUID id) {
                UUID actorUserId = UUID.fromString(userDetails.getUsername());

                GetStorageRequest request = GetStorageRequest.builder()
                                .actorUserId(actorUserId)
                                .id(id)
                                .build();

                StorageResponse storageResponse = storageService.get(request);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_TYPE, storageResponse.getMimeType())
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "inline; filename=\"" + storageResponse.getName() + "\"")
                                .body(new InputStreamResource(storageResponse.getFileStream()));
        }

}
