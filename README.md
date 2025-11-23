# 🎓 Smart Quiz System

A modern, hybrid quiz platform designed for seamless interaction between Faculty and Students. This system combines a robust **JavaFX Desktop Application** for faculty management with a flexible **Web Client** for student participation, powered by a **Spring Boot** backend.

---

## 🚀 Key Features

### 👨‍🏫 Faculty Dashboard (Desktop App)
*   **Syllabus Upload**: Upload text or PDF files to automatically generate quiz questions using AI.
*   **Question Management**: Review, edit, and customize generated questions before starting the session.
*   **Session Management**: Start a new quiz session with a unique **Session ID** and **OTP**.
*   **Live Scoreboard**: Monitor student progress in real-time with a dynamic scoreboard that updates as students submit their answers.
*   **Secure**: Built-in authentication and OTP verification for session security.

### 👨‍🎓 Student Client (Hybrid Access)
Students can join the quiz using **either** the Desktop App or the Web Interface:
*   **Desktop Client**: A native JavaFX application for a focused, distraction-free experience.
*   **Web Client**: A responsive web interface accessible from any device (Mobile, Tablet, Laptop) via the browser.
*   **Seamless Flow**: Register -> Join Session (ID + OTP) -> Take Quiz -> Submit & View Score.

---

## 🛠️ Technology Stack

*   **Backend**: Java 17, Spring Boot 3, Spring Security, Spring Data JPA, H2 Database.
*   **Frontend (Desktop)**: JavaFX 19, CSS3 (Modern UI).
*   **Frontend (Web)**: HTML5, CSS3, Vanilla JavaScript (SPA).
*   **AI Integration**: Mock AI Service (extensible for OpenAI/Gemini integration).
*   **Tools**: Maven, PDFBox (for PDF parsing).

---

## 🏗️ Architecture

```mermaid
graph TD
    subgraph "Faculty Station"
        FD[Faculty Dashboard\n(JavaFX)] -->|REST API| API
    end

    subgraph "Student Devices"
        SD[Student Desktop App\n(JavaFX)] -->|REST API| API
        SW[Student Web Client\n(Browser)] -->|HTTP/REST| API
    end

    subgraph "Backend Server"
        API[Spring Boot API]
        DB[(H2 Database)]
        AI[AI Service]
        
        API --> DB
        API --> AI
        API --> Static[Static Web Resources]
    end
```

---

## 📦 Installation & Setup

### Prerequisites
*   **Java 17** or higher installed.
*   **Maven** installed.

### 1. Start the Backend Server
The backend hosts the API and the Student Web Client.
```bash
cd backend
mvn spring-boot:run
```
*Server starts at `http://localhost:8080`*

### 2. Run the Faculty Dashboard
Open a new terminal:
```bash
cd client
mvn javafx:run
```
*Select "I am a Faculty" to log in.*

### 3. Run the Student Client
**Option A: Desktop App**
Run the client command again and select "I am a Student".
```bash
cd client
mvn javafx:run
```

**Option B: Web Client (Mobile/Laptop)**
Open your browser and navigate to:
*   **Local**: `http://localhost:8080`
*   **Network**: `http://<YOUR_IP_ADDRESS>:8080` (e.g., `http://192.168.1.5:8080`)

---

## 📖 Usage Guide

1.  **Faculty**:
    *   Launch the Dashboard.
    *   Upload a syllabus (Text/PDF) or paste content.
    *   Select the number of questions and click **"Generate Questions"**.
    *   Review/Edit questions and click **"Start Session"**.
    *   Share the **Session ID** and **OTP** with students.
    *   Click **"View Scoreboard"** to watch live results.

2.  **Student**:
    *   Open the App or Web Page.
    *   **Register** with Name and Enrollment Number.
    *   **Join** using the provided Session ID and OTP.
    *   Answer questions and **Submit**.
    *   View your score instantly!

---

## 🤝 Contributing
Feel free to fork this repository and submit pull requests. For major changes, please open an issue first to discuss what you would like to change.

---

**Developed by Daku3011 and Moonshine**
