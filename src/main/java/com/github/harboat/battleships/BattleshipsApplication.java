package com.github.harboat.battleships;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(
        scanBasePackages = {
                "com.github.harboat.rabbitmq",
                "com.github.harboat.battleships"
        }
)
@EnableEurekaClient
public class BattleshipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BattleshipsApplication.class, args);
    }

}
