package com.example.lucene.analyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A TokenFilter that concatenates all tokens from the input TokenStream
 * into a single token using a configurable delimiter.
 * 
 * The class is final as required by Lucene's TokenStream contract.
 */
public final class ConcatenationTokenFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private boolean concatenated = false;
    private final String delimiter;
    
    /**
     * Create a new ConcatenationTokenFilter.
     * 
     * @param input     The input TokenStream
     * @param delimiter The delimiter to use between tokens (default: " ")
     */
    public ConcatenationTokenFilter(TokenStream input, String delimiter) {
        super(input);
        this.delimiter = delimiter != null ? delimiter : " ";
    }
    
    /**
     * Create a new ConcatenationTokenFilter with default delimiter (" ").
     * 
     * @param input The input TokenStream
     */
    public ConcatenationTokenFilter(TokenStream input) {
        this(input, " ");
    }
    
    @Override
    public final boolean incrementToken() throws IOException {
        // If we've already produced our concatenated token, we're done
        if (concatenated) {
            return false;
        }
        
        // Collect all tokens from the input
        List<String> tokens = new ArrayList<>();
        while (input.incrementToken()) {
            tokens.add(termAtt.toString());
        }
        
        // If no tokens, we're done
        if (tokens.isEmpty()) {
            return false;
        }
        
        // Clear attributes for our new token
        clearAttributes();
        
        // Join all tokens with the delimiter
        String concatenatedText = String.join(delimiter, tokens);
        
        // Set the term attribute with our concatenated text
        termAtt.setEmpty().append(concatenatedText);
        
        // Set position increment to 1 (this is the first token in a new position)
        posIncrAtt.setPositionIncrement(1);
        
        // Mark that we've produced our single token
        concatenated = true;
        
        return true;
    }
    
    @Override
    public void reset() throws IOException {
        super.reset();
        concatenated = false;
    }
} 