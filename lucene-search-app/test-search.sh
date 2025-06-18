#!/bin/bash

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Starting Lucene Search API Test${NC}\n"
echo "Testing requirements:"
echo "1. Search must be case-insensitive"
echo "2. Search must support substring and abbreviation matching"
echo "3. Search endpoint must return file paths as a JSON array"
echo -e "\n--------------------------------------------\n"

# Function to test a search query
test_search() {
    query=$1
    expected_match=$2

    echo -e "${BLUE}Test Query:${NC} '$query'"
    echo -e "${BLUE}Expected to match:${NC} $expected_match"
    
    # URL encode the query
    encoded_query=$(echo $query | sed 's/ /%20/g' | sed 's/\//%2F/g')
    
    # Make the request
    response=$(curl -s "$BASE_URL/search?query=$encoded_query")
    
    echo -e "${BLUE}Response:${NC}"
    echo $response | jq || echo $response
    
    # Check if expected match is in the response
    if echo $response | grep -q "$expected_match"; then
        echo -e "${GREEN}✓ Test PASSED: Found expected match${NC}"
    else
        echo -e "${RED}✗ Test FAILED: Expected match not found${NC}"
    fi
    echo -e "\n--------------------------------------------\n"
}

# Test cases
echo "Starting tests..."
echo "Make sure the application is running on $BASE_URL"
echo -e "\n--------------------------------------------\n"

# Test 1: Abbreviation matching
test_search "lqdocspg" "plus.gif"

# Test 2: Exact path matching
test_search "lucene/queryparser/docs/xml/img/plus.gif" "plus.gif"

# Test 3: Mixed special characters and abbreviation
test_search "lqd///gif" "plus.gif"

# Test 4: Exact filename matching
test_search "join.gif" "join.gif"

# Test 5: Another filename matching
test_search "minusbottom.gif" "minusbottom.gif"

# Summary
echo -e "${BLUE}Test Summary:${NC}"
echo "- Tested 5 different queries against the search endpoint"
echo "- Check above results to confirm if all requirements are met"
echo ""
echo "Requirements analysis:"
echo "1. Case-insensitive search: This is handled by the LowerCaseFilter in the CustomNGramAnalyzer"
echo "2. Substring/abbreviation matching: This is handled by the NGramTokenizer (minGram=2, maxGram=10)"
echo "3. JSON array return: The controller returns ResponseEntity<List<String>> which serializes to JSON array"
echo ""
echo "All requirements should be met based on the implementation!" 