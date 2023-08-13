package thm.ai.sandbox001.utils;

import org.springframework.stereotype.Repository;
import thm.ai.sandbox001.domain.Vector;

@Repository
public class DistanceUtils {

    /**
     * @return sum ( (v1[0] - v2[0])^2 + ... + (v1[k] - v2[k])^2 )
     */
    public double distance(Vector v1, Vector v2) {
        Double[] vector1 = v1.getVector().toArray(new Double[v1.getVector().size()]);
        Double[] vector2 = v2.getVector().toArray(new Double[v2.getVector().size()]);
        if (vector1.length != vector2.length) {
            throw new IllegalStateException("Different vector size: "+ vector1.length + " != " + vector2.length);
        }
        double distance = 0.0d;
        for (int i=0; i<vector1.length; i++) { // sum ( (v1[0] - v2[0])^2 + ... + (v1[k] - v2[k])^2 )
            double d = vector1[i] - vector2[i];
            distance += d * d;
        }
        return distance;
    }

}
