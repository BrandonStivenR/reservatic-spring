package com.reservatic.reservaticspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Clase principal: arranca Spring Boot con el servidor Tomcat embebido. */
@SpringBootApplication
public class ReservaticSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservaticSpringApplication.class, args);
    }
}