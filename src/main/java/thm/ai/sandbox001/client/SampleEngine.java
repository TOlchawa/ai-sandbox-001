package thm.ai.sandbox001.client;

import com.google.gson.internal.LinkedTreeMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import thm.ai.sandbox001.domain.Vector;
import thm.ai.sandbox001.utils.DistanceUtils;
import thm.ai.sandbox001.utils.IOUtils;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

@Slf4j
@AllArgsConstructor
@Service
public class SampleEngine {

    public static final float MIN_DISTANCE = 0.33f;
    private static final int MAX_DATA_LEN = 5000;
    private final IOUtils ioUtils;
    private final FileDataLoader fileDataLoader;

    private final DistanceUtils distanceUtils;
    private final EmbeddingClient embeddingClient;
    private final ChatClient chatClient;
    private final VectorsRepository vectorsRepository;

    private static boolean clearData = false;
    private static boolean clearGeneratedData = false;
    private static boolean generateDataForAllFiles = true;
    private static boolean loadData = false;
    private static boolean distanceMap = false;
    private static boolean updateAndLoadData = true;

    public List<Vector> createContext(String subject) {

        // get VECTOR for question
        float[] embedding = embeddingClient.getEmbeddings(subject);

        // get all vectors
        List<Vector> vectorsWithoutData = vectorsRepository.getAllVectorWithoutOrigin();
        LinkedTreeMap<Float, Vector> distanceTree = new LinkedTreeMap<>();

        // calculate distance from to VECTOR
        vectorsWithoutData.forEach(v -> distanceTree.put(Float.valueOf(distanceUtils.distance(embedding, v.getEmbedding())), v));

        List<Map.Entry<Float, Vector>> sortedEntries = new ArrayList<>(distanceTree.entrySet());

        Collections.sort(sortedEntries, Comparator.comparing(Map.Entry::getKey));

        AtomicInteger currentSize = new AtomicInteger(0);
        List<Vector> similar = sortedEntries.stream()
                .filter(e -> e.getKey().floatValue() < MIN_DISTANCE)
                .peek(e -> log.info("distance: {}", e.getKey()))
                .map(Map.Entry::getValue)
                .filter(v -> {
                    boolean result = (MAX_DATA_LEN > (currentSize.get() + v.getSizeOrigin()));
                    if (result) {
                        currentSize.addAndGet(v.getSizeOrigin());
                    }
                    return result;
                })
                .map(Vector::getId)
                .map(id -> vectorsRepository.getVectorById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        log.info("similar: {}", similar);

        List<Vector> related = similar.stream()
                .map(v -> Pair.of(Float.valueOf(distanceUtils.distance(embedding, v.getEmbedding())), v))
//                .filter(p -> p.getLeft().floatValue() < MIN_DISTANCE)
                .filter(p -> !similar.contains(p.getRight()))
//                .limit(2)
                .map(p -> p.getRight().getId())
                .map(id -> vectorsRepository.getVectorById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(v -> {
                    boolean result = (MAX_DATA_LEN > (currentSize.get() + v.getSizeOrigin()));
                    if (result) {
                        currentSize.addAndGet(v.getSizeOrigin());
                    }
                    return result;
                })
                .toList();

        log.info("related: {}", similar);

        List<Vector> result = new ArrayList<>();
        result.addAll(similar);
        result.addAll(related);

        log.info("CONTEXT.SIZE: {}", result.size());
        result.forEach(v -> {
            log.info("CONTEXT: {}", v);
        });

        return result;
    }

    public void processData(String path) {

        if (clearData) {
            vectorsRepository.getAllVectorIds().stream()
                    .map(id -> vectorsRepository.getVectorById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .peek(v -> log.info("Removing from DB: {}", v))
                    .forEach(v -> vectorsRepository.removeVector(v));
        }

        if (clearGeneratedData) {
            vectorsRepository.getAllVectorWithGeneratedDataIds().stream()
                    .map(id -> vectorsRepository.getVectorById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .peek(v -> log.info("Removing from DB: {}", v))
                    .forEach(v -> vectorsRepository.removeVector(v));
        }

        if (generateDataForAllFiles) {
            vectorsRepository.getAllVectorWithFileNameIds().stream()
                    .map(id -> vectorsRepository.getVectorById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(v -> {
                        generateDescriptionDataForFileName(v);
                        generateTechnicalDescriptionDataForFileName(v);
                    });
        }

        if (loadData) {
            List<Vector> vectors = generateEmbeddings(path);
            generateEmbeddingsOfDescriptions(vectors);
        }

        if (updateAndLoadData) {

            log.info("searching for deprecated files...");
            vectorsRepository.getAllVectorWithFileNameIds().stream()
                    .map(id -> vectorsRepository.getVectorById(id))
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
                    .forEach(v -> vectorsRepository.removeDeprecatedData(v));

            log.info("searching for updated files files...");
            fileDataLoader.loadData(path).stream()
                    .filter(this::isModified)
                    .peek(v -> {
                        Instant startT = Instant.now();
                        v.setEmbedding(embeddingClient.getEmbeddings(v.getOrigin()));
                        log.info("Calculate embeddings for {} in {} [TODO: need to update related data]", v.getName(), Duration.between(startT, Instant.now()).toMillis());
                    })
                    .map(v -> vectorsRepository.saveVector(v))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        List<Vector> vectorsWithoutData = vectorsRepository.getAllVectorWithoutOrigin();

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

    private void generateDescriptionDataForFileName(Vector v) {
        log.info("v: {}", v);
        String question = String.format("Describe functionality for file:\n%s\n\n%s", v.getFileName(), v.getOrigin());
        log.info("question: {}", question);
        String response = chatClient.askQuestion(Collections.emptyList(), question);
        log.info("response: {}", response);

        float[] emeddings = embeddingClient.getEmbeddings(response);

        Vector result =  new Vector();
        result.setName(v.getName());
        result.setOrigin(response);
        result.setEmbedding(emeddings);
        result.setHashCodeOrigin(response.hashCode());
        result.setParentId(v.getId());
        result.setTags(List.of("generated"));

        vectorsRepository.saveVector(result);
    }


    private void generateTechnicalDescriptionDataForFileName(Vector v) {
        log.info("v: {}", v);
        String question = String.format("Create short technical description of file (complexity, used technologies, used frameworks) for file:\n%s\n\n%s", v.getFileName(), v.getOrigin());
        log.info("question: {}", question);
        String response = chatClient.askQuestion(Collections.emptyList(), question);
        log.info("response: {}", response);

        float[] emeddings = embeddingClient.getEmbeddings(response);

        Vector result =  new Vector();
        result.setName(v.getName());
        result.setOrigin(response);
        result.setEmbedding(emeddings);
        result.setHashCodeOrigin(response.hashCode());
        result.setParentId(v.getId());
        result.setTags(List.of("generated"));

        vectorsRepository.saveVector(result);
    }

    private boolean isModified(Vector v) {
        log.info("checking hash code for {}", v.getFileName());
        int currentHashCodeOriginal = v.getHashCodeOrigin();
        return vectorsRepository.getAllForFileName(v.getFileName())
                .stream()
                .anyMatch(val -> val.getHashCodeOrigin() != currentHashCodeOriginal);
    }


    private List<Vector>  generateEmbeddings(String path) {
        List<Vector> listOfVectors = fileDataLoader.loadData(path).stream()
                .map(v -> {
                    Instant startT = Instant.now();
                    v.setEmbedding(embeddingClient.getEmbeddings(v.getOrigin()));
                    log.info("Calculate embedings for {} in {}", v.getName(), Duration.between(startT, Instant.now()).toMillis());
                    return v;
                })
                .map(v -> vectorsRepository.saveVector(v))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return listOfVectors;
    }

    private void generateEmbeddingsOfDescriptions(List<Vector> vectors) {
        vectors.stream()
                .map(v -> {
                    Instant startT = Instant.now();
                    Optional<Vector> result = vectorsRepository.saveVector(embeddingClient.createAdditionalMetaData(v));
                    log.info("Calculate embedings of description for {} in {}", v.getName(), Duration.between(startT, Instant.now()).toMillis());
                    return result;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public String processContext(List<Vector> contextVectors, String question) {
        return chatClient.askQuestion(contextVectors, question);
    }

}
