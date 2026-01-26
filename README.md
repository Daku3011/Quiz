# 🎓 Smart Quiz System - Enterprise Edition

> **A Next-Generation AI-Powered Examination Platform**
> _Seamlessly connecting Faculty and Students with the power of Generative AI._

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen) ![License](https://img.shields.io/badge/License-MIT-blue)

---

## 🌐 Live Demo

Experience the platform live on Render:

| Portal                | URL                                                                                  | Credentials           |
| :-------------------- | :----------------------------------------------------------------------------------- | :-------------------- |
| **Faculty Dashboard** | [https://faculty-quiz-portal.onrender.com](https://faculty-quiz-portal.onrender.com) | `admin` / `admin123`  |
| **Student Portal**    | [https://quiz-31dy.onrender.com](https://quiz-31dy.onrender.com)                     | _(No Login Required)_ |

---

## 📑 Table of Contents

1.  [Overview](#-overview)
2.  [Key Features](#-key-features)
3.  [Architecture](#-architecture)
4.  [Installation & Setup](#-installation--setup)
5.  [Running Locally](#-running-locally)
6.  [Deployment](#-deployment)
7.  [API Documentation](#-api-documentation)
8.  [Troubleshooting](#-troubleshooting--faq)

---

## 🌟 Overview

The **Smart Quiz System** is a hybrid examination platform designed to modernize the academic assessment process. It eliminates the tedious task of manual question creation by leveraging **Google Gemini AI** to generate technical questions directly from syllabus documents.

It consists of two main interfaces:

1.  **Faculty Web Portal**: A dashboard for uploading syllabus files, generating questions, and monitoring live exams.
2.  **Student Web Portal**: A secure, anti-cheating enabled interface for students to take exams on any device.

---

## 🚀 Key Features

### 🧠 AI & Automation

- **Instant Question Generation**: Paste syllabus text or upload PDFs to generate structured Multiple Choice Questions (MCQs).
- **Intelligent Mapping**: AI maps questions to **Chapters** and **Course Outcomes (CO)**.
- **Bloom's Taxonomy**: Generates Conceptual, Application, and Analysis questions.

### 👨‍🏫 Faculty Command Center

- **Session Management**: Create secure sessions with unique **Session IDs** & **OTPs**.
- **Live Monitoring**: Real-time Scoreboard showing student progress and scores.
- **Question Editing**: Review and modify AI-generated questions before the exam.

### 👨‍🎓 Student Experience

- **Zero Registration**: Quick entry using Name and Enrollment ID.
- **Anti-Cheating**:
  - **Tab-Switch Detection**: Flags attempts to leave the exam window.
  - **Randomized Sets**: Each student gets a unique shuffle of questions.
  - **Timer Enforcement**: Server-side time validation.

---

## 🏗️ Architecture

The system uses a 3-tier architecture with a decoupled frontend for maximum scalability.

```
┌─────────────────────────────┐        ┌──────────────────────────────┐
│   Faculty Web Portal        │        │    Student Web Portal        │
│    (Static Frontend)        │        │     (Spring Boot JSP)        │
└──────────────┬──────────────┘        └──────────────┬───────────────┘
               │ REST API                             │ HTTP/REST
               ▼                                      ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       BACKEND SERVER (Spring Boot)                  │
│  ┌──────────────┐   ┌──────────────┐   ┌─────────────────────────┐  │
│  │  Controller  │   │   Service    │   │      AI Integration     │  │
│  └──────┬───────┘   └──────┬───────┘   └───────────┬─────────────┘  │
│         │                  │                       │                │
│         ▼                  ▼                       ▼                │
│    [ H2 Database ]    [ Session Mgr ]      [ Gemini 1.5 Flash ]     │
└─────────────────────────────────────────────────────────────────────┘
```

**Tech Stack:**

- **Backend**: Java 17, Spring Boot 3.0, Spring Security, H2 Database
- **AI**: Google Gemini API
- **Frontend**: HTML5, CSS3, Vanilla JavaScript (No heavy frameworks)
- **Deployment**: Docker (Backend), Static Site (Frontend)

---

## 📦 Installation & Setup

### Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **Python 3** (Optional, for local faculty portal)

### Automated Setup

We provide a script to check your environment and build the project.

```bash
# 1. Clone the repository
git clone https://github.com/Daku3011/Quiz.git
cd Quiz

# 2. Run Setup Script
./setup.sh
```

> **Important**: Create a `backend/.env` file with your `GEMINI_API_KEY=...` before running.

---

## ▶️ Running Locally

Use the automated launcher script to start all services (Backend + Frontend + Client).

```bash
./run.sh
```

**Access Points:**

- **Faculty Portal**: [http://localhost:9876](http://localhost:9876) (Login: `admin`/`admin123`)
- **Student Portal**: [http://localhost:8080](http://localhost:8080)

---

## 🚀 Deployment

The project is configured for **Render**.

### 1. Backend (Docker)

- **Repo URL**: `https://github.com/Daku3011/Quiz`
- **Root Directory**: `backend`
- **Runtime**: Docker
- **Env Vars**: `GEMINI_API_KEY`

### 2. Frontend (Static Site)

- **Root Directory**: `faculty_portal`
- **Build Command**: (None)
- **Publish Directory**: `.`
- **Config**: Update `js/config.js` with your Backend URL.

---

## 📡 API Documentation

| Method | Endpoint                 | Description               |
| :----- | :----------------------- | :------------------------ |
| `POST` | `/api/auth/login`        | Authenticate Faculty      |
| `POST` | `/api/session/start`     | Create a new Quiz Session |
| `GET`  | `/api/session/active`    | List running sessions     |
| `POST` | `/api/syllabus/generate` | Generate questions via AI |
| `POST` | `/api/quiz/submit`       | Submit student answers    |

---

## ❓ Troubleshooting & FAQ

**Q: "403 Forbidden" on Login?**

> A: Hard refresh your browser (Ctrl+Shift+R) to clear old cache.

**Q: Port 8080 already in use?**

> A: The `run.sh` script attempts to free it. If it fails, run `pkill -f quiz-backend`.

---

## 📜 License

Distributed under the **MIT License**. Created by [Daku3011](https://github.com/Daku3011).
