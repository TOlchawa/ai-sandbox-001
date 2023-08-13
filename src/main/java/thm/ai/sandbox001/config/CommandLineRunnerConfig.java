package thm.ai.sandbox001.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thm.ai.sandbox001.client.FileDataLoader;
import thm.ai.sandbox001.client.SampleEngine;
import thm.ai.sandbox001.db.VectorService;

import java.util.Optional;

@Configuration
public class CommandLineRunnerConfig {

    @Bean
    public CommandLineRunner commandLineRunner(SampleEngine sampleEngine) {
        return args -> {
            sampleEngine.processData(args[0]);
        };
    };
}

