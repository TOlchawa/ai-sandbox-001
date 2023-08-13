package thm.ai.sandbox001.client;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatGPTClient {
    private final EmbeddingModel embeddingModel;

    public float[] getEmbeddings(String original) {
        Embedding result = embeddingModel.embed(original);
        return result.vector();
    }
}
