package thm.ai.sandbox001.client;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@AllArgsConstructor
public class EmbeddingClient {
    private final EmbeddingModel embeddingModel;

    public float[] getEmbeddings(String original) {
        Embedding result = embeddingModel.embed(original);
        return result.vector();
    }
}
