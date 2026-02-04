package com.sns.analyzer.controller;

import com.sns.analyzer.service.CommentCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final CommentCleanupService commentCleanupService;

    @PostMapping("/cleanup")
    public ResponseEntity<?> triggerCleanup() {
        commentCleanupService.cleanupOldComments();
        return ResponseEntity.ok(Map.of("message", "Cleanup triggered manually"));
    }
}