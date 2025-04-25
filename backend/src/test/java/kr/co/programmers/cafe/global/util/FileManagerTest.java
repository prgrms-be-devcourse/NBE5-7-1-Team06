package kr.co.programmers.cafe.global.util;

import kr.co.programmers.cafe.global.exception.FileNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileManagerTest {
    FileManager fileManager = new FileManager();
    private MockMultipartFile mockFile;
    private String testFileName;

    @BeforeEach
    void setUp() {
        testFileName = "test-image.png";
        String content = "test file content";
        mockFile = new MockMultipartFile(
                "file",
                testFileName,
                MediaType.IMAGE_PNG_VALUE,
                content.getBytes()
        );
    }

    @AfterEach
    void cleanup() throws IOException {
        File testFile = new File(fileManager.getAbsolutePath(testFileName));
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void saveFile_Success() throws IOException {
        String savedFileName = fileManager.saveFile(mockFile);

        assertThat(savedFileName).isNotNull();
        File savedFile = new File(fileManager.getAbsolutePath(savedFileName));
        assertThat(savedFile.exists()).isTrue();
    }

    @Test
    void getImage_Success() throws IOException {
        String savedFileName = fileManager.saveFile(mockFile);

        Resource resource = fileManager.getFileResource(savedFileName);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
    }

    @Test
    void getImage_NotFound404() throws IOException {
        assertThatThrownBy(() ->
                fileManager.getFileResource("InvalidFileName.png")
        ).isInstanceOf(FileNotFoundException.class);
    }

    @Test
    void deleteFile_Success() throws IOException {
        String savedFileName = fileManager.saveFile(mockFile);
        File savedFile = new File(fileManager.getAbsolutePath(savedFileName));
        assertThat(savedFile.exists()).isTrue();

        fileManager.deleteFile(savedFileName);

        assertThat(savedFile.exists()).isFalse();
    }

    @Test
    void getMediaType_Success() {
        assertThat(fileManager.getMediaType("test.png")).isEqualTo(MediaType.IMAGE_PNG);
        assertThat(fileManager.getMediaType("test.jpg")).isEqualTo(MediaType.IMAGE_JPEG);
        assertThat(fileManager.getMediaType("test.jpeg")).isEqualTo(MediaType.IMAGE_JPEG);
        assertThat(fileManager.getMediaType("test.gif")).isEqualTo(MediaType.IMAGE_GIF);
        assertThat(fileManager.getMediaType("test")).isEqualTo(MediaType.IMAGE_PNG);
        assertThat(fileManager.getMediaType(null)).isEqualTo(null);

    }

}