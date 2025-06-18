package com.example.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

/**
 * A custom analyzer that:
 * 1. Splits text on whitespace using WhitespaceTokenizer
 * 2. Removes stop words using StopFilter
 * 3. Concatenates all remaining tokens into a single token using ConcatenationTokenFilter
 */
public class ConcatenationAnalyzer extends Analyzer {
    private final CharArraySet stopWords;
    private final String delimiter;
    
    /**
     * Create a new ConcatenationAnalyzer with custom stop words and delimiter.
     * 
     * @param stopWords CharArraySet of stop words to filter out
     * @param delimiter Delimiter to use between tokens when concatenating
     */
    public ConcatenationAnalyzer(CharArraySet stopWords, String delimiter) {
        this.stopWords = stopWords;
        this.delimiter = delimiter;
    }
    
    /**
     * Create a new ConcatenationAnalyzer with default English stop words and space delimiter.
     */
    public ConcatenationAnalyzer() {
        this(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET, " ");
    }
    
    /**
     * Create a new ConcatenationAnalyzer with custom stop words and default space delimiter.
     * 
     * @param stopWords CharArraySet of stop words to filter out
     */
    public ConcatenationAnalyzer(CharArraySet stopWords) {
        this(stopWords, " ");
    }
    
    /**
     * Create a new ConcatenationAnalyzer with default English stop words and custom delimiter.
     * 
     * @param delimiter Delimiter to use between tokens when concatenating
     */
    public ConcatenationAnalyzer(String delimiter) {
        this(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET, delimiter);
    }
    
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // 1. Use WhitespaceTokenizer to split text on whitespace
        Tokenizer tokenizer = new WhitespaceTokenizer();
        
        // 2. Apply StopFilter to remove stop words
        TokenStream tokenStream = new StopFilter(tokenizer, stopWords);
        
        // 3. Apply ConcatenationTokenFilter to join remaining tokens
        tokenStream = new ConcatenationTokenFilter(tokenStream, delimiter);
        
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
} 