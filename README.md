Quiz System - Full Project (Backend + JavaFX Client)
---------------------------------------------------

Requirements:
- Java 17 JDK installed (or compatible JDK 17+)
- Maven 3.6+
- For JavaFX client: ensure JavaFX SDK is available via Maven (the pom uses org.openjfx dependencies)

Run backend:
1. cd backend
2. mvn clean package
3. mvn spring-boot:run
   Backend listens on http://localhost:8080

Quick test:
- Open http://localhost:8080/ in browser - should show 'Server is running!'
- Use JavaFX client to connect to backend.

Run client:
1. cd client
2. mvn javafx:run

Notes:
- AIService uses a placeholder algorithm. Replace with a real LLM API as needed.
- OTPService currently prints OTP to console. Integrate Twilio or email for real delivery.
