# 🎓 Smart Quiz System — Enterprise Edition

> **A Next-Generation AI-Powered Examination Platform**
> _Seamlessly connecting Faculty and Students with the power of Generative AI._

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen) ![Gemini](https://img.shields.io/badge/AI-Google%20Gemini-blue) ![License](https://img.shields.io/badge/License-MIT-blue)

---

## 🌐 Live Demo

Experience the platform live on Render:

| Portal                | URL                                                                                  | Credentials           |
| :-------------------- | :----------------------------------------------------------------------------------- | :-------------------- |
| **Faculty Dashboard** | [https://faculty-quiz-portal.onrender.com](https://faculty-quiz-portal.onrender.com) | `admin` / `admin123`  |
| **Student Portal**    | [https://student-quiz-portal.onrender.com](https://student-quiz-portal.onrender.com) | _(No Login Required)_ |
| **Backend**           | [https://quiz-backend-cdxz.onrender.com](https://quiz-backend-cdxz.onrender.com)     | _(No Login Required)_ |


---

## 📑 Table of Contents

1.  [Overview](#-overview)
2.  [Key Features](#-key-features)
3.  [Architecture](#-architecture)
4.  [Tech Stack](#-tech-stack)
5.  [Project Structure](#-project-structure)
6.  [Installation & Setup](#-installation--setup)
7.  [Running Locally](#-running-locally)
8.  [LAN / Lab Access](#-lan--lab-access)
9.  [Deployment](#-deployment)
10. [API Reference](#-api-reference)
11. [Troubleshooting & FAQ](#-troubleshooting--faq)
12. [License](#-license)

---

## 🌟 Overview

The **Smart Quiz System** is a hybrid examination platform designed to modernize the academic assessment process. It eliminates the tedious task of manual question creation by leveraging **Google Gemini AI** to generate technical questions directly from syllabus documents (text or PDF).

The platform consists of three independently deployable services:

1.  **Backend API** — Spring Boot REST server handling all business logic, data, and AI integration.
2.  **Faculty Web Portal** — A feature-rich dashboard for uploading syllabi, generating questions, managing sessions, and viewing analytics.
3.  **Student Web Portal** — A secure, anti-cheating quiz interface accessible on any device via a browser.

---

## 🚀 Key Features

### 🧠 AI & Automation

- **Instant Question Generation** — Paste syllabus text **or upload PDFs** to generate structured MCQs via Google Gemini.
- **Syllabus Analysis** — AI-powered analysis endpoint to extract topics and structure from syllabus content.
- **Intelligent Mapping** — AI maps each question to **Chapters** and **Course Outcomes (CO)**.
- **Bloom's Taxonomy** — Generates Conceptual, Application, and Analysis-level questions.
- **Configurable Weights** — Faculty can set topic-level weights to control question distribution.

### 👨‍🏫 Faculty Command Center

- **Session Management** — Create secure sessions with unique **Session IDs** & auto-generated **OTPs**.
- **JWT Authentication** — Secure login with access/refresh tokens and token blacklisting on logout.
- **Live Scoreboard** — Real-time scoreboard showing student progress, scores, question sets, and cheat flags.
- **Question Editing** — Review and modify AI-generated questions before the exam.
- **Manual Question Creation** — Add custom questions alongside AI-generated ones.
- **CO Analytics** — Aggregated session analytics with per-CO and per-student performance breakdowns.
- **Admin Panel** — Onboard new faculty accounts with password validation.
- **Dark Theme UI** — Modern dark navy theme with glassmorphism effects, custom icons, and responsive layout.

### 👨‍🎓 Student Experience

- **Zero Registration** — Quick entry using Name, Enrollment ID, Session ID, and OTP.
- **Anti-Cheating Suite**:
    - **Tab-Switch Detection** — Flags attempts to leave the exam window.
    - **Randomized Question Sets** — Multiple shuffled sets (A, B, C...) assigned by student ID.
    - **Timer Enforcement** — Server-side time validation prevents late submissions.
    - **Cheat Flagging** — Cheating students are marked and scored zero.
- **State Recovery** — Uses `sessionStorage` so students don't lose work on page refresh.
- **Auto-Recovery** — If the server restarts mid-exam, students can still submit and their identity is recovered.
- **Modular Codebase** — Clean separation into `api.js`, `app.js`, `auth.js`, `components.js`, `utils.js`.

---

## 🏗️ Architecture

The system uses a **3-service architecture** — each component is a standalone static or application server:

```
┌──────────────────────────────┐       ┌──────────────────────────────┐
│    Faculty Web Portal        │       │     Student Web Portal       │
│   (Static HTML/JS/CSS)       │       │    (Static HTML/JS/CSS)      │
│   Served on Port 9876        │       │    Served on Port 8080       │
└──────────────┬───────────────┘       └──────────────┬───────────────┘
               │ REST API (JWT Auth)                  │ REST API
               ▼                                      ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    BACKEND SERVER (Spring Boot)                      │
│                         Port 9090                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │  Controllers  │  │   Services   │  │     Config / Security    │  │
│  │  (REST API)   │  │  (Business)  │  │  (JWT, CORS, DataInit)   │  │
│  └──────┬───────┘  └──────┬───────┘  └──────────┬───────────────┘  │
│         │                 │                      │                  │
│         ▼                 ▼                      ▼                  │
│   [ H2 Database ]   [ Session Mgr ]      [ Google Gemini AI ]      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## ⚡ Tech Stack

| Layer        | Technology                                                                     |
| :----------- | :----------------------------------------------------------------------------- |
| **Backend**  | Java 17, Spring Boot 3.0, Spring Security, Spring Data JPA, Jackson            |
| **Auth**     | JWT (Access + Refresh Tokens), BCrypt password hashing, Token blacklist         |
| **Database** | H2 (Embedded SQL, in-memory by default)                                        |
| **AI**       | Google Gemini API (1.5 Flash) — question generation & syllabus analysis         |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript (ES6) — No heavy frameworks                    |
| **Styling**  | CSS Variables, Dark theme, Glassmorphism, Responsive design, Custom SVG icons  |
| **Deploy**   | Docker (Backend), Static Site (Portals), Render-ready                          |
| **Scripts**  | Bash (`run.sh`, `setup.sh`, `show_access_urls.sh`), Batch (`run.bat`)          |

---

## 📂 Project Structure

```text
QuizFinal/
├── backend/                      # Spring Boot Server Application
│   ├── Dockerfile                # Multi-stage Docker build
│   ├── pom.xml                   # Maven config
│   └── src/main/java/com/quiz/
│       ├── QuizApplication.java  # Entry point
│       ├── config/               # Security, JWT, CORS, DataInitializer
│       │   ├── SecurityConfig.java
│       │   ├── JwtUtils.java
│       │   ├── JwtAuthenticationFilter.java
│       │   └── DataInitializer.java
│       ├── controller/           # REST API endpoints
│       │   ├── AuthController.java       # Login, Logout, Token Refresh
│       │   ├── SessionController.java    # Session lifecycle & OTP
│       │   ├── SyllabusController.java   # AI question generation
│       │   ├── QuizController.java       # Quiz submission & scoring
│       │   ├── ScoreboardController.java # Live scoreboard
│       │   ├── AnalyticsController.java  # CO & session analytics
│       │   ├── AdminController.java      # Faculty management
│       │   └── StudentController.java    # Student registration
│       ├── model/                # JPA Entities
│       │   ├── User.java, Role.java, Faculty.java
│       │   ├── Session.java, Student.java
│       │   ├── Question.java, Answer.java
│       │   ├── Submission.java, Otp.java, Syllabus.java
│       ├── repository/           # Spring Data JPA Repositories
│       ├── service/              # Business Logic
│       │   ├── AIService.java             # Gemini AI integration
│       │   ├── SessionService.java        # Session management
│       │   ├── OTPService.java            # OTP generation & validation
│       │   ├── TokenBlacklistService.java # JWT blacklisting
│       │   └── CustomUserDetailsService.java
│       └── util/                 # Utilities (PasswordValidator)
│
├── faculty_portal/               # Faculty Web Dashboard (Static)
│   ├── index.html                # Main dashboard SPA
│   ├── login.html                # Faculty login page
│   ├── css/style.css             # Dark theme styling
│   ├── js/app.js                 # Dashboard logic
│   ├── js/config.js              # API base URL config
│   └── icons/                    # Custom SVG icons
│
├── student_portal/               # Student Quiz Interface (Static)
│   ├── index.html                # Quiz SPA
│   ├── css/                      # Modular CSS (theme, layout, components, responsive)
│   │   ├── variables.css, theme.css, reset.css
│   │   ├── layout.css, components.css, style.css
│   │   └── responsive.css
│   ├── js/                       # Modular JavaScript
│   │   ├── config.js             # API base URL config
│   │   ├── api.js                # HTTP client & API endpoints
│   │   ├── app.js                # Core quiz logic & state machine
│   │   ├── auth.js               # Student authentication flow
│   │   ├── components.js         # UI components
│   │   ├── utils.js              # Helpers & anti-cheat logic
│   │   └── app.test.js           # Unit tests
│   └── assets/                   # Static assets
│
├── client/                       # JavaFX Desktop Client (Legacy)
│   ├── pom.xml
│   └── src/                      # Desktop app source
│
├── run.sh                        # Launch all 3 services (Linux/Mac)
├── run.bat                       # Launch all 3 services (Windows)
├── setup.sh                      # Environment check & build
├── show_access_urls.sh           # Display LAN IPs for lab access
├── show_access_urls.bat          # Display LAN IPs (Windows)
├── README.md                     # This file
└── info.md                       # Technical documentation
```

---

## 📦 Installation & Setup

### Prerequisites

- **Java 17+** (JDK)
- **Maven 3.6+**
- **Python 3** (for serving static portals locally)
- **Google Gemini API Key** ([Get one here](https://aistudio.google.com/app/apikey))

### Automated Setup

```bash
# 1. Clone the repository
git clone https://github.com/Daku3011/Quiz.git
cd Quiz

# 2. Create your environment file
echo "GEMINI_API_KEY=your_api_key_here" > backend/.env

# 3. Run the setup script (checks dependencies & builds)
./setup.sh
```

---

## ▶️ Running Locally

Use the automated launcher to start all three services:

```bash
# Linux / macOS
./run.sh

# Windows
run.bat
```

This will:
1. Build and start the **Backend** on port `9090`
2. Start the **Faculty Portal** on port `9876` (via Python HTTP server)
3. Start the **Student Portal** on port `8080` (via Python HTTP server)
4. Display all access URLs (including LAN IPs)

### Access Points

| Service              | URL                                                          | Notes                  |
| :------------------- | :----------------------------------------------------------- | :--------------------- |
| **Backend API**      | [http://localhost:9090](http://localhost:9090)                | REST API only          |
| **Faculty Portal**   | [http://localhost:9876](http://localhost:9876)                | Login: `admin`/`admin123` |
| **Student Portal**   | [http://localhost:8080](http://localhost:8080)                | No login required      |

---

## 📱 LAN / Lab Access

To allow students on other devices (phones, tablets) to connect over the local network:

```bash
# Show all access URLs with your LAN IP
./show_access_urls.sh

# If using a firewall, open the required ports:
sudo ufw allow 8080/tcp && sudo ufw allow 9876/tcp && sudo ufw allow 9090/tcp
```

Students connect to `http://<YOUR_IP>:8080` from any browser. The portals auto-detect the host machine's IP for API calls.

---

## 🚀 Deployment

The project is configured for **Render** (or any Docker-compatible host).

### 1. Backend (Docker Web Service)

| Setting           | Value                                  |
| :---------------- | :------------------------------------- |
| **Repo URL**      | `https://github.com/Daku3011/Quiz`     |
| **Root Directory**| `backend`                              |
| **Runtime**       | Docker                                 |
| **Env Vars**      | `GEMINI_API_KEY=your_key`              |

### 2. Faculty Portal (Static Site)

| Setting               | Value                                  |
| :-------------------- | :------------------------------------- |
| **Root Directory**    | `faculty_portal`                       |
| **Build Command**     | _(None)_                               |
| **Publish Directory** | `.`                                    |
| **Config**            | Update `js/config.js` with Backend URL |

### 3. Student Portal (Static Site)

| Setting               | Value                                  |
| :-------------------- | :------------------------------------- |
| **Root Directory**    | `student_portal`                       |
| **Build Command**     | _(None)_                               |
| **Publish Directory** | `.`                                    |
| **Config**            | Update `js/config.js` with Backend URL |

---

## 📡 API Reference

All endpoints are prefixed with `/api`. The Backend runs on port `9090`.

### Authentication

| Method | Endpoint              | Description                          | Auth     |
| :----- | :-------------------- | :----------------------------------- | :------- |
| `POST` | `/api/auth/login`     | Login with username/password → JWT   | None     |
| `POST` | `/api/auth/refresh`   | Refresh an expired access token      | Refresh  |
| `POST` | `/api/auth/logout`    | Blacklist current token              | Bearer   |

### Session Management

| Method | Endpoint                          | Description                            | Auth   |
| :----- | :-------------------------------- | :------------------------------------- | :----- |
| `POST` | `/api/session/start`              | Create session, save questions, gen OTP | Bearer |
| `POST` | `/api/session/join`               | Student joins with OTP validation       | None   |
| `GET`  | `/api/session/{id}/status`        | Get session status & timing info        | None   |
| `GET`  | `/api/session/{id}/questions`     | Get shuffled questions for a student    | None   |
| `GET`  | `/api/session/active`             | List all currently running sessions     | Bearer |
| `POST` | `/api/session/{id}/stop`          | End a running session                   | Bearer |

### Syllabus & AI

| Method | Endpoint                  | Description                               | Auth   |
| :----- | :------------------------ | :---------------------------------------- | :----- |
| `POST` | `/api/syllabus/generate`  | Generate MCQs from text or PDF (Base64)   | Bearer |
| `POST` | `/api/syllabus/analyze`   | Analyze syllabus structure & topics       | Bearer |

### Quiz & Submission

| Method | Endpoint           | Description                                | Auth |
| :----- | :----------------- | :----------------------------------------- | :--- |
| `POST` | `/api/quiz/submit` | Submit answers, get score & results        | None |

### Scoreboard & Analytics

| Method | Endpoint                                           | Description                                | Auth   |
| :----- | :------------------------------------------------- | :----------------------------------------- | :----- |
| `GET`  | `/api/session/{id}/scoreboard`                     | Live scoreboard for a session              | Bearer |
| `GET`  | `/api/analytics/session/{id}`                      | Aggregated CO analytics for a session      | Bearer |
| `GET`  | `/api/analytics/session/{sessionId}/student/{sid}`  | Individual student analytics               | Bearer |

### Admin

| Method | Endpoint               | Description                | Auth   |
| :----- | :--------------------- | :------------------------- | :----- |
| `POST` | `/api/admin/faculty`   | Create a new faculty user  | Bearer |
| `GET`  | `/api/admin/submissions`| Get all quiz submissions  | Bearer |

### Student

| Method | Endpoint                | Description                             | Auth |
| :----- | :---------------------- | :-------------------------------------- | :--- |
| `POST` | `/api/student/register` | Register for a session (name, enrollment, OTP) | None |

---

## ❓ Troubleshooting & FAQ

**Q: "403 Forbidden" on Faculty Login?**
> Hard-refresh your browser (`Ctrl+Shift+R`) to clear cached credentials.

**Q: Port already in use?**
> The `run.sh` script auto-kills processes on ports 8080, 9090, and 9876. If it fails, manually run:
> ```bash
> fuser -k 8080/tcp 9090/tcp 9876/tcp
> ```

**Q: Students can't connect from their devices?**
> 1. Make sure all devices are on the **same network** (Wi-Fi or hotspot).
> 2. Run `./show_access_urls.sh` to get the correct LAN URL.
> 3. Open firewall ports: `sudo ufw allow 8080/tcp && sudo ufw allow 9090/tcp`

**Q: AI question generation fails?**
> Verify your `GEMINI_API_KEY` in `backend/.env` is correct and has quota remaining.

**Q: How do I change default admin credentials?**
> Edit `DataInitializer.java` in `backend/src/main/java/com/quiz/config/` and rebuild.

---

## 📜 License

Distributed under the **MIT License**.\
Owner [Daku3011](https://github.com/Daku3011).
