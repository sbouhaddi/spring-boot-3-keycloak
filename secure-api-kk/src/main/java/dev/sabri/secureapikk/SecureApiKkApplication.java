package dev.sabri.secureapikk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "dev.sabri")
public class SecureApiKkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureApiKkApplication.class, args);
    }

}
