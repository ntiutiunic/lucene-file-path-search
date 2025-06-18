#!/bin/bash

echo "Building the JAR file with Maven..."
echo "=================================="

# Change to project directory (if running from elsewhere)
cd "$(dirname "$0")"

# Clean and package the project
mvn clean package

# Check if JAR was created successfully
if [ $? -eq 0 ]; then
    echo -e "\nJAR file created successfully!"
    JAR_FILE="target/lucene-search-app-0.0.1-SNAPSHOT.jar"
    
    echo -e "\nRunning the JAR file..."
    echo "======================="
    java -jar $JAR_FILE
else
    echo -e "\nError building the JAR file. See Maven output for details."
    exit 1
fi 