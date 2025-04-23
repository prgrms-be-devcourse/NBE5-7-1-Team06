package kr.co.programmers.cafe.global.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileManager {
    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images";

    @PostConstruct
    public void init() {
        File file = new File(IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    //이미지 파일을 home/devcourse/images/에 저장합니다.
    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;
        String savedPath = IMAGE_DIRECTORY + "/" + savedFileName;

        try {
            file.transferTo(new File(savedPath));
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
        File file = new File(IMAGE_DIRECTORY + "/" + filePath);
        if(file.exists()){
            file.delete();
        }
    }
        
}
