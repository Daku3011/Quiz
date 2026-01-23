# 🎓 Smart Quiz System - Enterprise Edition

> **A Next-Generation AI-Powered Examination Platform**
> _Seamlessly connecting Faculty and Students with the power of Generative AI._

---

## 📑 Table of Contents

1.  [Overview](#-overview)
2.  [Key Features](#-key-features)
3.  [Architecture & Tech Stack](#-architecture--tech-stack)
4.  [Directory Structure](#-directory-structure)
5.  [Installation & Setup](#-installation--setup)
6.  [Running the System](#-running-the-system)
7.  [Usage Guide](#-usage-guide)
8.  [API Documentation](#-api-documentation)
9.  [Troubleshooting & FAQ](#-troubleshooting--faq)

---

## 🌟 Overview

The **Smart Quiz System** is a hybrid examination platform designed to modernize the academic assessment process. It eliminates the tedious task of manual question creation by leveraging **Google Gemini AI** to generate technical questions directly from syllabus documents.

It consists of three core components:

1.  **Backend Server**: A Spring Boot powerhouse handling logic, security, and AI processing.
2.  **Faculty Command Center**: A modern web-based dashboard for managing sessions and live monitoring.
3.  **Student Portal**: A secure, anti-cheating enabled interface for taking exams.

---

## 🚀 Key Features

### 🧠 AI & Automation

- **Instant Question Generation**: Paste any syllabus text or upload a PDF to generate structured Multiple Choice Questions (MCQs).
- **Intelligent Mapping**: The AI automatically maps questions to specific **Chapters** and **Course Outcomes (CO)** (e.g., CO1, CO2).
- **Bloom's Taxonomy Support**: Generates a mix of Conceptual, Application, and Analysis based questions.

### 👨‍🏫 Faculty Command Center

- **Session Management**: Create secure sessions with a unique **ID** and **OTP** valid for 24 hours.
- **Live Monitoring**: Watch the **Live Scoreboard** update in real-time as students submit answers.
- **Advanced Filters**: Review generated questions by filtering for specific Chapters or COs before finalizing the quiz.
- **Persistence**: Active sessions are saved locally, ensuring you can refresh or rejoin the dashboard without losing the session state.

### 👨‍🎓 Student Experience

- **Seamless Entry**: zero-registration required for quick quizzes; just entering Name and Enrollment ID.
- **Anti-Cheating Mechanisms**:
  - **Randomized Sets**: Each student receives a unique permutation of questions (Set A, B, C...).
  - **Tab-Switch Detection**: The system logs and flags any attempt to leave the exam window.
  - **Timer Enforcement**: Server-side validation of start and end times.
- **Instant Feedback**: Detailed performance analysis with correct answers and explanations displayed immediately after submission.

---

## 🏗️ Architecture & Tech Stack

### Technology Stack

- **Core**: Java 17 (OpenJDK)
- **Framework**: Spring Boot 3.0 (Spring Web, Spring Security, Spring Data JPA)
- **Database**: H2 In-Memory Database (High speed, no external installation required)
- **AI Engine**: Google Gemini 2.5 Flash API
- **Frontend**: HTML5, Vanilla JavaScript, CSS3 (Modern Glassmorphism)
- **Build Tool**: Apache Maven

### System Flow

1.  **Faculty** triggers the `create-session` event.
2.  **Backend** calls **Gemini AI** to parse syllabus and generate JSON questions.
3.  **Questions** are saved to the H2 database.
4.  **Students** connect via WebSocket/REST to fetch their unique Question Set.
5.  **Submissions** are graded instantly on the server and pushed to the Scoreboard.

---

## 📂 Directory Structure

Here is where "everything is installed" and located:

```text
/mnt/Dwarkesh/QuizFinal
├── backend/                  # The Spring Boot Application
│   ├── src/main/java/        # Java Source Code (Controllers, Models, Services)
│   ├── src/main/resources/   # Config files and Static Web Assets
│   │   ├── static/           # host for Student Portal (index.html, js, css)
│   │   └── application.properties
│   ├── target/               # Compiled JAR files live here after build
│   └── .env                  # API Key configuration file
├── faculty_portal/           # The Web-Based Faculty Dashboard
│   ├── index.html            # Main Dashboard
│   ├── login.html            # Secure Login Page
│   └── js/app.js             # Frontend Logic
├── setup.sh                  # Automated Build Script
└── run.sh                    # Automated Launch Script
```

---

## 📦 Installation & Setup

### Prerequisites

- **Java 17+** (`java -version`)
- **Maven** (`mvn -version`)
- **Internet Connection** (For AI API calls)

### Method 1: Automated Setup (Recommended)

We have provided a script that checks your environment, builds the backend, and sets up dependencies.

1.  Open your terminal in the project folder.
2.  Run the setup script:
    ```bash
    ./setup.sh
    ```
    _This will compile the Java code and create the `target` folder._

### Method 2: Manual Setup

If you prefer to do it manually:

1.  Navigate to the backend: `cd backend`
2.  Build the project:
    ```bash
    mvn clean install -DskipTests
    ```

### Configuration

You MUST configure your AI API key before running.

1.  Create or edit the file `backend/.env`.
2.  Add your key:
    ```properties
    GEMINI_API_KEY=your_google_ai_key_here
    ```

---

## ▶️ Running the System

### Option A: The "One-Click" Launcher (Best)

1.  Run the launcher script:
    ```bash
    ./run.sh
    ```
    _This script handles stopping old processes, checking ports, and launching the server._

### Option B: Manual Execution

1.  Go to the backend folder: `cd backend`
2.  Run with Maven:
    ```bash
    mvn spring-boot:run
    ```

**Access Points:**

- **Faculty Portal**: [http://localhost:9876](http://localhost:9876) (Login: `admin` / `admin123`)
- **Student Portal**: [http://localhost:8080](http://localhost:8080)

---

## � Usage Guide

### For Faculty

1.  **Log In**: Go to localhost:9876 and log in.
2.  **Create Quiz**: Navigate to "Create Quiz".
3.  **Input Syllabus**: Paste text or upload a PDF.
4.  **Generate**: Click "Generate Questions" and wait for the AI.
5.  **Review & Filter**: Use the filter bar to check questions by "Chapter" or "CO". Edit if necessary.
6.  **Start Session**: Click "Start Quiz". You will get a **Session ID** and **OTP**.
7.  **Share**: Give the ID and OTP to students.
8.  **Monitor**: Go to the "Active Sessions" tab -> "Scoreboard" to watch live results.

### For Students

1.  **Access**: Go to localhost:8080 on a laptop or mobile.
2.  **Join**: Enter the Session ID and OTP provided by the faculty.
3.  **Exam**:
    - Answer the questions.
    - **Do not switch tabs** (you will be warned).
    - Submit before the timer runs out.
4.  **Results**: View your score and explanations immediately.

---

## 📡 API Documentation

Developers can interact directly with the backend via these endpoints:

| Method      | Endpoint                 | Description                   |
| :---------- | :----------------------- | :---------------------------- |
| **AUTH**    | `/api/auth/login`        | Authenticate Admin/Faculty    |
| **SESSION** | `/api/session/start`     | Initialize a new exam session |
| **SESSION** | `/api/session/active`    | Get list of running exams     |
| **SESSION** | `/api/session/{id}/stop` | Force stop an exam            |
| **AI**      | `/api/syllabus/generate` | Generate questions from text  |
| **STUDENT** | `/api/quiz/submit`       | Submit answers for grading    |
| **ADMIN**   | `/api/admin/faculty`     | Create new faculty accounts   |

---

## ❓ Troubleshooting & FAQ

### Q: I see "403 Forbidden" when loading the page?

**A:** This usually happens with cached assets or favicons. We fixed this in the latest update (v3.2) by updating `SecurityConfig`. Please **Hard Refresh** (Ctrl+Shift+R) your browser.

### Q: "Infinite Recursion (StackOverflow)" error in logs?

**A:** This was a known bug where Session and OTP objects referenced each other in a loop. It has been patched with `@JsonIgnore` annotations. Please restart the backend.

### Q: My "Active Session" disappears when I refresh?

**A:** The Faculty Portal now saves your active session to the browser's **Local Storage**. If it disappears, ensure you are allowing local storage and not in "Incognito" mode with strict blocking.

### Q: Port 8080 is already in use?

**A:** The `run.sh` script tries to free the port automatically. If it fails, run:

```bash
pkill -f quiz-backend
```

---

## 📜 License

This project is open-source and available for educational and commercial use under the **MIT License**.

**Created by Daku3011 and Moonshine**
