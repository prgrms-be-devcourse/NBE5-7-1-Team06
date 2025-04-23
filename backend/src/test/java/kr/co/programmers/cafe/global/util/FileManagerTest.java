package kr.co.programmers.cafe.global.util;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {
    FileManager fileManager = new FileManager();

    @Test
    void test() {
        MultipartFile multipartFile = new MockMultipartFile("test.png","test.png", "image/png", "test".getBytes());
        String filePath = fileManager.saveFile(multipartFile);
        File file = new File(fileManager.getAbsolutePath(filePath));
        assertThat(filePath).isNotNull();
        assertThat(file.exists()).isTrue();
        assertThat(Paths.get(file.toURI())).isEqualTo(Paths.get(fileManager.getAbsolutePath(filePath)));

        fileManager.deleteFile(filePath);
        assertThat(file.exists()).isFalse();
    }

}