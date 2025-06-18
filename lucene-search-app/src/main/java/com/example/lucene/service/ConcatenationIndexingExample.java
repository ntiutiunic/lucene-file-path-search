package com.example.lucene.service;

import com.example.lucene.analyzer.ConcatenationAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example class showing how to use ConcatenationAnalyzer for indexing and searching.
 * This demonstrates a practical use case of the ConcatenationTokenFilter.
 */
@Component
public class ConcatenationIndexingExample {
    
    private static final String ID_FIELD = "id";
    private static final String TITLE_FIELD = "title";
    private static final String CONTENT_FIELD = "content";
    private static final String CONCATENATED_FIELD = "concatenated";
    
    private final Directory directory;
    private final Analyzer concatenationAnalyzer;
    
    public ConcatenationIndexingExample() {
        this.directory = new ByteBuffersDirectory();
        this.concatenationAnalyzer = new ConcatenationAnalyzer();
    }
    
    /**
     * Creates a sample index with documents that have both regular fields
     * and a concatenated field that combines all content with stop words removed.
     */
    public void createSampleIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(concatenationAnalyzer);
        IndexWriter writer = new IndexWriter(directory, config);
        
        // Clear any existing documents
        writer.deleteAll();
        
        // Add some sample documents
        addDocument(writer, "1", "Introduction to Lucene", 
                "Lucene is a powerful search library for Java applications.");
        
        addDocument(writer, "2", "Advanced Lucene Indexing", 
                "This article explains how to use custom analyzers and filters in Lucene.");
        
        addDocument(writer, "3", "Searching with Lucene", 
                "Learn how to perform efficient searches using Lucene's query parser.");
        
        writer.commit();
        writer.close();
    }
    
    /**
     * Helper method to add a document with both regular and concatenated fields
     */
    private void addDocument(IndexWriter writer, String id, String title, String content) throws IOException {
        Document doc = new Document();
        
        // Add regular fields
        doc.add(new StringField(ID_FIELD, id, Field.Store.YES));
        doc.add(new TextField(TITLE_FIELD, title, Field.Store.YES));
        doc.add(new TextField(CONTENT_FIELD, content, Field.Store.YES));
        
        // Add the concatenated field
        // This field contains all content from title and content fields,
        // but with stop words removed and all tokens concatenated into one term
        String combinedText = title + " " + content;
        doc.add(new TextField(CONCATENATED_FIELD, combinedText, Field.Store.YES));
        
        writer.addDocument(doc);
    }
    
    /**
     * Search for documents matching the given query text in the concatenated field
     */
    public List<DocumentResult> searchConcatenated(String queryText) throws IOException, ParseException {
        if (queryText == null || queryText.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Create a reader
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        
        // Create query parser using the concatenation analyzer
        QueryParser parser = new QueryParser(CONCATENATED_FIELD, concatenationAnalyzer);
        Query query = parser.parse(queryText);
        
        // Perform search
        TopDocs results = searcher.search(query, 10);
        
        // Collect results
        List<DocumentResult> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            documents.add(new DocumentResult(
                    doc.get(ID_FIELD),
                    doc.get(TITLE_FIELD),
                    doc.get(CONTENT_FIELD),
                    doc.get(CONCATENATED_FIELD),
                    scoreDoc.score
            ));
        }
        
        reader.close();
        return documents;
    }
    
    /**
     * Simple class to represent a document with its score
     */
    public static class DocumentResult {
        private final String id;
        private final String title;
        private final String content;
        private final String concatenated;
        private final float score;
        
        public DocumentResult(String id, String title, String content, String concatenated, float score) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.concatenated = concatenated;
            this.score = score;
        }
        
        @Override
        public String toString() {
            return String.format("Document[id=%s, title=%s, score=%.2f]", id, title, score);
        }
        
        // Getters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getConcatenated() { return concatenated; }
        public float getScore() { return score; }
    }
} 