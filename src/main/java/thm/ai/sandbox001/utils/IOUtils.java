package thm.ai.sandbox001.utils;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import thm.ai.sandbox001.domain.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import static java.util.List.of;

@Slf4j
@Repository
@AllArgsConstructor
public class IOUtils {

    private final ResourceLoader resourceLoader;

    public List<File> loadAllFiles(String directoryPath) {
        List<File> result = of();
        try {
            File directory = new File(directoryPath);
            String[] extensions = {"java"};  // null = load all files regardless of extension
            boolean recursive = true;  // include subdirectories

            result = FileUtils.listFiles(directory, extensions, recursive).stream().toList();

        } catch (Exception e) {
            System.out.println("An error occurred while loading files.");
            e.printStackTrace();
        }
        return result;
    }

    public Vector loadFileContent(Vector vector) {
        Resource resource = resourceLoader.getResource("file:" + vector.getFileName());
        try {
            vector.setOrigin(org.apache.commons.io.IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Not able to load data for {}", vector, e);
        }
        return vector;
    }

}
