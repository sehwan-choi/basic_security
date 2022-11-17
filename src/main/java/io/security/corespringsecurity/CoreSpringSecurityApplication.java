package io.security.corespringsecurity;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class CoreSpringSecurityApplication {

    public static void main(String[] args) {

        SpringApplication.run(CoreSpringSecurityApplication.class, args);
    }
}
