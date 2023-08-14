package thm.ai.sandbox001.client;

import com.google.gson.internal.LinkedTreeMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import thm.ai.sandbox001.db.VectorService;
import thm.ai.sandbox001.domain.Vector;
import thm.ai.sandbox001.utils.DistanceUtils;
import thm.ai.sandbox001.utils.IOUtils;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class SampleEngine {

    public static final float MIN_DISTANCE = 0.33f;
    private final IOUtils ioUtils;
    private final FileDataLoader fileDataLoader;
    private final VectorService vectorService;
    private final DistanceUtils distanceUtils;
    private final EmbeddingClient embeddingClient;
    private final ChatClient chatClient;

    private static boolean clearData = false;
    private static boolean loadData = false;
    private static boolean distanceMap = false;
    private static boolean updateAndLoadData = true;

    public List<Vector> createContext(String subject) {
        float[] embedding = embeddingClient.getEmbeddings(subject);
        List<Vector> vectorsWithoutData = vectorService.getAllVectorWithoutOrigin();
        LinkedTreeMap<Float, Vector> distanceTree = new LinkedTreeMap<>();
        vectorsWithoutData.forEach(v -> distanceTree.put(Float.valueOf(distanceUtils.distance(embedding, v.getEmbedding())), v));

        List<Map.Entry<Float, Vector>> sortedEntries = new ArrayList<>(distanceTree.entrySet());
        Collections.sort(sortedEntries, Comparator.comparing(Map.Entry::getKey));

        List<Vector> similar = sortedEntries.stream()
                .filter(e -> e.getKey().floatValue() < MIN_DISTANCE)
                .limit(3)
                .peek(e -> log.info("distance: {}", e.getKey()))
                .map(Map.Entry::getValue)
                .map(Vector::getId)
                .map(id -> vectorService.getVectorById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        List<Vector> related = similar.stream()
                .map(v -> Pair.of(Float.valueOf(distanceUtils.distance(embedding, v.getEmbedding())), v))
                .filter(p -> p.getLeft().floatValue() < MIN_DISTANCE)
                .filter(p -> !similar.contains(p.getRight()))
                .limit(2)
                .map(p -> p.getRight().getId())
                .map(id -> vectorService.getVectorById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        List<Vector> result = new ArrayList<>();
        result.addAll(similar);
        result.addAll(related);


        return result;
    }

    public void processData(String path) {

        if (clearData) {
            vectorService.getAllVectorIds().stream()
                    .map(id -> vectorService.getVectorById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .peek(v -> log.info("Removing from DB: {}", v))
                    .forEach(v -> vectorService.deleteVectorById(v.getId()));
        }

        if (loadData) {
            fileDataLoader.loadData(path).stream()
                    .peek(v -> {
                        Instant startT = Instant.now();
                        v.setEmbedding(embeddingClient.getEmbeddings(v.getOrigin()));
                        log.info("Calculate embedings for {} in {}", v.getName(), Duration.between(startT, Instant.now()).toMillis());
                    })
                    .map(v -> vectorService.saveVector(v))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        if (updateAndLoadData) {

            log.info("searching for deprecated files...");
            vectorService.getAllVectorIds().stream()
                    .map(id -> vectorService.getVectorById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(v -> Pair.of(new File(v.getFileName()), v))
                    .filter( p -> {
                        File file = p.getLeft();
                        Vector v = p.getRight();
                        if (!file.exists()) {
                            return true; // something
                        } else {
                            int currentHashCode = v.getHashCodeOrigin();
                            return !ioUtils.loadFileContent(file)
                                    .map(String::hashCode)
                                    .filter(hashCode -> hashCode == currentHashCode)
                                    .isPresent();
                        }
                    })
                    .map(Pair::getRight)
                    .peek(v -> log.info("Removing from DB: {}", v))
                    .forEach(v -> vectorService.deleteVectorById(v.getId()));

            log.info("searching for updated files files...");
            fileDataLoader.loadData(path).stream()
                    .filter( v -> {
                        int currentHashCodeOriginal = v.getHashCodeOrigin();
                        return !vectorService.getAllVectorHashCodeOrigin(v.getName())
                                .stream()
                                .anyMatch(val -> val.getHashCodeOrigin() == currentHashCodeOriginal);
                    })
                    .peek(v -> {
                        Instant startT = Instant.now();
                        v.setEmbedding(embeddingClient.getEmbeddings(v.getOrigin()));
                        log.info("Calculate embeddings for {} in {}", v.getName(), Duration.between(startT, Instant.now()).toMillis());
                    })
                    .map(v -> vectorService.saveVector(v))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        List<Vector> vectorsWithoutData = vectorService.getAllVectorWithoutOrigin();

        if (distanceMap) {
            float[][] distanceMap = new float[vectorsWithoutData.size()][vectorsWithoutData.size()];
            Vector[] v1 = vectorsWithoutData.toArray(new Vector[vectorsWithoutData.size()]);
            Vector[] v2 = vectorsWithoutData.toArray(new Vector[vectorsWithoutData.size()]);
            for (int x = 0; x < v1.length; x++) {
                for (int y = 0; y < v1.length; y++) {
                    distanceMap[x][y] = distanceUtils.distance(v1[x], v2[y]);
                }
            }

            for (int x = 0; x < v1.length; x++) {
                for (int y = 0; y < v1.length; y++) {
                    System.out.print(distanceMap[x][y] + " , ");
                }
                System.out.println();
            }
        }

    }

    public String processContext(List<Vector> contextVectors, String question) {
        return chatClient.askQuestion(contextVectors, question);
    }

}
