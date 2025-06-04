package com.sdi.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class VersionController {

    @Value("${git.commit.id.describe:unknown}")
    private String gitDescribe;

    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        if (gitDescribe == null || gitDescribe.isEmpty()) {
            return ResponseEntity.ok("unknown");
        }
        // extraire le tag, la partie avant le premier '-'
        String tag = gitDescribe.split("-")[0];
        return ResponseEntity.ok(tag);
    }

}
