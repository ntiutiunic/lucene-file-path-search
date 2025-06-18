package com.example.lucene.controller;

import com.example.lucene.service.LuceneService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    
    private final LuceneService luceneService;
    
    @Autowired
    public SearchController(LuceneService luceneService) {
        this.luceneService = luceneService;
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<String>> search(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "false") boolean fuzzy) {
        try {
            logger.info("Searching for: {}, fuzzy: {}", query, fuzzy);
            List<String> results = luceneService.search(query, fuzzy);
            return ResponseEntity.ok(results);
        } catch (IOException | ParseException e) {
            logger.error("Error searching for '{}': {}", query, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
} 