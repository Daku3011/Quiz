#!/bin/bash

# Run Script for Smart Quiz System
# Starts Backend in background, then launches Client

echo "========================================"
echo "   🚀 Smart Quiz System - Launcher"
echo "========================================"

# Function to kill backend on exit
cleanup() {
    echo ""
    echo "🛑 Stopping Backend Server..."
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID
    fi
    echo "👋 Goodbye!"
    exit
}

# Trap interrupts
trap cleanup SIGINT SIGTERM

# 1. Start Backend
echo "⏳ Starting Backend Server..."
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev > ../backend.log 2>&1 &
BACKEND_PID=$!
cd ..

echo "   Backend running with PID $BACKEND_PID. Logs: backend.log"

# 2. Wait for Backend
echo "   Waiting for server to be ready on port 8080..."
MAX_RETRIES=30
COUNT=0
URL="http://localhost:8080/" # Health check execution (index.html)

while ! curl --output /dev/null --silent --fail "$URL"; do
    printf "."
    sleep 2
    COUNT=$((COUNT+1))
    if [ $COUNT -ge $MAX_RETRIES ]; then
        echo ""
        echo "❌ Backend failed to start in time. Check backend.log."
        cleanup
    fi
done

echo ""
echo "✅ Backend is UP!"

# 3. Start Client
echo "🖥️  Starting Faculty/Student Client..."
cd client
mvn javafx:run
cd ..

# 4. Cleanup when client closes
cleanup
