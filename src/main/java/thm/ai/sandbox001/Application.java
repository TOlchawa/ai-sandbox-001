package thm.ai.sandbox001;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import thm.ai.sandbox001.utils.IOUtils;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
//            for (String arg : args) {
//                System.out.println(arg);
//
//            }
            String path = args[0];
            IOUtils.loadAllFiles(path)
                    .stream()
                    .peek(f -> System.out.println(f.getAbsolutePath()))
                    .toList();
        };
    }

}
