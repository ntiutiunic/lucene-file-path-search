package com.example.lucene.service;

import com.example.lucene.analyzer.CustomNGramAnalyzer;
import jakarta.annotation.PostConstruct;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LuceneService {
    private static final Logger logger = LoggerFactory.getLogger(LuceneService.class);
    
    private static final String PATH_FIELD = "path";
    private static final String CONTENT_FIELD = "content";
    private static final String FILENAME_FIELD = "filename";
    
    private final List<String> filePaths = Arrays.asList(
            "lucene/queryparser/docs/xml/img/plus.gif",
            "lucene/queryparser/docs/xml/img/join.gif",
            "lucene/queryparser/docs/xml/img/minusbottom.gif"
    );
    
    private Directory directory;
    private Analyzer ngramAnalyzer;
    private Analyzer standardAnalyzer;
    private IndexWriter indexWriter;
    
    public LuceneService() {
        // Initialize in-memory directory using ByteBuffersDirectory (non-deprecated)
        this.directory = new ByteBuffersDirectory();
        
        // Create analyzer with nGram (min=2, max=10) and lowercase filter
        this.ngramAnalyzer = new CustomNGramAnalyzer(2, 10);
        
        // Standard analyzer for fuzzy search
        this.standardAnalyzer = new StandardAnalyzer();
    }
    
    @PostConstruct
    public void init() throws IOException {
        createIndex();
    }
    
    private void createIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(ngramAnalyzer);
        indexWriter = new IndexWriter(directory, config);
        
        // Clear any existing documents
        indexWriter.deleteAll();
        
        // Add documents for each file path
        for (String filePath : filePaths) {
            Document document = new Document();
            
            // Store the path as a string field (stored but not tokenized)
            document.add(new StringField(PATH_FIELD, filePath, Field.Store.YES));
            
            // Add the path also as a text field for searching (tokenized)
            document.add(new TextField(CONTENT_FIELD, filePath, Field.Store.NO));
            
            // Extract and store filename separately for fuzzy search
            String filename = extractFilename(filePath);
            document.add(new TextField(FILENAME_FIELD, filename, Field.Store.NO));
            
            indexWriter.addDocument(document);
        }
        
        indexWriter.commit();
        indexWriter.close();
        
        logger.info("Indexed {} file paths", filePaths.size());
    }
    
    private String extractFilename(String path) {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < path.length() - 1) {
            return path.substring(lastSlash + 1);
        }
        return path;
    }
    
    /**
     * Search for file paths matching the query.
     * 
     * @param queryText The search query text
     * @param fuzzy Whether to use fuzzy search (true) or ngram search (false)
     * @return List of matching file paths
     */
    public List<String> search(String queryText, boolean fuzzy) throws IOException, ParseException {
        if (queryText == null || queryText.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        if (fuzzy) {
            return fuzzySearch(queryText);
        } else {
            return ngramSearch(queryText);
        }
    }
    
    /**
     * Original ngram-based search implementation
     */
    private List<String> ngramSearch(String queryText) throws IOException, ParseException {
        // Escape special characters to avoid query syntax errors
        queryText = QueryParser.escape(queryText);
        
        // Create query parser
        QueryParser queryParser = new QueryParser(CONTENT_FIELD, ngramAnalyzer);
        Query query = queryParser.parse(queryText);
        
        // Perform search
        return executeSearch(query);
    }
    
    /**
     * Fuzzy search implementation with maxEdits=2
     */
    private List<String> fuzzySearch(String queryText) throws IOException {
        // Convert to lowercase for case-insensitive search
        queryText = queryText.toLowerCase();
        
        // Create a boolean query to combine multiple fuzzy queries
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        
        // Try to match the full path with fuzzy query
        Term pathTerm = new Term(CONTENT_FIELD, queryText);
        FuzzyQuery pathFuzzyQuery = new FuzzyQuery(pathTerm, 2); // maxEdits=2
        queryBuilder.add(pathFuzzyQuery, BooleanClause.Occur.SHOULD);
        
        // Try to extract and match the filename part if there's a slash
        if (queryText.contains("/")) {
            // Extract and match the filename
            String filename = extractFilename(queryText);
            Term filenameTerm = new Term(FILENAME_FIELD, filename);
            FuzzyQuery filenameFuzzyQuery = new FuzzyQuery(filenameTerm, 2); // maxEdits=2
            queryBuilder.add(filenameFuzzyQuery, BooleanClause.Occur.SHOULD);
            
            // Break path into components and add fuzzy queries for each component
            String[] pathComponents = queryText.split("/");
            for (String component : pathComponents) {
                if (!component.isEmpty()) {
                    Term componentTerm = new Term(CONTENT_FIELD, component);
                    FuzzyQuery componentQuery = new FuzzyQuery(componentTerm, 2); // maxEdits=2
                    queryBuilder.add(componentQuery, BooleanClause.Occur.SHOULD);
                    
                    // Also try to match components against filenames 
                    // (in case a path component is similar to a filename)
                    Term filenameComponentTerm = new Term(FILENAME_FIELD, component);
                    FuzzyQuery filenameComponentQuery = new FuzzyQuery(filenameComponentTerm, 2);
                    queryBuilder.add(filenameComponentQuery, BooleanClause.Occur.SHOULD);
                }
            }
        } else {
            // If it's just a filename without path, search in both fields
            Term filenameTerm = new Term(FILENAME_FIELD, queryText);
            FuzzyQuery filenameFuzzyQuery = new FuzzyQuery(filenameTerm, 2); // maxEdits=2
            queryBuilder.add(filenameFuzzyQuery, BooleanClause.Occur.SHOULD);
        }
        
        // Perform search
        return executeSearch(queryBuilder.build());
    }
    
    /**
     * Execute a search query and collect results
     */
    private List<String> executeSearch(Query query) throws IOException {
        // Create index reader and searcher
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
        // Perform search
        TopDocs topDocs = indexSearcher.search(query, 10);
        
        // Collect results
        List<String> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            results.add(document.get(PATH_FIELD));
        }
        
        indexReader.close();
        
        return results;
    }
    
    /**
     * Legacy method for backward compatibility
     */
    public List<String> search(String queryText) throws IOException, ParseException {
        return search(queryText, false);
    }
} 