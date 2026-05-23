@echo off
setlocal

echo ========================================
echo    Smart Quiz System - Windows Launcher
echo ========================================
echo.

REM Get the directory of the script
set "PROJECT_ROOT=%~dp0"

REM Detect working Python command
set "PYTHON_CMD="

python --version >nul 2>&1
if %errorlevel% equ 0 set "PYTHON_CMD=python"

if defined PYTHON_CMD goto :python_found

py --version >nul 2>&1
if %errorlevel% equ 0 set "PYTHON_CMD=py"

if defined PYTHON_CMD goto :python_found

echo ❌ Neither 'python' nor 'py' command is working on your system.
echo    Please install Python and make sure it is added to your PATH.
pause
exit /b 1

:python_found
echo ✅ Using Python command: %PYTHON_CMD%
echo.

echo 1. Starting Backend (Port 9090)...
echo    (A new window will open for the Backend)
start "Quiz Backend" cmd /k "cd /d %PROJECT_ROOT%backend && mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo.
echo 2. Starting Faculty Portal (Port 9876)...
echo    (A new window will open for the Faculty Portal)
start "Faculty Portal" cmd /k "cd /d %PROJECT_ROOT%faculty_portal && %PYTHON_CMD% -m http.server 9876"

echo.
echo 3. Starting Student Portal (Port 8080)...
echo    (A new window will open for the Student Portal)
start "Student Portal" cmd /k "cd /d %PROJECT_ROOT%student_portal && %PYTHON_CMD% -m http.server 8080"

echo.
echo Waiting for services to initialize...
timeout /t 5 >nul

echo.
call "%PROJECT_ROOT%show_access_urls.bat"

echo.
echo All systems launched in separate windows!
echo Close those windows to stop the servers.
pause

