package com.example.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcatenationAnalyzerTest {
    
    private Analyzer analyzer;
    
    @BeforeEach
    public void setUp() {
        analyzer = new ConcatenationAnalyzer();
    }
    
    @AfterEach
    public void tearDown() {
        if (analyzer != null) {
            analyzer.close();
        }
    }
    
    /**
     * Test that the analyzer correctly removes stop words and produces a single token.
     */
    @Test
    public void testStopWordRemovalAndConcatenation() throws IOException {
        // Input text with stop words
        String text = "this is a simple test of the filter";
        
        // The expected output (stop words removed, tokens concatenated)
        String expected = "simple test filter";
        
        // Get tokens from the analyzer
        List<String> tokens = analyzeText(analyzer, text);
        
        // Verify that we have exactly one token
        assertEquals(1, tokens.size(), "The analyzer should produce exactly one token");
        
        // Verify that the token matches our expected output
        assertEquals(expected, tokens.get(0), "The token should match the expected concatenated text");
    }
    
    /**
     * Test that the analyzer works with custom delimiter
     */
    @Test
    public void testCustomDelimiter() throws IOException {
        // Create analyzer with custom delimiter
        Analyzer customAnalyzer = new ConcatenationAnalyzer("-");
        
        try {
            // Input text with stop words
            String text = "this is a simple test of the filter";
            
            // The expected output (stop words removed, tokens concatenated with dash)
            String expected = "simple-test-filter";
            
            // Get tokens from the analyzer
            List<String> tokens = analyzeText(customAnalyzer, text);
            
            // Verify that we have exactly one token
            assertEquals(1, tokens.size(), "The analyzer should produce exactly one token");
            
            // Verify that the token matches our expected output
            assertEquals(expected, tokens.get(0), "The token should match the expected concatenated text with custom delimiter");
        } finally {
            customAnalyzer.close();
        }
    }
    
    /**
     * Test that the analyzer handles empty input correctly
     */
    @Test
    public void testEmptyInput() throws IOException {
        // Input text that is empty
        String text = "";
        
        // Get tokens from the analyzer
        List<String> tokens = analyzeText(analyzer, text);
        
        // Verify that we have no tokens
        assertTrue(tokens.isEmpty(), "The analyzer should produce no tokens for empty input");
    }
    
    /**
     * Test that the analyzer handles input with only stop words correctly
     */
    @Test
    public void testOnlyStopWords() throws IOException {
        // Input text with only stop words
        String text = "this is a the";
        
        // Get tokens from the analyzer
        List<String> tokens = analyzeText(analyzer, text);
        
        // Verify that we have no tokens (all words are stop words)
        assertTrue(tokens.isEmpty(), "The analyzer should produce no tokens when all words are stop words");
    }
    
    /**
     * Helper method to get all tokens from an analyzer for a given text
     */
    private List<String> analyzeText(Analyzer analyzer, String text) throws IOException {
        List<String> result = new ArrayList<>();
        TokenStream tokenStream = analyzer.tokenStream("content", text);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            result.add(charTermAttribute.toString());
        }
        tokenStream.end();
        tokenStream.close();
        
        return result;
    }
} 