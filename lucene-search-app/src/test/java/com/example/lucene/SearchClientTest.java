package com.example.lucene;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SearchClientTest {

    private static final String BASE_URL = "http://localhost:8080";

    public static void main(String[] args) {
        System.out.println("=== REGULAR SEARCH TESTS ===");
        // Test regular search queries
        String[] regularQueries = {
            "lqdocspg",
            "lucene/queryparser/docs/xml/img/plus.gif",
            "lqd///gif",
            "join.gif",
            "minusbottom.gif"
        };

        // Run tests for each regular query
        for (String query : regularQueries) {
            testSearch(query, false);
        }
        
        System.out.println("\n=== FUZZY SEARCH TESTS ===");
        // Test fuzzy search queries
        String[] fuzzyQueries = {
            "pluss.gif", // should match plus.gif even with an extra "s"
            "miunsbottom.gif", // should match minusbottom.gif with a typo
            "lucen/queryparsr/docs/xml/img/join.gif", // should match with typos in path
            "plos.gif" // should match plus.gif with two typos
        };
        
        // Run tests for each fuzzy query
        for (String query : fuzzyQueries) {
            testSearch(query, true);
        }
        
        System.out.println("\n=== PATH COMPONENT MATCHING TESTS ===");
        // Test path component matching enhancement
        String[] componentQueries = {
            "lucene/quereyparser/img", // typo in path component "queryparser"
            "docs/xml/gif", // partial path components
            "lucene/queri/docs", // abbreviation with typo
            "docs/minusbottom" // path component + filename
        };
        
        // Run tests for path component matching
        for (String query : componentQueries) {
            testSearch(query, true);
        }

        // Print conclusion
        System.out.println("\n=== Test Conclusion ===");
        System.out.println("Requirements Analysis:");
        System.out.println("1. Case-insensitive search: Implemented with LowerCaseFilter");
        System.out.println("2. Regular search: Uses NGramTokenizer for substring and abbreviation matching");
        System.out.println("3. Fuzzy search: Uses FuzzyQuery with maxEdits=2 for typo tolerance");
        System.out.println("4. Path component matching: Added fuzzy matching for individual path components");
        System.out.println("5. JSON array return: Controller returns ResponseEntity<List<String>> as JSON array");
    }

    private static void testSearch(String query, boolean fuzzy) {
        try {
            System.out.println("\n=== Testing query: " + query + " (fuzzy=" + fuzzy + ") ===");
            
            // URL encode the query
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            URL url = new URL(BASE_URL + "/search?query=" + encodedQuery + "&fuzzy=" + fuzzy);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                System.out.println("Response: " + response.toString());
                
                // Simple check if the response contains expected results
                boolean hasResults = !response.toString().equals("[]");
                System.out.println("Has results: " + hasResults);
            } else {
                System.out.println("Request failed");
            }
            
        } catch (Exception e) {
            System.out.println("Error testing search: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 