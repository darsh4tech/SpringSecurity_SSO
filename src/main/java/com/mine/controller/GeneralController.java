package com.mine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GeneralController {

    @GetMapping("/all")
    public ResponseEntity<String> getContent() {
        return ResponseEntity.ok("Public content goes here");
    }
	
}
