package dev.practice.cacheproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class CacheProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheProxyApplication.class, args);
    }

}
