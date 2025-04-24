package kr.co.programmers.cafe.global.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    public String getFullPath(String filePath) {
        return IMAGE_DIRECTORY + "/" + filePath;
    }

    //이미지 파일을 home/devcourse/images/에 저장합니다.
    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;
        String savedPath = getFullPath(savedFileName);

        try {
            file.transferTo(Path.of(savedPath).toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return savedFileName;
    }

    //해당 이미지 파일을 삭제합니다.
    public void deleteFile(String filePath){
        if(filePath == null){
            return;
        }
        File file = Path.of(getFullPath(filePath)).toFile();
        if(file.exists()){
            file.delete();
        }
    }

    public byte[] getFile(String filePath){
        String fullPath = getFullPath(filePath);
        File file = new File(fullPath);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
        
}
