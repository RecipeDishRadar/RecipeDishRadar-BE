package com.ducnt.recipedishradar;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RecipeDishRadarApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        System.setProperty("JWT_SIGNATURE_KEY", dotenv.get("JWT_SIGNATURE_KEY"));

//        System.setProperty("CLOUDINARY_CLOUD_NAME", dotenv.get("CLOUDINARY_CLOUD_NAME"));
//        System.setProperty("CLOUDINARY_API_KEY", dotenv.get("CLOUDINARY_API_KEY"));
//        System.setProperty("CLOUDINARY_API_SECRET", dotenv.get("CLOUDINARY_API_SECRET"));

        System.setProperty("MONGODB_URI", dotenv.get("MONGODB_URI"));

        System.setProperty("SPRING_MAIL_HOST", dotenv.get("SPRING_MAIL_HOST"));
        System.setProperty("SPRING_MAIL_PORT", dotenv.get("SPRING_MAIL_PORT"));
        System.setProperty("SPRING_MAIL_PROTOCOL", dotenv.get("SPRING_MAIL_PROTOCOL"));
        System.setProperty("SPRING_MAIL_USERNAME", dotenv.get("SPRING_MAIL_USERNAME"));
        System.setProperty("SPRING_MAIL_PASSWORD", dotenv.get("SPRING_MAIL_PASSWORD"));

        System.setProperty("SPRING_DATA_REDIS_HOST", dotenv.get("SPRING_DATA_REDIS_HOST"));
        System.setProperty("SPRING_DATA_REDIS_PORT", dotenv.get("SPRING_DATA_REDIS_PORT"));
        System.setProperty("SPRING_REDIS_TIMEOUT", dotenv.get("SPRING_REDIS_TIMEOUT"));

        SpringApplication.run(RecipeDishRadarApplication.class, args);
    }

}
