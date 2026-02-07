package edu.com.bookingsystem.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/service")
@RestController
public class GreetingController {

    @GetMapping("/hello")
    public String hello(){
        return "Booking system: Hello microservice world!";
    }
}
