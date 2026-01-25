package com.smarthub.smarthub.config.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    private String uploadDir;

    private String uploadUrl;

    public Path getPathUpload() {
        return Paths.get(this.uploadDir);
    }

    public String getFile() {
        return getPathUpload().toUri().toString();
    }
}