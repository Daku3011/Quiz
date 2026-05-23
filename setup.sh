#!/bin/bash

# Setup Script for Smart Quiz System
# Checks dependencies and builds the project

echo "========================================"
echo "   🎓 Smart Quiz System - Setup"
echo "========================================"

# 1. Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed."
    echo "   Please install JDK 17+ and try again."
    exit 1
fi
echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

# 2. Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed."
    echo "   Please install Maven and try again."
    exit 1
fi
echo "✅ Maven found: $(mvn -version | head -n 1)"

# 3. Check/Create .env
ENV_FILE="backend/.env"
if [ ! -f "$ENV_FILE" ]; then
    echo "⚠️  .env file not found in backend/. Creating template..."
    cat > "$ENV_FILE" << 'EOF'
GEMINI_API_KEY=replace_with_your_key
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
EOF
    echo "✅ Created backend/.env. Please edit it with your API Key & email credentials!"
else
    echo "✅ backend/.env exists."
fi

# 4. Build Backend
echo ""
echo "📦 Building Backend..."
cd backend
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Backend build failed."
    exit 1
fi
cd ..
echo "✅ Backend built successfully."

# 5. Build Client
echo ""
echo "📦 Building Client..."
cd client
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Client build failed."
    exit 1
fi
cd ..
echo "✅ Client built successfully."

# 6. Install Student Portal JS test dependencies
echo ""
echo "📦 Installing Student Portal JS test dependencies..."
cd student_portal
npm install
if [ $? -ne 0 ]; then
    echo "⚠️  npm install failed — JS tests won't run until fixed."
fi
cd ..
echo "✅ Student Portal JS dependencies installed."

echo ""
echo "========================================"
echo "🎉 Setup Complete! You can now run:"
echo "   ./run.sh"
echo ""
echo "   Test commands:"
echo "   cd backend && mvn test        (5 backend tests)"
echo "   cd student_portal && npm test  (51 JS tests)"
echo "========================================"
