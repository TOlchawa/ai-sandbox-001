package thm.ai.sandbox001.utils;

import org.springframework.stereotype.Repository;
import thm.ai.sandbox001.domain.Vector;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Repository
public class DomainUtils {


    private static final long SEED = 123;
    public static final int DEFAULT_VECTOR_SIZE = 5;
    private final Random RND = new Random(SEED);

    public Vector prepareVector(File f) {
        Vector result = new Vector();
        result.setFileName(f.getAbsolutePath());
        result.setVector(generateRandomList(DEFAULT_VECTOR_SIZE));
        return result;
    }

    public List<Double> generateRandomList(int size) {
        return DoubleStream.generate(RND::nextDouble)
                .limit(size)
                .boxed()
                .collect(Collectors.toList());
    }
}
