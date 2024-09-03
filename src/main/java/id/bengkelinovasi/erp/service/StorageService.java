package id.bengkelinovasi.erp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import id.bengkelinovasi.erp.entity.Storage;
import id.bengkelinovasi.erp.entity.Company;
import id.bengkelinovasi.erp.entity.Project;
import id.bengkelinovasi.erp.entity.User;
import id.bengkelinovasi.erp.enumeration.StorageObjectType;
import id.bengkelinovasi.erp.model.request.AddFileRequest;
import id.bengkelinovasi.erp.model.request.CreateFolderRequest;
import id.bengkelinovasi.erp.model.request.DeleteStorageRequest;
import id.bengkelinovasi.erp.model.request.GetStorageListRequest;
import id.bengkelinovasi.erp.model.request.GetStorageRequest;
import id.bengkelinovasi.erp.model.response.StorageListResponse;
import id.bengkelinovasi.erp.model.response.StorageResponse;
import id.bengkelinovasi.erp.repository.ProjectRepository;
import id.bengkelinovasi.erp.repository.StorageRepository;
import id.bengkelinovasi.erp.repository.UserRepository;
import id.bengkelinovasi.erp.util.ValidationUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.messages.DeleteError;
import io.minio.Result;
import io.minio.messages.DeleteObject;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StorageService {

    private static final Tika tika = new Tika();

    private static final Pattern FILENAME_PATTERN = Pattern.compile("[^a-zA-Z0-9.-_]");

    @Value("${minio.bucket-name}")
    private String BUCKET_NAME;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private MinioClient minioClient;

    @Transactional
    public UUID addFile(AddFileRequest request) {
        validationUtil.validate(request);

        User actorUser = userRepository.findById(request.getActorUserId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

        Storage file = new Storage();
        file.setType(StorageObjectType.FILE);
        if (request.getFolderId() != null) {
            Storage folder = storageRepository.findById(request.getFolderId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder tidak ditemukan"));
            if (!folder.getType().equals(StorageObjectType.FOLDER)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Penyimpanan bukan bertipe folder");
            }
            file.setParentFolder(folder);
            file.setProject(folder.getProject());
        }
        file.setName(sanitizeFilename(request.getFile().getOriginalFilename()));
        try {
            file.setMimeType(tika.detect(request.getFile().getInputStream()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Berkas tidak dapat dibaca");
        }
        file.setSize(request.getFile().getSize());
        file.setUser(actorUser);

        try (InputStream inputStream = request.getFile().getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(file.getId().toString())
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getMimeType())
                    .build());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Gagal menyimpan berkas");
        }

        storageRepository.save(file);

        updateParentFolderSize(file, file.getSize());

        return file.getId();
    }

    @Transactional
    public UUID createFolder(CreateFolderRequest request) {
        validationUtil.validate(request);

        User actorUser = userRepository.findById(request.getActorUserId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

        Storage folder = new Storage();
        folder.setType(StorageObjectType.FOLDER);
        if (request.getParentId() != null) {
            Storage parentFolder = storageRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder tidak ditemukan"));
            if (!parentFolder.getType().equals(StorageObjectType.FOLDER)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Penyimpanan bukan bertipe folder");
            }
            folder.setParentFolder(parentFolder);
            folder.setProject(parentFolder.getProject());
        } else if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proyek tidak ditemukan"));
            folder.setProject(project);
        }
        folder.setName(request.getName());
        folder.setUser(actorUser);

        storageRepository.save(folder);

        return folder.getId();
    }

    @Transactional
    public UUID delete(DeleteStorageRequest request) {
        validationUtil.validate(request);

        User actorUser = userRepository.findById(request.getActorUserId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

        Storage storage = storageRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Penyimpanan tidak ditemukan"));

        if (!storage.getUser().getCompany().getId().equals(actorUser.getCompany().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Penyimpanan tidak dimiliki perusahaan ini");
        }

        switch (storage.getType()) {
            case FOLDER:
                if (storage.getParentFolder() == null) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Folder tidak dapat dihapus");
                }

                Set<Storage> childrenFiles = getAllChildrenStorage(storage);

                List<DeleteObject> objectsToBeDeleted = new LinkedList<>();
                for (Storage file : childrenFiles) {
                    objectsToBeDeleted.add(new DeleteObject(file.getId().toString()));
                }
                Iterable<Result<DeleteError>> results = minioClient
                        .removeObjects(
                                RemoveObjectsArgs.builder().bucket(BUCKET_NAME).objects(objectsToBeDeleted).build());
                for (Result<DeleteError> result : results) {
                    try {
                        DeleteError error = result.get();
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Gagal menghapus berkas dengan id " + error.objectName());
                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Gagal menghapus berkas");
                    }
                }
                break;
            case FILE:
                try {
                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(BUCKET_NAME)
                                    .object(storage.getId().toString())
                                    .build());
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Gagal menghapus berkas");
                }
                break;
        }

        updateParentFolderSize(storage, -storage.getSize());

        storageRepository.delete(storage);

        return storage.getId();
    }

    @Transactional(readOnly = true)
    public Page<StorageListResponse> getMany(GetStorageListRequest request) {
        validationUtil.validate(request);

        User actorUser = userRepository.findById(request.getActorUserId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

        Company actorCompany = actorUser.getCompany();

        Storage parentStorage = null;
        if (request.getParentFolderId() != null) {
            parentStorage = storageRepository.findById(request.getParentFolderId())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder tidak ditemukan"));
            if (!parentStorage.getType().equals(StorageObjectType.FOLDER)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Penyimpanan bukan bertipe folder");
            }
        }

        Specification<Storage> specification = (root, query, builder) -> {
            if (query == null) {
                throw new IllegalArgumentException("CriteriaQuery cannot be null");
            }

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.equal(root.get("user").get("company").get("id"), actorCompany.getId()));

            if (request.getParentFolderId() != null) {
                predicates.add(builder.equal(root.get("parentFolder").get("id"), request.getParentFolderId()));
            } else {
                predicates.add(builder.isNull(root.get("parentFolder")));
            }

            if (request.getProjectId() != null) {
                predicates.add(builder.equal(root.get("project").get("id"), request.getProjectId()));
            }

            query.where(predicates.toArray(new Predicate[] {}));
            query.orderBy(builder.asc(root.get("type")), builder.asc(root.get("name")));

            return query.getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Storage> storagePage = storageRepository.findAll(specification, pageable);
        List<Storage> storageList = storagePage.getContent();

        List<StorageListResponse> StorageListResponses = new ArrayList<>();
        for (Storage storage : storageList) {
            StorageListResponses.add(StorageListResponse.fromEntity(storage, parentStorage));
        }

        return new PageImpl<>(StorageListResponses, pageable, storagePage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public StorageResponse get(GetStorageRequest request) {
        validationUtil.validate(request);

        User actorUser = userRepository.findById(request.getActorUserId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna tidak ditemukan"));

        Storage file = storageRepository.findById(request.getId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Berkas tidak ditemukan"));

        if (!file.getUser().getCompany().getId().equals(actorUser.getCompany().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Penyimpanan tidak dimiliki perusahaan ini");
        }

        if (!file.getType().equals(StorageObjectType.FILE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Penyimpanan bukan bertipe berkas");
        }

        InputStream fileStream = null;
        try {
            fileStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(file.getId().toString())
                    .build());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Gagal mendapatkan berkas dengan id " + file.getId());
        }

        return StorageResponse.fromEntity(file, fileStream);
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename is null or empty");
        }

        // Remove unwanted characters
        String sanitizedFilename = FILENAME_PATTERN.matcher(filename).replaceAll("_");

        // Optionally limit filename length
        if (sanitizedFilename.length() > 255) {
            sanitizedFilename = sanitizedFilename.substring(0, 255);
        }

        return sanitizedFilename;
    }

    private Set<Storage> getAllChildrenStorage(Storage parentStorage) {
        List<Storage> storageList = storageRepository.findAllByParentFolderId(parentStorage.getId());

        Set<Storage> files = new HashSet<>();
        for (Storage storage : storageList) {
            switch (storage.getType()) {
                case FOLDER -> getAllChildrenStorage(storage);
                case FILE -> files.add(storage);
            }
        }

        return files;
    }

    private void updateParentFolderSize(Storage storage, Long addition) {
        if (storage == null) {
            return;
        }

        Storage parentFolder = storage.getParentFolder();
        if (parentFolder != null) {
            parentFolder.setSize(parentFolder.getSize() + addition);
            storageRepository.save(parentFolder);
            updateParentFolderSize(parentFolder, addition);
        }
    }

}
