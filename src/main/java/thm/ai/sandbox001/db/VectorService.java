package thm.ai.sandbox001.db;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import thm.ai.sandbox001.domain.Vector;

import java.util.List;
import java.util.Optional;

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
        query.fields().include("_id", "vector", "name", "sizeOrigin");

        return mongoTemplate.find(query, Vector.class)
                .stream()
                .toList();
    }

    public List<Vector> getAllVectorHashCodeOrigin(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        query.fields().include("_id", "hashCodeOrigin", "name", "fileName");

        return mongoTemplate.find(query, Vector.class)
                .stream()
                .toList();
    }

}
