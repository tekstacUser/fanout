package com.example;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
public class SnsController {

    @PostMapping("/sns/receive")
    public ResponseEntity<String> receive(@RequestBody String message) {
        System.out.println("SNS Message Received: " + message);
        return ResponseEntity.ok("OK");
    }
}
