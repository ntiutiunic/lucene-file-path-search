# Lucene Search Training Project

This repository contains a comprehensive Lucene search training project with multiple implementations and examples.

## 📁 Project Structure

- **`lucene-search-app/`** - Main Spring Boot application with Lucene file path search
- **`src/`** - Additional source code and examples
- **`.gitignore`** - Comprehensive gitignore for Java/Spring Boot projects

## 🚀 Main Application

The primary application is located in the `lucene-search-app/` directory. It's a Spring Boot application that demonstrates:

- Apache Lucene file path searching
- Custom nGram analyzer implementation
- Fuzzy search capabilities
- REST API with comprehensive testing

### Quick Start

```bash
# Navigate to the main application
cd lucene-search-app

# Set Java environment (if needed)
export JAVA_HOME=/path/to/your/java17
export PATH=$JAVA_HOME/bin:$PATH

# Build and run
chmod +x build-and-run-jar.sh
./build-and-run-jar.sh
```

### Testing

```bash
# Test the search functionality
chmod +x test-search.sh
./test-search.sh

# Test comprehensive API features
chmod +x curl-test.sh
./curl-test.sh

# Test custom filters
chmod +x run-concatenation-test.sh
./run-concatenation-test.sh
```

For detailed documentation, see [lucene-search-app/README.md](lucene-search-app/README.md).

## 🛠️ Requirements

- Java 17 or higher
- Maven 3.6+
- curl (for testing)
