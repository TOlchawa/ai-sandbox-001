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

    @Bean
    public CommandLineRunner commandLineRunner(SampleEngine sampleEngine) {
        return args -> {
            sampleEngine.processData(args[0]);
            Scanner scanner = new Scanner(System.in);
            while (!finish) {
                processInput(scanner);
            }

        };
    }

    private void processInput(Scanner scanner) {
        System.out.print("Enter input: ");
        String input = scanner.nextLine();
        if ("exit".equalsIgnoreCase(input)) {
            finish = true;
            applicationContext.close();
        } else {
            List<Vector> context = sampleEngine.createContext(input);
            context.stream().limit(10).forEach(v -> log.info("CONTEXT: {}", v));
        }
        System.out.println("Received input: " + input);
    }

}

