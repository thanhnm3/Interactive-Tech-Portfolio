package com.portfolio.infrastructure.web.controller;

import com.portfolio.application.dto.CodeSnippetDto;
import com.portfolio.application.dto.SqlCaptureDto;
import com.portfolio.application.service.CodeDisplayService;
import com.portfolio.application.service.DesignPatternDocService;
import com.portfolio.application.service.DesignPatternDocService.PatternDoc;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for code display and design pattern documentation
 * Provides APIs for code snippets, SQL capture, and pattern docs
 */
@RestController
@RequestMapping("/api/v1/code-lab")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
public class CodeLabController {

    private final CodeDisplayService codeDisplayService;
    private final DesignPatternDocService patternDocService;

    /**
     * Constructor with dependency injection
     * @param codeDisplayService - code display service
     * @param patternDocService - pattern documentation service
     */
    public CodeLabController(CodeDisplayService codeDisplayService,
                            DesignPatternDocService patternDocService) {
        this.codeDisplayService = codeDisplayService;
        this.patternDocService = patternDocService;
    }

    /**
     * Get available code snippets and patterns
     * @return ResponseEntity - available resources
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAvailableResources() {
        return ResponseEntity.ok(Map.of(
            "codeSnippets", codeDisplayService.getAvailableSnippetKeys(),
            "categories", codeDisplayService.getAvailableCategories(),
            "patterns", patternDocService.getAvailablePatternKeys(),
            "patternTypes", patternDocService.getAvailablePatternTypes()
        ));
    }

    /**
     * Get code snippet by key
     * @param key - snippet key
     * @return ResponseEntity - code snippet
     */
    @GetMapping("/snippets/{key}")
    public ResponseEntity<CodeSnippetDto> getCodeSnippet(@PathVariable String key) {
        CodeSnippetDto snippet = codeDisplayService.getCodeSnippet(key);

        if (snippet == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(snippet);
    }

    /**
     * Get all code snippets
     * @return ResponseEntity - all snippets
     */
    @GetMapping("/snippets")
    public ResponseEntity<Map<String, CodeSnippetDto>> getAllCodeSnippets() {
        return ResponseEntity.ok(codeDisplayService.getAllCodeSnippets());
    }

    /**
     * Get code snippets by category
     * @param category - category to filter
     * @return ResponseEntity - matching snippets
     */
    @GetMapping("/snippets/category/{category}")
    public ResponseEntity<List<CodeSnippetDto>> getSnippetsByCategory(@PathVariable String category) {
        List<CodeSnippetDto> snippets = codeDisplayService.getSnippetsByCategory(category);
        return ResponseEntity.ok(snippets);
    }

    /**
     * Get design pattern documentation
     * @param key - pattern key
     * @return ResponseEntity - pattern documentation
     */
    @GetMapping("/patterns/{key}")
    public ResponseEntity<PatternDoc> getPatternDoc(@PathVariable String key) {
        PatternDoc doc = patternDocService.getPatternDoc(key);

        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(doc);
    }

    /**
     * Get all design pattern documentation
     * @return ResponseEntity - all pattern docs
     */
    @GetMapping("/patterns")
    public ResponseEntity<Map<String, PatternDoc>> getAllPatternDocs() {
        return ResponseEntity.ok(patternDocService.getAllPatternDocs());
    }

    /**
     * Get patterns by type
     * @param type - pattern type
     * @return ResponseEntity - matching patterns
     */
    @GetMapping("/patterns/type/{type}")
    public ResponseEntity<List<PatternDoc>> getPatternsByType(@PathVariable String type) {
        List<PatternDoc> patterns = patternDocService.getPatternsByType(type);
        return ResponseEntity.ok(patterns);
    }

    /**
     * Search patterns by keyword
     * @param keyword - search keyword
     * @return ResponseEntity - matching patterns
     */
    @GetMapping("/patterns/search")
    public ResponseEntity<List<PatternDoc>> searchPatterns(@RequestParam String keyword) {
        List<PatternDoc> patterns = patternDocService.searchPatterns(keyword);
        return ResponseEntity.ok(patterns);
    }

    /**
     * Start SQL capture session
     * @return ResponseEntity - confirmation
     */
    @PostMapping("/sql-capture/start")
    public ResponseEntity<Map<String, String>> startSqlCapture() {
        codeDisplayService.startSqlCapture();
        return ResponseEntity.ok(Map.of(
            "status", "started",
            "message", "SQL capture session started. Execute queries and then call /sql-capture/stop to get results."
        ));
    }

    /**
     * Stop SQL capture session and get results
     * @return ResponseEntity - captured SQL queries
     */
    @PostMapping("/sql-capture/stop")
    public ResponseEntity<Map<String, Object>> stopSqlCapture() {
        codeDisplayService.stopSqlCapture();
        List<SqlCaptureDto> captured = codeDisplayService.getCapturedSql();

        return ResponseEntity.ok(Map.of(
            "status", "stopped",
            "queriesCapturde", captured.size(),
            "queries", captured
        ));
    }

    /**
     * Get currently captured SQL queries
     * @return ResponseEntity - captured queries
     */
    @GetMapping("/sql-capture")
    public ResponseEntity<List<SqlCaptureDto>> getCapturedSql() {
        return ResponseEntity.ok(codeDisplayService.getCapturedSql());
    }

    /**
     * Clear captured SQL
     * @return ResponseEntity - confirmation
     */
    @DeleteMapping("/sql-capture")
    public ResponseEntity<Map<String, String>> clearCapturedSql() {
        codeDisplayService.clearCapturedSql();
        return ResponseEntity.ok(Map.of(
            "status", "cleared",
            "message", "Captured SQL queries cleared"
        ));
    }
}
