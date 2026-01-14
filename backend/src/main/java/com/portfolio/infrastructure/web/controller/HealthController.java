package com.portfolio.infrastructure.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check and status controller
 * Provides application status endpoints
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    /**
     * Get application status
     * @return ResponseEntity - application status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "application", "Portfolio Backend",
            "version", "1.0.0",
            "timestamp", LocalDateTime.now().toString(),
            "modules", Map.of(
                "algorithms", "ACTIVE",
                "codeLab", "ACTIVE",
                "dbLab", "ACTIVE",
                "patterns", "ACTIVE"
            )
        ));
    }

    /**
     * Get API documentation links
     * @return ResponseEntity - documentation links
     */
    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> getDocs() {
        return ResponseEntity.ok(Map.of(
            "swagger", "/swagger-ui.html",
            "openapi", "/api-docs",
            "endpoints", Map.of(
                "algorithms", "/api/v1/algorithms",
                "codeLab", "/api/v1/code-lab",
                "dbLab", "/api/v1/db-lab"
            )
        ));
    }

    /**
     * Get showcase summary
     * @return ResponseEntity - showcase data
     */
    @GetMapping("/showcase")
    public ResponseEntity<Map<String, Object>> getShowcase() {
        return ResponseEntity.ok(Map.of(
            "title", "Interactive Tech Portfolio",
            "description", "Demonstrating backend development skills with Spring Boot",
            "features", Map.of(
                "cleanArchitecture", "Domain-driven design with layered architecture",
                "designPatterns", "Strategy, Factory, Builder, Repository patterns",
                "algorithmVisualization", "Interactive data structure demonstrations",
                "databaseTuning", "Query optimization comparison and analysis",
                "codeDisplay", "Live code snippets with SQL capture"
            ),
            "technologies", Map.of(
                "framework", "Spring Boot 3.2",
                "database", "PostgreSQL 16",
                "cache", "Redis 7",
                "messageQueue", "RabbitMQ 3",
                "language", "Java 21"
            ),
            "apiEndpoints", Map.of(
                "algorithms", "/api/v1/algorithms",
                "codeLab", "/api/v1/code-lab",
                "dbLab", "/api/v1/db-lab",
                "documentation", "/swagger-ui.html"
            )
        ));
    }
}
