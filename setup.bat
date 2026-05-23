@echo off
setlocal enabledelayedexpansion

:: Set UTF-8 codepage to render emoji and unicode characters correctly
chcp 65001 >nul

echo ========================================
echo    🎓 Smart Quiz System - Windows Setup
echo ========================================
echo.

:: Get the directory of the script
set "PROJECT_ROOT=%~dp0"

:: 1. Check Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed or not in PATH.
    echo    Please install JDK 17+ and try again.
    exit /b 1
)

for /f "tokens=*" %%i in ('java -version 2^>^&1') do (
    echo ✅ Java found: %%i
    goto :java_done
)
:java_done

:: 2. Check Maven
call mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven is not installed or not in PATH.
    echo    Please install Maven and try again.
    exit /b 1
)

for /f "tokens=*" %%i in ('call mvn -version 2^>^&1') do (
    echo ✅ Maven found: %%i
    goto :mvn_done
)
:mvn_done

:: 3. Check/Create .env
set "ENV_FILE=%PROJECT_ROOT%backend\.env"
if not exist "%ENV_FILE%" (
    echo ⚠️  .env file not found in backend/. Creating template...
    (echo GEMINI_API_KEY=replace_with_your_key) > "%ENV_FILE%"
    echo ✅ Created backend/.env. Please edit it with your API Key!
) else (
    echo ✅ backend/.env exists.
)

:: 4. Build Backend
echo.
echo 📦 Building Backend...
cd /d "%PROJECT_ROOT%backend"
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ❌ Backend build failed.
    cd /d "%PROJECT_ROOT%"
    exit /b 1
)
cd /d "%PROJECT_ROOT%"
echo ✅ Backend built successfully.

:: 5. Build Client
echo.
echo 📦 Building Client...
cd /d "%PROJECT_ROOT%client"
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ❌ Client build failed.
    cd /d "%PROJECT_ROOT%"
    exit /b 1
)
cd /d "%PROJECT_ROOT%"
echo ✅ Client built successfully.

echo.
echo ========================================
echo 🎉 Setup Complete! You can now run:
echo    run.bat
echo ========================================
echo.
pause
