package com.ericsson.cifwk.taf.scheduler;

import org.springframework.boot.SpringApplication;

public final class ApplicationLauncher {

    private ApplicationLauncher() {
    }

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "dev");
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }
}
