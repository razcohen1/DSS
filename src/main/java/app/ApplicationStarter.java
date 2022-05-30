package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ApplicationStarter {

    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(ApplicationStarter.class);
        springApplicationBuilder.headless(false);
        springApplicationBuilder.run(args);
//        SpringApplication.run(ApplicationStarter.class, args);
    }

}