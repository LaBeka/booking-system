package edu.com.bookingsystem.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequestMapping("/produce")
@RestController
public class ProducerController {

    @Value("${data.file.location}")
    private String fileLocation;

    @GetMapping("/pro")
    public String pro() throws IOException {
        Path path = Path.of(fileLocation);
        if (!Files.exists(path)) {
            return "Error: File not found at " + path.toAbsolutePath() +
                    ". Please ensure you ran 'docker cp' correctly.";
        }
        return Files.readString(path); //"Reading Booking system: Hello microservice world!";
    }

    @GetMapping("/prod")
    public String prosTRING() throws IOException {
        return "Reading Booking system: Hello microservice world! the string is from rest api ";
    }
}
