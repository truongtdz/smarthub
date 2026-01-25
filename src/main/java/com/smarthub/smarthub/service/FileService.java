package com.smarthub.smarthub.service;

import com.smarthub.smarthub.config.exception.AppException;
import com.smarthub.smarthub.config.web.FileProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileProperties fileProperties;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = fileProperties.getPathUpload();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Đã tạo thư mục upload: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload!", e);
        }
    }

    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException("File bị rỗng");
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (originalFileName.contains("..")) {
                throw new AppException("File không hợp lệ");
            }

            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }

            String baseName = originalFileName.substring(0, dotIndex).replaceAll("[^a-zA-Z0-9\\-_]", "_");
            String uniqueFileName = baseName + "_" + UUID.randomUUID() + fileExtension;

            Path targetLocation = fileProperties.getPathUpload().resolve(uniqueFileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return fileProperties.getUploadUrl() + uniqueFileName;
        } catch (Exception ex) {
            throw new AppException("Đăng file thất bại");
        }

    }
}