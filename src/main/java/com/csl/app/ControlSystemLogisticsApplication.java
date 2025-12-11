package com.csl.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // <--- Sin paréntesis ni excludes
public class ControlSystemLogisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ControlSystemLogisticsApplication.class, args);
    }
}