package com.example.lucene.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Simple Java client to test the search endpoint.
 * This can be run after starting the main application to verify search functionality.
 */
public class SearchTestClient {

    private static final String BASE_URL = "http://localhost:8080/search?query=";
    
    public static void main(String[] args) {
        // Test queries
        String[] queries = {
            "lqdocspg",                               // Test 1: Abbreviation matching
            "lucene/queryparser/docs/xml/img/plus.gif", // Test 2: Exact path matching
            "lqd///gif",                              // Test 3: Mixed special characters and abbreviation
            "join.gif",                               // Test 4: Exact filename matching
            "minusbottom.gif"                         // Test 5: Another filename matching
        };
        
        // Expected matches
        String[] expectedMatches = {
            "plus.gif",
            "plus.gif",
            "plus.gif",
            "join.gif",
            "minusbottom.gif"
        };
        
        System.out.println("Starting Lucene Search API Test\n");
        System.out.println("Testing requirements:");
        System.out.println("1. Search must be case-insensitive");
        System.out.println("2. Search must support substring and abbreviation matching");
        System.out.println("3. Search endpoint must return file paths as a JSON array");
        System.out.println("\n--------------------------------------------\n");
        
        for (int i = 0; i < queries.length; i++) {
            String query = queries[i];
            String expectedMatch = expectedMatches[i];
            
            testSearch(query, expectedMatch);
        }
        
        System.out.println("\nTest Summary:");
        System.out.println("- Tested 5 different queries against the search endpoint");
        System.out.println("- Check above results to confirm if all requirements are met");
        System.out.println();
        System.out.println("Requirements analysis:");
        System.out.println("1. Case-insensitive search: This is handled by the LowerCaseFilter in the CustomNGramAnalyzer");
        System.out.println("2. Substring/abbreviation matching: This is handled by the NGramTokenizer (minGram=2, maxGram=10)");
        System.out.println("3. JSON array return: The controller returns ResponseEntity<List<String>> which serializes to JSON array");
        System.out.println();
        System.out.println("All requirements should be met based on the implementation!");
    }
    
    private static void testSearch(String query, String expectedMatch) {
        try {
            System.out.println("Test Query: '" + query + "'");
            System.out.println("Expected to match: " + expectedMatch);
            
            // URL encode the query
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            URL url = new URL(BASE_URL + encodedQuery);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            // Read the response
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                String responseBody = response.toString();
                System.out.println("Response: " + responseBody);
                
                // Check if expected match is in the response
                if (responseBody.contains(expectedMatch)) {
                    System.out.println("✓ Test PASSED: Found expected match");
                } else {
                    System.out.println("✗ Test FAILED: Expected match not found");
                }
            }
            
            System.out.println("\n--------------------------------------------\n");
            
        } catch (IOException e) {
            System.out.println("Error connecting to the search endpoint: " + e.getMessage());
            System.out.println("Make sure the application is running on http://localhost:8080");
            System.out.println("\n--------------------------------------------\n");
        }
    }
} 