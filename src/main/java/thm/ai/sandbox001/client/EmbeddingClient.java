package thm.ai.sandbox001.client;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import thm.ai.sandbox001.domain.Vector;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EmbeddingClient {
    private final EmbeddingModel embeddingModel;
    private final ChatClient chatClient;


    public float[] getEmbeddings(String original) {
        Embedding result = embeddingModel.embed(original);
        return result.vector();
    }

    public Vector createAdditionalMetaData(Vector v) {
        Vector result = new Vector();
        result.setName("DESC " + v.getFileName());
        String description = chatClient.askQuestion(List.of(v), "Generate description of " +
                " class " + v.getName() +
                " located in file: " + v.getFileName() +
                " add information about package name, complexity of file and description of functionality");
        result.setOrigin(description);
        result.setHashCodeOrigin(description.hashCode());
        result.setSizeOrigin(description.length());
        result.setEmbedding(getEmbeddings(description));
        return result;
    }
}
