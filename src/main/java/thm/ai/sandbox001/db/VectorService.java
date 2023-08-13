package thm.ai.sandbox001.db;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import thm.ai.sandbox001.domain.Vector;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VectorService {
    private final VectorRepository vectorRepository;
    private final MongoTemplate mongoTemplate;

    public Optional<Vector> saveVector(Vector vector) {
        return Optional.of(vectorRepository.save(vector));
    }

    public Optional<Vector> getVectorById(String id) {
        return vectorRepository.findById(id);
    }

    public void deleteVectorById(String id) {
        vectorRepository.deleteById(id);
    }

    public List<String> getAllVectorIds() {
        Query query = new Query();
        query.fields().include("_id");

        return mongoTemplate.find(query, Vector.class)
                .stream()
                .map(Vector::getId)
                .toList();
    }

    public List<Vector> getAllVectorWithoutOrigin() {
        Query query = new Query();
        query.fields().include("_id", "vector");

        return mongoTemplate.find(query, Vector.class)
                .stream()
                .toList();
    }

}
