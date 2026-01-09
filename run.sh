#!/bin/bash
# Get the absolute path of the script directory
PROJECT_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Run Script for Smart Quiz System
# Starts Backend in background, then launches Client

echo "========================================"
echo "   🚀 Smart Quiz System - Launcher"
echo "========================================"
echo "Project Root: $PROJECT_ROOT"

# Function to kill backend on exit
cleanup() {
    echo ""
    echo "🛑 Stopping Services..."
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null
        # Wait a moment
        sleep 1
        kill -9 $BACKEND_PID 2>/dev/null
    fi
    if [ ! -z "$FACULTY_WEB_PID" ]; then
        echo "🛑 Stopping Faculty Portal..."
        kill $FACULTY_WEB_PID 2>/dev/null
        kill -9 $FACULTY_WEB_PID 2>/dev/null
    fi
    
    # Extra safety: kill anything on our ports
    fuser -k 9876/tcp >/dev/null 2>&1
    fuser -k 8080/tcp >/dev/null 2>&1
    
    echo "👋 Goodbye!"
    exit
}

# Trap interrupts AND normal exit (EXIT covers both signal and normal end)
trap cleanup EXIT INT TERM

# 1. Start Backend
echo "⏳ Starting Backend Server..."

# Cleanup Port 8080
EXISTING_BACKEND=$(lsof -t -i:8080)
if [ ! -z "$EXISTING_BACKEND" ]; then
    echo "   ⚠️ Found existing process on port 8080 (PID $EXISTING_BACKEND). Killing it..."
    kill -9 $EXISTING_BACKEND 2>/dev/null
fi

cd "$PROJECT_ROOT/backend"
mvn spring-boot:run -Dspring-boot.run.profiles=dev > "$PROJECT_ROOT/backend.log" 2>&1 &
BACKEND_PID=$!

echo "   Backend running with PID $BACKEND_PID. Logs: backend.log"

# 2. Wait for Backend
echo "   Waiting for server to be ready on port 8080..."
MAX_RETRIES=30
COUNT=0
URL="http://localhost:8080/" 

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

# 3. Start Secret Faculty Portal (Port 9876)

# Kill anything already on port 9876
EXISTING_PID=$(lsof -t -i:9876)
if [ ! -z "$EXISTING_PID" ]; then
    echo "   ⚠️ Found existing process on port 9876 (PID $EXISTING_PID). Killing it..."
    kill -9 $EXISTING_PID 2>/dev/null
fi

if command -v python3 &> /dev/null; then
    # Run in subshell to ensure directory change applies only to the server process
    (cd "$PROJECT_ROOT/faculty_secret_portal" && python3 -m http.server 9876) > /dev/null 2>&1 &
    FACULTY_WEB_PID=$!
    echo "   Faculty Portal running at http://localhost:9876 (PID $FACULTY_WEB_PID)"
else
    echo "   ⚠️  Python3 not found. Faculty Web Portal skipped."
fi

# 4. Start Client
echo "🖥️  Starting Faculty/Student Client..."
cd "$PROJECT_ROOT/client"
mvn javafx:run

# 5. Cleanup when client closes
cleanup
