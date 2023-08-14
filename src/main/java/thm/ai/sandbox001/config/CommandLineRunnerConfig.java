package thm.ai.sandbox001.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thm.ai.sandbox001.client.SampleEngine;
import thm.ai.sandbox001.domain.Vector;

import java.util.List;
import java.util.Scanner;

@Slf4j
@Configuration
@AllArgsConstructor
public class CommandLineRunnerConfig {

    private final ConfigurableApplicationContext applicationContext;
    private final SampleEngine sampleEngine;
    private static boolean finish = false;

    private static StringBuffer buffer = new StringBuffer();

    @Bean
    public CommandLineRunner commandLineRunner(SampleEngine sampleEngine) {
        return args -> {
            sampleEngine.processData(args[0]);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter input ({context}, 'enter', 'exit') : ");
            while (!finish) {
                processInput(scanner);
            }

        };
    }

    private void processInput(Scanner scanner) {
        String input = scanner.nextLine();
        if ("exit".equalsIgnoreCase(input)) {
            finish = true;
            applicationContext.close();
        } else if ("enter".equalsIgnoreCase(input)) {
            String question = buffer.toString();
            buffer = new StringBuffer();
            List<Vector> contextVectors = sampleEngine.createContext(question);

            log.info("Question: {}", question);
            contextVectors.forEach(v -> log.info("context: {}", v));

            String answer = sampleEngine.processContext(contextVectors, question);

            log.info("ANSWER:\n{}", answer);

            System.out.println("Enter input ({question}, 'enter', 'exit') : ");
        } else {
            buffer.append(input).append(System.lineSeparator());
        }
    }

}

