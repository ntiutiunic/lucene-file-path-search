#!/bin/bash

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper function to format JSON output
format_json() {
  if command -v jq &> /dev/null; then
    echo $1 | jq .
  else
    echo $1
  fi
}

# Test function
test_query() {
  query=$1
  fuzzy=$2
  
  echo -e "\n${BLUE}=== Testing query: $query (fuzzy=$fuzzy) ===${NC}"
  
  # URL encode the query
  encoded_query=$(echo $query | sed 's/ /%20/g' | sed 's/\//%2F/g')
  
  # Make the request
  response=$(curl -s "$BASE_URL/search?query=$encoded_query&fuzzy=$fuzzy")
  
  echo -e "${BLUE}Response:${NC}"
  format_json "$response"
  
  # Check if response has results
  if [ "$response" != "[]" ]; then
    echo -e "${GREEN}✓ Has results${NC}"
  else
    echo -e "\033[0;31m✗ No results${NC}"
  fi
}

echo -e "${BLUE}REGULAR SEARCH TESTS${NC}"
# Original test cases with regular (non-fuzzy) search
test_query "lqdocspg" "false"
test_query "lucene/queryparser/docs/xml/img/plus.gif" "false"
test_query "lqd///gif" "false"
test_query "join.gif" "false"
test_query "minusbottom.gif" "false"

echo -e "\n${BLUE}FUZZY SEARCH TESTS${NC}"
# Fuzzy search tests
test_query "pluss.gif" "true"  # should match plus.gif even with an extra "s"
test_query "miunsbottom.gif" "true"  # should match minusbottom.gif with a typo
test_query "lucen/queryparsr/docs/xml/img/join.gif" "true"  # should match with typos in path
test_query "plos.gif" "true"  # should match plus.gif with two typos

echo -e "\n${BLUE}PATH COMPONENT MATCHING TESTS${NC}"
# Path component matching tests
test_query "lucene/quereyparser/img" "true"  # typo in path component "queryparser"
test_query "docs/xml/gif" "true"  # partial path components
test_query "lucene/queri/docs" "true"  # abbreviation with typo
test_query "docs/minusbottom" "true"  # path component + filename

echo -e "\n${BLUE}=== Test Conclusion ===${NC}"
echo "Requirements Analysis:"
echo "1. Case-insensitive search: Implemented with LowerCaseFilter"
echo "2. Regular search: Uses NGramTokenizer for substring and abbreviation matching"
echo "3. Fuzzy search: Uses FuzzyQuery with maxEdits=2 for typo tolerance"
echo "4. Path component matching: Added fuzzy matching for individual path components"
echo "5. JSON array return: Controller returns ResponseEntity<List<String>> as JSON array" 