package thm.ai.sandbox001.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import thm.ai.sandbox001.domain.Vector;
import thm.ai.sandbox001.utils.DomainUtils;
import thm.ai.sandbox001.utils.IOUtils;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FileDataLoader {

    private final IOUtils ioUtils;
    private final DomainUtils domainUtils;

    public List<Vector> loadData(String path) {
        return ioUtils.loadAllFiles(path)
                .stream()
                .peek(f -> log.info(f.getAbsolutePath()))
                .map(f -> domainUtils.prepareVector(f))
                .map(v -> ioUtils.loadFileContent(v))
                .toList();
    }

}
