package kr.co.programmers.cafe.global.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

class FileManagerTest {
    FileManager fileManager = new FileManager();

    @Test
    void test() {
        MultipartFile multipartFile = new MockMultipartFile("test.png","test.png", "image/png", "test".getBytes());
        String filePath = fileManager.saveFile(multipartFile);
        File file = Path.of(fileManager.getFullPath(filePath)).toFile();
        assertThat(filePath).isNotNull();
        assertThat(file.exists()).isTrue();
        assertThat(Paths.get(file.toURI())).isEqualTo(Paths.get(fileManager.getFullPath(filePath)));

        fileManager.deleteFile(filePath);
        assertThat(file.exists()).isFalse();
    }
}