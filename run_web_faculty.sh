#!/bin/bash
# Checks if python3 is installed
if ! command -v python3 &> /dev/null
then
    echo "Python3 could not be found, attempting to use python..."
    if ! command -v python &> /dev/null
    then
        echo "Python not found. Please install Python to run the web server."
        exit 1
    fi
fi

echo "=================================================="
echo "   STARTING SECRET FACULTY PORTAL"
echo "=================================================="
echo "Port: 9876"
echo "URL: http://localhost:9876"
echo "Mode: Undetectable (Standalone Server)"
echo "=================================================="

# Run HTTP server serving the faculty_secret_portal directory
cd faculty_secret_portal
python3 -m http.server 9876
