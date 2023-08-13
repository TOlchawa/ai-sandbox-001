package thm.ai.sandbox001.utils;

import org.springframework.stereotype.Repository;
import thm.ai.sandbox001.domain.Vector;

@Repository
public class DistanceUtils {

    /**
     * @return sum ( (v1[0] - v2[0])^2 + ... + (v1[k] - v2[k])^2 )
     */
    public float distance(Vector v1, Vector v2) {
        float[] vector1 = v1.getEmbedding();
        float[] vector2 = v2.getEmbedding();
        if (vector1.length != vector2.length) {
            throw new IllegalStateException("Different vector size: "+ vector1.length + " != " + vector2.length);
        }
        float distance = 0.0f;
        for (int i=0; i<vector1.length; i++) { // sum ( (v1[0] - v2[0])^2 + ... + (v1[k] - v2[k])^2 )
            double d = vector1[i] - vector2[i];
            distance += d * d;
        }
        return distance;
    }

}
