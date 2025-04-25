package kr.co.programmers.cafe.global.util;

import jakarta.annotation.PostConstruct;
import kr.co.programmers.cafe.global.exception.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Component
public class FileManager {
    private static final String IMAGE_DIRECTORY = System.getProperty("user.home") + "/devcourse/images/";

    @PostConstruct
    public void init() {
        File file = Path.of(IMAGE_DIRECTORY).toFile();
        log.info("file.getAbsoluteFile()={}", file.getAbsoluteFile());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public String getAbsolutePath(String fileName) {
        return IMAGE_DIRECTORY + "/" + fileName;
    }

    //이미지 파일을 home/devcourse/images/에 저장합니다.
    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;
        String savedPath = getAbsolutePath(savedFileName);

        try {
            file.transferTo(Path.of(savedPath).toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return savedFileName;
    }

    //해당 이미지 파일을 삭제합니다.
    public void deleteFile(String fileName) {
        if (fileName == null) {
            return;
        }
        File file = Path.of(getAbsolutePath(fileName)).toFile();
        if (file.exists()) {
            file.delete();
        }
    }

    public Resource getFileResource(String fileName) {
        if (fileName == null) {
            throw new FileNotFoundException(null);
        }
        String fullPath = getAbsolutePath(fileName);
        Resource resource = new FileSystemResource(Path.of(fullPath));
        if (!resource.exists()) {
            throw new FileNotFoundException(null);
        }
        return resource;
    }

    public MediaType getMediaType(String fileName) {
        if (fileName != null) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            switch (extension) {
                case "jpg":
                case "jpeg":
                    return MediaType.IMAGE_JPEG;
                case "gif":
                    return MediaType.IMAGE_GIF;
                case "png":
                default:
                    return MediaType.IMAGE_PNG;
            }
        }
        return null;
    }

}
