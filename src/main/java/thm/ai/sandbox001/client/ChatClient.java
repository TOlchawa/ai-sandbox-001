package thm.ai.sandbox001.client;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import thm.ai.sandbox001.domain.Vector;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ChatClient {
    public static final String USE_BELOW_MESSAGES_AS_CONTEXT = "use below messages as context";
    public static final String ANSWER_USER_QUESTION = "answer user question";
    ChatLanguageModel chatLanguageModel;

    public String askQuestion(List<Vector> contextVectors, String question) {

        List<ChatMessage> conversation = prepareMessages(contextVectors, question);
        Instant startT = Instant.now();
        AiMessage answer = chatLanguageModel.sendMessages(conversation);
        log.info("Response generated in {}", Duration.between(startT, Instant.now()).toSeconds());
        return answer.toString();
    }

    private List<ChatMessage> prepareMessages(List<Vector> contextVectors, String question) {
        List<ChatMessage> result = new ArrayList<>();
        result.add(new SystemMessage(USE_BELOW_MESSAGES_AS_CONTEXT));
        contextVectors.forEach(v -> result.add(new SystemMessage(v.getOrigin())));
        result.add(new SystemMessage(ANSWER_USER_QUESTION));
        result.add(new UserMessage(question));
        return result;
    }
}
