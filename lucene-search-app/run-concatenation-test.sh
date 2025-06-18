#!/bin/bash

echo "Building and running ConcatenationFilterRunner..."
echo "==============================================="

# Change to project directory (if running from elsewhere)
cd "$(dirname "$0")"

# Compile project
echo "Compiling project with Maven..."
mvn clean compile

# Run the test runner class
echo -e "\nRunning ConcatenationFilterRunner..."
mvn exec:java -Dexec.mainClass="com.example.lucene.ConcatenationFilterRunner"

# Also run the JUnit tests to verify all test cases
echo -e "\n\nRunning JUnit tests for ConcatenationAnalyzer..."
mvn test -Dtest=ConcatenationAnalyzerTest 