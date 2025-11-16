package ru.akarpo.openprofile.is_openprofile;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IsOpenprofileApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        System.setProperty("POSTGRES_HOST", dotenv.get("POSTGRES_HOST"));
        System.setProperty("PGPORT", dotenv.get("PGPORT"));
        System.setProperty("POSTGRES_DB", dotenv.get("POSTGRES_DB"));
        System.setProperty("POSTGRES_USER", dotenv.get("POSTGRES_USER"));
        System.setProperty("POSTGRES_PASSWORD", dotenv.get("POSTGRES_PASSWORD"));

        SpringApplication.run(IsOpenprofileApplication.class, args);
    }

}


