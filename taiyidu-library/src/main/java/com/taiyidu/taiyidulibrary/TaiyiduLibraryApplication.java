package com.taiyidu.taiyidulibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TaiyiduLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaiyiduLibraryApplication.class, args);
    }

}
