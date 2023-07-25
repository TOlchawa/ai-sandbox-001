package thm.ai.sandbox001.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static java.util.List.of;

@UtilityClass
public class IOUtils {

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

}
