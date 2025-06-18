package com.example.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;

public class CustomNGramAnalyzer extends Analyzer {
    private final int minGram;
    private final int maxGram;

    public CustomNGramAnalyzer(int minGram, int maxGram) {
        this.minGram = minGram;
        this.maxGram = maxGram;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // Create an NGram tokenizer with the specified min and max gram sizes
        Tokenizer tokenizer = new NGramTokenizer(minGram, maxGram);
        
        // Add a lowercase filter to make the search case-insensitive
        TokenStream tokenStream = new LowerCaseFilter(tokenizer);
        
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
} 