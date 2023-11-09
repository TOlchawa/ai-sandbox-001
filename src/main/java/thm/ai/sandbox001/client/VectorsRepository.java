package thm.ai.sandbox001.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import thm.ai.sandbox001.db.VectorService;
import thm.ai.sandbox001.domain.Vector;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Repository
public class VectorsRepository {

    private final VectorService vectorService;

    public void removeDeprecatedData(Vector v) {
        vectorService.getVectorByParentId(v.getId()).forEach(
            child -> vectorService.deleteVectorById(child.getId())
        );
        vectorService.deleteVectorById(v.getId());
    }

    public void removeVector(Vector v) {
        vectorService.deleteVectorById(v.getId());
    }

    public List<Vector> getAllVectorWithoutOrigin() {
        return vectorService.getAllVectorWithoutOrigin();
    }

    public Optional<Vector> saveVector(Vector v) {
        return vectorService.saveVector(v);
    }

    public List<String> getAllVectorIds() {
        return vectorService.getAllVectorIds();
    }

    public List<String> getAllVectorWithGeneratedDataIds() {
        return vectorService.getAllVectorWithGeneratedDataIds();
    }


    public List<String> getAllVectorWithFileNameIds() {
        return vectorService.getAllVectorWithFileNameIds();
    }

    public Optional<Vector> getVectorById(String id) {
        return vectorService.getVectorById(id);
    }

    public List<Vector> getAllForFileName(String fileName) {
        return vectorService.getAllForFileName(fileName);
    }
}
