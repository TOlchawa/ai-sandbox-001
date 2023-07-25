package thm.ai.sandbox001.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import thm.ai.sandbox001.domain.Vector;


@Repository
public interface VectorRepository extends MongoRepository<Vector, String> {

}
