package com.example.lucene;

import com.example.lucene.analyzer.ConcatenationAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Runnable class to validate the ConcatenationTokenFilter and ConcatenationAnalyzer.
 * This class demonstrates the functionality with direct output to console.
 */
public class ConcatenationFilterRunner {

    public static void main(String[] args) {
        System.out.println("Starting ConcatenationTokenFilter validation");
        System.out.println("===========================================");
        
        try {
            // Test default analyzer (space delimiter)
            testAnalyzer(new ConcatenationAnalyzer(), 
                         "this is a simple test of the filter",
                         "Test with default delimiter (space)");
            
            // Test with custom delimiter
            testAnalyzer(new ConcatenationAnalyzer("-"), 
                         "this is a simple test of the filter",
                         "Test with custom delimiter (dash)");
            
            // Test with only stop words
            testAnalyzer(new ConcatenationAnalyzer(), 
                         "this is a the",
                         "Test with only stop words");
            
            // Test with no stop words
            testAnalyzer(new ConcatenationAnalyzer(), 
                         "simple direct exact match",
                         "Test with no stop words");
            
            // Test with mixed case
            testAnalyzer(new ConcatenationAnalyzer(), 
                         "This IS a SIMPLE test OF the FILTER",
                         "Test with mixed case");
            
            // Test with special characters
            testAnalyzer(new ConcatenationAnalyzer(), 
                         "special! characters? in-between normal words",
                         "Test with special characters");
            
            System.out.println("\nAll tests completed successfully!");
            
        } catch (IOException e) {
            System.err.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test an analyzer with given input text
     * 
     * @param analyzer The analyzer to test
     * @param inputText The input text to analyze
     * @param testName Description of the test
     */
    private static void testAnalyzer(Analyzer analyzer, String inputText, String testName) throws IOException {
        System.out.println("\n" + testName);
        System.out.println("Input: \"" + inputText + "\"");
        
        List<String> tokens = analyzeText(analyzer, inputText);
        
        System.out.println("Number of tokens: " + tokens.size());
        
        if (tokens.isEmpty()) {
            System.out.println("Output: [no tokens]");
        } else {
            for (int i = 0; i < tokens.size(); i++) {
                System.out.println("Token " + (i+1) + ": \"" + tokens.get(i) + "\"");
            }
        }
    }
    
    /**
     * Analyze text with given analyzer and return list of tokens
     */
    private static List<String> analyzeText(Analyzer analyzer, String text) throws IOException {
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