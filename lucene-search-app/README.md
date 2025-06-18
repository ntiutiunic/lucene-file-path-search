# Lucene File Path Search Engine

A powerful Spring Boot application that demonstrates Apache Lucene file path searching with custom nGram analyzer and fuzzy search capabilities. This project implements intelligent file path searching similar to GitHub's file finder with support for case-insensitive matching, substring search, abbreviation matching, and fuzzy search for typo tolerance.

## 🚀 Features

- **Spring Boot 3** with Java 17+
- **Apache Lucene 9.7.0** integration for intelligent file path searching
- **Custom nGram analyzer** (minGram=2, maxGram=10) with lowercase filter
- **Fuzzy search** with typo tolerance (maxEdits=2)
- **Case-insensitive** substring and abbreviation matching
- **REST API** endpoint for searching file paths
- **CORS support** for cross-origin requests
- **Special character handling** for safe searching
- **Comprehensive test suite** with shell scripts

## 📋 Requirements

- **Java 17** or higher
- **Maven 3.6+**
- **curl** (for testing)

## 🛠️ Quick Start

### 1. Clone and Navigate

```bash
git clone <your-repo-url>
cd lucene-search-app
```

### 2. Set Java Environment (if needed)

```bash
# For macOS with multiple Java versions
export JAVA_HOME=/path/to/your/java17
export PATH=$JAVA_HOME/bin:$PATH

# Verify Java version
java -version
```

### 3. Build and Run the Application

#### Option A: Using the provided script (Recommended)
```bash
# Make script executable
chmod +x build-and-run-jar.sh

# Build and run
./build-and-run-jar.sh
```

#### Option B: Manual Maven commands
```bash
# Build the project
mvn clean package

# Run the application
java -jar target/lucene-search-app-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## 🧪 Testing

The project includes several shell scripts for comprehensive testing. Make sure the application is running before executing tests.

### 1. Test Concatenation Filter

Tests the custom ConcatenationTokenFilter with various scenarios:

```bash
# Make script executable
chmod +x run-concatenation-test.sh

# Run the test
./run-concatenation-test.sh
```

**What it tests:**
- Default delimiter (space) processing
- Custom delimiter (dash) processing
- Stop word filtering
- Mixed case handling
- Special character handling

### 2. Test Search API (Comprehensive)

Tests the REST API with various search scenarios:

```bash
# Make script executable
chmod +x curl-test.sh

# Run the test
./curl-test.sh
```

**What it tests:**
- Regular search queries
- Fuzzy search with typos
- Path component matching
- Abbreviation matching
- Case-insensitive search

### 3. Test Basic Requirements

Tests the core requirements of the search functionality:

```bash
# Make script executable
chmod +x test-search.sh

# Run the test
./test-search.sh
```

**What it tests:**
- Case-insensitive search
- Substring and abbreviation matching
- JSON array response format

## 🔍 API Usage

### Search Endpoint

```
GET /search?query={searchTerm}&fuzzy={true|false}
```

**Parameters:**
- `query` (required): Search term
- `fuzzy` (optional): Enable fuzzy search (default: false)

### Example Requests

```bash
# Basic search
curl "http://localhost:8080/search?query=plus"

# Fuzzy search with typos
curl "http://localhost:8080/search?query=pluss.gif&fuzzy=true"

# Abbreviation search
curl "http://localhost:8080/search?query=lqdocspg"

# Path component search
curl "http://localhost:8080/search?query=docs/xml/gif&fuzzy=true"
```

### Example Response

```json
[
  "lucene/queryparser/docs/xml/img/plus.gif",
  "lucene/queryparser/docs/xml/img/join.gif",
  "lucene/queryparser/docs/xml/img/minusbottom.gif"
]
```

## 📁 Indexed Data

The application indexes the following file paths on startup:
- `lucene/queryparser/docs/xml/img/plus.gif`
- `lucene/queryparser/docs/xml/img/join.gif`
- `lucene/queryparser/docs/xml/img/minusbottom.gif`

## 🔧 Implementation Details

### Search Capabilities

- **Case-insensitive matching**: Implemented with LowerCaseFilter
- **Substring matching**: Uses NGramTokenizer (minGram=2, maxGram=10)
- **Abbreviation matching**: Same NGramTokenizer handles abbreviations
- **Fuzzy search**: Uses FuzzyQuery with maxEdits=2 for typo tolerance
- **Path component matching**: Fuzzy matching for individual path components

### Technical Stack

- **Spring Boot 3.1.0**: Web framework
- **Apache Lucene 9.7.0**: Search engine
- **ByteBuffersDirectory**: In-memory Lucene index
- **Custom nGram analyzer**: For intelligent tokenization
- **FuzzyQuery**: For typo-tolerant search

## 🏗️ Project Structure

```
lucene-search-app/
├── src/
│   ├── main/java/com/example/lucene/
│   │   ├── LuceneSearchApplication.java    # Main Spring Boot app
│   │   ├── controller/
│   │   │   └── SearchController.java       # REST API controller
│   │   ├── service/
│   │   │   └── LuceneService.java          # Search service
│   │   ├── analyzer/
│   │   │   └── CustomNGramAnalyzer.java    # Custom analyzer
│   │   └── client/
│   │       └── SearchClient.java           # HTTP client
│   └── test/java/com/example/lucene/
│       └── analyzer/
│           └── ConcatenationAnalyzerTest.java
├── build-and-run-jar.sh                    # Build and run script
├── run-concatenation-test.sh               # Filter test script
├── curl-test.sh                           # API test script
├── test-search.sh                         # Requirements test script
└── README.md
```

## 🚀 Development

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ConcatenationAnalyzerTest
```

### Building for Production

```bash
# Create executable JAR
mvn clean package

# The JAR will be in target/lucene-search-app-0.0.1-SNAPSHOT.jar
```
