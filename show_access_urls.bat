@echo off
echo ========================================
echo    External Access Helper (Windows)
echo ========================================
echo.
echo Your IP Addresses (Look for IPv4 Address):
ipconfig | findstr "IPv4"
echo.
echo ----------------------------------------
echo FACULTY PORTAL (Management)
echo ----------------------------------------
echo URL: http://[YOUR_IP]:9876
echo.
echo ----------------------------------------
echo STUDENT PORTAL (Quiz)
echo ----------------------------------------
echo URL: http://[YOUR_IP]:8080
echo.
echo ----------------------------------------
echo INSTRUCTIONS:
echo 1. Connect your phone to your computer's Hotspot/Wi-Fi.
echo 2. Open any browser on your phone.
echo 3. Type one of the URLs above, replacing [YOUR_IP] with the IP Address shown.
echo 4. If it doesn't open, check your Windows Firewall settings.
echo ========================================
pause
