package thm.ai.sandbox001.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import thm.ai.sandbox001.db.VectorService;
import thm.ai.sandbox001.domain.Vector;
import thm.ai.sandbox001.utils.DistanceUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class SampleEngine {

    private final FileDataLoader fileDataLoader;
    private final VectorService vectorService;
    private final DistanceUtils distanceUtils;
    private final ChatGPTClient chatGPTClient;

    public void processData(String path) {

//        vectorService.getAllVectorIds().stream()
//                .map(id -> vectorService.getVectorById(id))
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .peek(v -> log.info("Removing from DB: {}", v))
//                .forEach(v -> vectorService.deleteVectorById(v.getId()));

//        fileDataLoader.loadData(path).stream()
//                .peek(v -> v.setEmbedding(chatGPTClient.getEmbeddings(v.getOrigin())))
//                .map(v -> vectorService.saveVector(v))
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .map(v -> vectorService.getVectorById(v.getId()))
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .peek(v -> log.info("Loaded from DB: {}", v))
//                .toList();


        List<Vector> vectorsWithoutData = vectorService.getAllVectorWithoutOrigin();
        float[][] distanceMap = new float[vectorsWithoutData.size()][vectorsWithoutData.size()];

        Vector[] v1 = vectorsWithoutData.toArray(new Vector[vectorsWithoutData.size()]);
        Vector[] v2 = vectorsWithoutData.toArray(new Vector[vectorsWithoutData.size()]);
        for(int x = 0; x < v1.length; x++) {
            for(int y = 0; y < v1.length; y++) {
                distanceMap[x][y] = distanceUtils.distance(v1[x],v2[y]);
            }
        }

        for(int x = 0; x < v1.length; x++) {
            for(int y = 0; y < v1.length; y++) {
                System.out.print(distanceMap[x][y] + " , ");
            }
            System.out.println();
        }

    }

}
