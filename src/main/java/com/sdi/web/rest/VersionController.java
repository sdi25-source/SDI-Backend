package com.sdi.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class VersionController {

    @Value("${project.version:Unknown}")
    private String projectVersion;

    @GetMapping("/Backend-Version")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok(projectVersion);
    }
}
