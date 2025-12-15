# Quiz Application Wiki

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen) ![JavaFX](https://img.shields.io/badge/JavaFX-19-blue) ![License](https://img.shields.io/badge/License-Open%20Source-green)

**Welcome to the Quiz Application Wiki!** This comprehensive guide contains everything you need to know about installing, running, and using the Quiz Application.

---

## 📚 Wiki Contents

| Section | Description |
|---------|-------------|
| **[Home](#quiz-application-wiki)** | Overview and quick start guide |
| **[Installation](#-installation-guide)** | Step-by-step installation instructions |
| **[Getting Started](#-getting-started)** | How to run the application |
| **[Faculty Guide](#-faculty-guide)** | Complete guide for faculty members |
| **[Student Guide](#-student-guide)** | Complete guide for students |
| **[Architecture](#-architecture)** | System design and architecture |
| **[Project Structure](#-project-structure)** | Project Structure |
| **[API Reference](#-api-reference)** | REST API documentation |
| **[Configuration](#-configuration)** | Configuration options |
| **[Troubleshooting](#-troubleshooting)** | Common issues and solutions |
| **[Contributing](#-contributing)** | How to contribute to the project |
| **[FAQ](#-frequently-asked-questions-faq)** | Frequently asked questions |

---

## 🎯 Quick Links

- 🏠 [GitHub Repository](https://github.com/Daku3011/Quiz.git)
- 🐛 [Report Issues](https://github.com/Daku3011/Quiz/issues)
- 💡 [Request Features](https://github.com/Daku3011/Quiz/issues/new)
- 👥 [Contributors](https://github.com/Daku3011/Quiz/graphs/contributors)

---

## 🚀 Overview

The **Quiz Application** is a modern, hybrid quiz platform designed for seamless interaction between Faculty and Students. This system combines a robust **JavaFX Desktop Application** for faculty management with a flexible **Web Client** for student participation, all powered by a **Spring Boot** backend.

### ✨ Key Highlights

- 🤖 **AI-Powered Question Generation** from syllabus documents (Text/PDF)
- 📊 **Real-Time Scoreboard** for live monitoring of student progress
- 💻 **Multi-Platform Support** (Desktop & Web interfaces)
- 🔐 **Secure Session Management** with OTP verification
- 📱 **Responsive Design** works on mobile, tablet, and desktop
- ⚡ **Fast & Lightweight** with H2 embedded database

### 👨‍💻 Developed By

- **[Daku3011](https://github.com/Daku3011)**
- **Moonshine**

### 📦 Repository

```
https://github.com/Daku3011/Quiz.git
```

---

## 📋 Features Overview

### 👨‍🏫 Faculty Features

| Feature | Description |
|---------|-------------|
| 📄 **Syllabus Upload** | Upload text or PDF files to automatically generate quiz questions using AI |
| ✏️ **Question Management** | Review, edit, and customize generated questions before starting the session |
| 🎮 **Session Management** | Start a new quiz session with a unique Session ID and OTP |
| 📊 **Live Scoreboard** | Monitor student progress in real-time with a dynamic scoreboard |
| 🔒 **Secure Access** | Built-in authentication and OTP verification for session security |

### 👨‍🎓 Student Features

Students can join the quiz using either the **Desktop App** or the **Web Interface**:

| Platform | Description |
|----------|-------------|
| 🖥️ **Desktop Client** | Native JavaFX application for a focused, distraction-free experience |
| 🌐 **Web Client** | Responsive web interface accessible from any device (Mobile, Tablet, Laptop) |
| ✅ **Seamless Flow** | Register → Join Session (ID + OTP) → Take Quiz → Submit & View Score |

> **Note:** Both platforms provide the same functionality, allowing students to choose their preferred method of participation.

---

## 🏗️ [Architecture](#-architecture)
The application follows a **three-tier architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                      │
├─────────────────────────────┬───────────────────────────────────┤
│      Faculty Station        │         Student Devices           │
│   ┌─────────────────────┐   │    ┌──────────────────────────┐   │
│   │ Faculty Dashboard   │   │    │ Desktop App (JavaFX)     │   │
│   │     (JavaFX)        │   │    │ Web Client (Browser)     │   │
│   └──────────┬──────────┘   │    └────────────┬─────────────┘   │
└──────────────┼──────────────┴─────────────────┼─────────────────┘
               │ REST API                       │ HTTP/REST
               │                                │
┌──────────────▼────────────────────────────────▼─────────────────┐
│                       APPLICATION LAYER                         │
│   ┌──────────────────────────────────────────────────────┐      │
│   │            Spring Boot REST API                      │      │
│   │  ┌────────────┬──────────────┬─────────────────┐     │      │
│   │  │Controllers │   Services   │   Security      │     │      │
│   │  └────────────┴──────────────┴─────────────────┘     │      │
│   └───────────────────────┬──────────────────────────────┘      │
└───────────────────────────┼─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                         DATA LAYER                              │
│   ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐       │
│   │ H2 Database  │  │  AI Service  │  │ Static Resources│       │
│   │  (Embedded)  │  │   (Mock)     │  │   (Web Files)   │       │
│   └──────────────┘  └──────────────┘  └─────────────────┘       │
└─────────────────────────────────────────────────────────────────┘
```

### 🔄 Data Flow

1. **Faculty** uploads syllabus via JavaFX Desktop → Backend generates questions
2. **Faculty** starts session → Backend creates Session ID & OTP
3. **Students** register and join via Desktop/Web → Backend validates credentials
4. **Students** submit answers → Backend calculates scores
5. **Faculty** views scoreboard → Backend provides real-time updates

> 💡 **Tip:** All communication happens through REST APIs, making the system highly scalable and maintainable.

---

## 🛠️ Technology Stack

### Backend Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| ☕ **Java** | 17+ | Core programming language |
| 🍃 **Spring Boot** | 3.x | Application framework |
| 🔐 **Spring Security** | - | Authentication & authorization |
| 💾 **Spring Data JPA** | - | Database access layer |
| 🗄️ **H2 Database** | - | In-memory/embedded database |
| 📦 **Maven** | 3.6+ | Build & dependency management |
| 📄 **PDFBox** | - | PDF parsing for syllabus upload |

### Frontend Technologies (Desktop)

| Technology | Version | Purpose |
|------------|---------|---------|
| 🖼️ **JavaFX** | 19 | Desktop UI framework |
| 🎨 **CSS3** | - | Modern UI styling |

### Frontend Technologies (Web)

| Technology | Version | Purpose |
|------------|---------|---------|
| 🌐 **HTML5** | - | Structure |
| 🎨 **CSS3** | - | Styling & responsive design |
| ⚡ **Vanilla JavaScript** | ES6+ | SPA functionality (no frameworks) |

### AI & Integration

| Component | Description |
|-----------|-------------|
| 🤖 **Mock AI Service** | Built-in question generator |
| 🔌 **Extensible API** | Ready for OpenAI/Gemini integration |

> 📌 **Design Philosophy:** The application uses battle-tested technologies with minimal dependencies for maximum stability and ease of deployment.

---

## 💻 System Requirements

### Hardware Requirements

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| **Processor** | Dual-core 2.0 GHz | Quad-core 2.5 GHz+ |
| **RAM** | 4 GB | 8 GB+ |
| **Storage** | 500 MB free | 1 GB+ free |
| **Network** | Basic internet | Broadband connection |

### Software Requirements

| Software | Version | Notes |
|----------|---------|-------|
| **Java JDK** | 17 or higher | Oracle JDK or OpenJDK |
| **Apache Maven** | 3.6+ | For building the project |
| **Operating System** | Windows 10+, macOS 10.14+, Linux (Ubuntu 20.04+) | 64-bit required |
| **Web Browser** | Latest version | Chrome, Firefox, Safari, or Edge |

### Network Requirements

| Requirement | Details |
|-------------|---------|
| **Port Availability** | Port 8080 must be available |
| **Local Network** | For mobile device access, all devices must be on same network |
| **Firewall** | Allow Java through firewall for network access |

> ⚠️ **Important:** Ensure Java 17+ is installed before proceeding with installation.

---

## 📥 Installation Guide

### Step 1️⃣: Install Java 17

#### 🪟 Windows

1. Download Java 17 JDK from:
   - [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or
   - [Adoptium Eclipse Temurin](https://adoptium.net/)

2. Run the installer and follow the wizard

3. Set environment variables:
   ```cmd
   setx JAVA_HOME "C:\Program Files\Java\jdk-17"
   setx PATH "%PATH%;%JAVA_HOME%\bin"
   ```

4. Verify installation:
   ```cmd
   java -version
   ```

#### 🍎 macOS

Using Homebrew:
```bash
# Install Homebrew if not installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 17
brew install openjdk@17

# Link Java
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

Verify:
```bash
java -version
```

#### 🐧 Linux (Ubuntu/Debian)

```bash
# Update package list
sudo apt update

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Verify installation
java -version
```

---

### Step 2️⃣: Install Maven

#### 🪟 Windows

1. Download Maven from [Apache Maven](https://maven.apache.org/download.cgi)
2. Extract to `C:\Program Files\Apache\maven`
3. Set environment variables:
   ```cmd
   setx MAVEN_HOME "C:\Program Files\Apache\maven"
   setx PATH "%PATH%;%MAVEN_HOME%\bin"
   ```
4. Verify:
   ```cmd
   mvn -version
   ```

#### 🍎 macOS

```bash
brew install maven
mvn -version
```

#### 🐧 Linux (Ubuntu/Debian)

```bash
sudo apt install maven -y
mvn -version
```

---

### Step 3️⃣: Clone the Repository

```bash
# Clone the repository
git clone https://github.com/Daku3011/Quiz.git

# Navigate to project directory
cd Quiz
```

> 💡 **Tip:** If you don't have Git installed, download the ZIP file from GitHub and extract it.

---

### Step 4️⃣: Build the Project

#### Build Backend

```bash
cd backend
mvn clean install
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  XX.XXX s
```

#### Build Client

```bash
cd ../client
mvn clean install
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  XX.XXX s
```

> ⚠️ **Troubleshooting:** If build fails, see the [Troubleshooting](#-troubleshooting) page.

---

## ✅ Installation Complete!

Your Quiz Application is now ready to run. Proceed to the [Getting Started](#-getting-started) guide to launch the application.

---

## 🚀 Getting Started

Follow these steps to run the Quiz Application:

### 🔹 Step 1: Start the Backend Server

The backend hosts the REST API and serves the Student Web Client.

```bash
# Navigate to backend directory
cd backend

# Start the server
mvn spring-boot:run
```

**Expected Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.x.x)

...
Started QuizApplication in 5.234 seconds (JVM running for 5.678)
Server running at http://localhost:8080
```

✅ **Server is ready when you see:** `Started QuizApplication`

> ⚠️ **Note:** Keep this terminal window open. The server must run continuously.

---

### 🔹 Step 2: Launch Faculty Dashboard

Open a **new terminal window**:

```bash
# Navigate to client directory
cd client

# Launch JavaFX application
mvn javafx:run
```

**Login Screen:**
- Select **"I am a Faculty"** to access the Faculty Dashboard

<details>
<summary>📸 Click to see Faculty Dashboard preview</summary>

```
┌─────────────────────────────────────┐
│      Quiz Application - Login       │
├─────────────────────────────────────┤
│                                     │
│   ⚪ I am a Faculty                 │
│   ⚪ I am a Student                 │
│                                     │
│         [  Login  ]                 │
│                                     │
└─────────────────────────────────────┘
```
</details>

---

### 🔹 Step 3: Launch Student Client

Students can choose between **Desktop App** or **Web Browser**.

#### Option A: Desktop Application

Open a **new terminal window**:

```bash
cd client
mvn javafx:run
```

- Select **"I am a Student"** at login

#### Option B: Web Client (Recommended for Mobile/Tablets)

**For Local Access:**
```
http://localhost:8080
```

**For Network Access (Other Devices):**

1. Find your IP address:
   ```bash
   # Windows
   ipconfig
   
   # macOS/Linux
   ifconfig
   # or
   ip addr show
   ```

2. Open browser on any device (mobile, tablet, laptop) and navigate to:
   ```
   http://<YOUR_IP_ADDRESS>:8080
   ```
   
   Example:
   ```
   http://192.168.1.5:8080
   ```

> 💡 **Tip:** Students on mobile devices should use the Web Client for the best experience!

---

## 🎯 Quick Start Workflow

### For Faculty:

1. ✅ Start Backend Server
2. ✅ Launch Faculty Dashboard
3. 📄 Upload Syllabus
4. 🎲 Generate Questions
5. ✏️ Review & Edit Questions
6. 🎮 Start Session (Get Session ID & OTP)
7. 📢 Share Session ID & OTP with Students
8. 📊 View Live Scoreboard

### For Students:

1. ✅ Backend Server must be running
2. 🌐 Open Web Client OR 🖥️ Launch Desktop App
3. 📝 Register (Name + Enrollment Number)
4. 🔑 Join Session (Enter Session ID + OTP)
5. ✍️ Take Quiz
6. 📤 Submit Answers
7. 🎉 View Score

---

## 📊 System Status Check

Verify all components are running:

| Component | URL/Command | Status Check |
|-----------|-------------|--------------|
| Backend API | `http://localhost:8080/api` | Should return API info |
| Web Client | `http://localhost:8080` | Should show student login |
| Faculty Dashboard | Terminal output | JavaFX window opens |
| Student Desktop | Terminal output | JavaFX window opens |

> 🎉 **Success!** All components are running. You're ready to create your first quiz!

---

**Next Steps:**
- 📖 Read the [Faculty Guide](#-faculty-guide) to learn how to create quizzes
- 📖 Read the [Student Guide](#-student-guide) to learn how to take quizzes
- ⚙️ Check [Configuration](#-configuration) for customization options

---

## 👨‍🏫 Faculty Guide

Complete guide for faculty members to create and manage quizzes.

### 🎬 Getting Started

#### Launch Faculty Dashboard

```bash
cd client
mvn javafx:run
```

Select **"I am a Faculty"** at the login screen.

---

### 📝 Creating a Quiz

#### Step 1: Upload Syllabus

You have three options:

**Option 1: Upload Text File**
- Click `📄 Upload File` button
- Select a `.txt` file containing syllabus content
- File will be automatically processed

**Option 2: Upload PDF File**
- Click `📄 Upload File` button
- Select a `.pdf` file
- System will extract text automatically using PDFBox

**Option 3: Paste Content**
- Click on the text area
- Paste your syllabus content directly
- Supports multi-line content

> 💡 **Best Practice:** Ensure syllabus content is well-structured with clear topics and concepts for better question generation.

---

#### Step 2: Generate Questions

1. **Select Question Count:**
   - Use the dropdown or input field
   - Options: 5, 10, 15, 20, 25, 30 questions
   - Default: 5 questions

2. **Click "Generate Questions":**
   - AI processes the syllabus
   - Questions are generated automatically
   - Wait for confirmation message

**Generation Time:** 
- 10 questions: ~5-10 seconds
- 30 questions: ~15-30 seconds

<details>
<summary>🤖 How AI Generates Questions</summary>

The AI service:
1. Analyzes syllabus content
2. Identifies key topics and concepts
3. Creates multiple-choice questions
4. Generates 4 options per question
5. Marks the correct answer
6. Returns formatted questions

</details>

---

#### Step 3: Review & Edit Questions

After generation, you can:

**✏️ Edit Questions:**
- Click on any question text to edit
- Modify question wording
- Update options
- Change correct answer
- Save changes

**➕ Add Custom Questions:**
- Click `+ Add Question` button
- Enter question text
- Add 4 options (A, B, C, D)
- Mark correct answer
- Save

**❌ Delete Questions:**
- Click trash icon next to any question
- Confirm deletion
- Question is removed from quiz

**📋 Question Preview:**
```
Q1. What is Java?
   A) A programming language ✓
   B) A coffee brand
   C) An operating system
   D) A web browser

[Edit] [Delete]
```

> ⚠️ **Important:** Review all questions carefully before starting the session!

---

#### Step 4: Start Quiz Session

1. Click `🎮 Start Session` button

2. **Session Created:**
   ```
   ╔════════════════════════════════╗
   ║      Session Created!          ║
   ╠════════════════════════════════╣
   ║  Session ID: ABC123            ║
   ║  OTP: 456789                   ║
   ║  Valid Until: 10:30 PM         ║
   ╚════════════════════════════════╝
   ```

3. **Share with Students:**
   - Write on whiteboard
   - Send via chat/email
   - Project on screen
   - Announce verbally

> 🔒 **Security:** Session ID and OTP are required for students to join. Keep OTP confidential.

---

### 📊 Monitoring Quiz

#### Live Scoreboard

1. Click `📊 View Scoreboard` button

2. **Scoreboard Display:**
   ```
   ┌───────────────────────────────────────────────┐
   │           Live Quiz Scoreboard                │
   ├──────┬─────────────────┬───────────┬──────────┤
   │ Rank │ Student Name    │ Enroll No │  Score   │
   ├──────┼─────────────────┼───────────┼──────────┤
   │  1   │ John Doe        │ 2021001   │  9/10 ✓  │
   │  2   │ Jane Smith      │ 2021002   │  8/10 ✓  │
   │  3   │ Bob Johnson     │ 2021003   │  7/10 ⏳ │
   └──────┴─────────────────┴───────────┴──────────┘
   
   ✓ Submitted  ⏳ In Progress  ❌ Not Started
   ```

**Features:**
- ⚡ **Real-time Updates:** Scoreboard refreshes automatically
- 📈 **Rankings:** Students ranked by score
- ✅ **Status Indicators:** See who submitted, in progress, or not started
- 📊 **Statistics:** Average score, completion rate
- 🔄 **Auto-refresh:** Every 5 seconds

---

### 🎛️ Session Management

#### Active Sessions

View all active sessions:
- Session ID
- Number of participants
- Start time
- Questions count
- Status (Active/Completed)

#### End Session

1. Click `⏹️ End Session` button
2. Confirm action
3. Students can no longer submit
4. Final scores are frozen

#### Export Results

1. Click `📥 Export Results`
2. Choose format:
   - CSV (for Excel)
   - PDF (for printing)
   - JSON (for processing)
3. Save file to desired location



---

### 💡 Tips & Best Practices

| Tip | Description |
|-----|-------------|
| 🎯 **Clear Questions** | Ensure questions are unambiguous and grammatically correct |
| ⏱️ **Time Management** | Set appropriate quiz duration based on question count |
| 🔄 **Test First** | Test the quiz yourself before sharing with students |
| 📢 **Clear Instructions** | Explain quiz format and rules before starting |
| 👀 **Monitor Live** | Keep scoreboard open to track student progress |
| 💾 **Save Sessions** | Export results immediately after quiz completion |

---

### ❓ Faculty FAQ

<details>
<summary><strong>Can I reuse questions from previous quizzes?</strong></summary>

Currently, questions are not saved between sessions. Plan to implement a question bank feature in future updates.
</details>

<details>
<summary><strong>How many students can join simultaneously?</strong></summary>

The system supports up to 100 concurrent students per session with current configuration.
</details>

<details>
<summary><strong>Can I pause a quiz?</strong></summary>

Currently, quizzes cannot be paused once started. Students must complete in one sitting.
</details>

<details>
<summary><strong>What if a student loses connection?</strong></summary>

Students can rejoin using the same Session ID and OTP. Their progress is saved.
</details>

---

**Next:** Read the [Student Guide](#-student-guide) | Check [API Reference](#-api-reference) | View [Troubleshooting](#-troubleshooting)

---

## 👨‍🎓 Student Guide

Complete guide for students to join and take quizzes.

### 🎯 Choosing Your Platform

Students can participate using either:

| Platform | Best For | Pros |
|----------|----------|------|
| 🖥️ **Desktop App** | Focused environment | No distractions, native performance |
| 🌐 **Web Client** | Mobile/Tablet | No installation, works anywhere |

---

### 🖥️ Using Desktop Application

#### Step 1: Launch Application

```bash
cd client
mvn javafx:run
```

Select **"I am a Student"** at the login screen.

---

#### Step 2: Register

Fill in the registration form:

```
┌─────────────────────────────────┐
│      Student Registration       │
├─────────────────────────────────┤
│                                 │
│  Name: [John Doe              ] │
│                                 │
│  Enrollment No: [2021001      ] │
│                                 │
│         [  Register  ]          │
│                                 │
└─────────────────────────────────┘
```

**Fields:**
- **Name:** Your full name
- **Enrollment Number:** Your student ID

Click `Register` button.

---

#### Step 3: Join Quiz Session

Enter session details provided by faculty:

```
┌─────────────────────────────────┐
│         Join Quiz Session       │
├─────────────────────────────────┤
│                                 │
│  Session ID: [ABC123          ] │
│                                 │
│  OTP: [456789                 ] │
│                                 │
│         [  Join Quiz  ]         │
│                                 │
└─────────────────────────────────┘
```

> 🔑 **Important:** Get Session ID and OTP from your faculty before joining.

---

#### Step 4: Take the Quiz

**Quiz Interface:**

```
┌───────────────────────────────────────────────┐
│  Question 1 of 10                             │
├───────────────────────────────────────────────┤
│                                               │
│  What is the capital of France?               │
│                                               │
│  ⚪ A) London                                 │
│  ⚪ B) Berlin                                 │
│  🔘 C) Paris                                  │
│  ⚪ D) Madrid                                 │
│                                               │
├───────────────────────────────────────────────┤
│  [◀ Previous] [Next ▶]              [Submit]  │
└───────────────────────────────────────────────┘
```

**Navigation:**
- Click on an option to select your answer
- Use `Next` button to move to next question
- Use `Previous` button to go back
- Selected answers are highlighted

**Features:**
- 📍 **Progress:** Current question number
- ✅ **Selection:** Visual feedback for selected answers
- 🔄 **Review:** Can change answers before submitting

---

#### Step 5: Submit Quiz

1. Review all your answers
2. Click `Submit Quiz` button
3. Confirm submission

**Confirmation Dialog:**
```
┌────────────────────────────────┐
│      Submit Quiz?              │
├────────────────────────────────┤
│  Are you sure you want to      │
│  submit? You cannot change     │
│  answers after submission.     │
│                                │
│   [Cancel]    [Submit]         │
└────────────────────────────────┘
```

> ⚠️ **Warning:** Once submitted, answers cannot be changed!

---

#### Step 6: View Results

**Results Screen:**

```
┌────────────────────────────────────┐
│         Quiz Results               │
├────────────────────────────────────┤
│                                    │
│  🎉 Congratulations, John Doe!     │
│                                    │
│  Your Score: 8 / 10                │
│  Percentage: 80%                   │
│  Grade: B                          │
│                                    │
│  ✅ Correct Answers: 8             │
│  ❌ Wrong Answers: 2               │
│                                    │
│  Time Taken: 12:34                 │
│                                    │
│         [View Answers]             │
│         [Exit]                     │
│                                    │
└────────────────────────────────────┘
```

---

### 🌐 Using Web Client

#### Step 1: Open Browser

Navigate to one of these URLs:

**If on the same computer as server:**
```
http://localhost:8080
```

**If on a different device (mobile/tablet):**
```
http://<FACULTY_IP_ADDRESS>:8080
```

Example: `http://192.168.1.5:8080`

> 💡 **Tip:** Ask your faculty for the correct IP address.

---

#### Step 2: Register

**Web Registration Form:**

<table>
<tr><td>

```html
┌──────────────────────────────┐
│   📱 Quiz App - Register     │
├──────────────────────────────┤
│                              │
│ 👤 Name                      │
│ [John Doe                  ] │
│                              │
│ 🎓 Enrollment Number         │
│ [2021001                   ] │
│                              │
│      [  Register  ]          │
│                              │
└──────────────────────────────┘
```

</td></tr>
</table>

Fill in your details and click `Register`.

---

#### Step 3: Join Session

**Join Session Form:**

<table>
<tr><td>

```html
┌──────────────────────────────┐
│   📱 Quiz App - Join         │
├──────────────────────────────┤
│                              │
│ 🔑 Session ID                │
│ [ABC123                    ] │
│                              │
│ 🔐 OTP                       │
│ [456789                    ] │
│                              │
│      [  Join Quiz  ]         │
│                              │
└──────────────────────────────┘
```

</td></tr>
</table>

Enter Session ID and OTP, then click `Join Quiz`.

---

#### Step 4: Answer Questions

**Mobile-Optimized Interface:**

<table>
<tr><td>

```html
┌─────────────────────────────┐
│ ☰                  ⏱️ 14:32 │
├─────────────────────────────┤
│                             │
│ Question 1/10               │
│                             │
│ What is Java?               │
│                             │
│ [ ] A programming language  │
│ [x] A coffee brand          │
│ [ ] An operating system     │
│ [ ] A database              │
│                             │
│ ┌─────────────────────────┐ │
│ │    Next Question  →     │ │
│ └─────────────────────────┘ │
│                             │
└─────────────────────────────┘
```

</td></tr>
</table>

**Features:**
- 📱 **Responsive:** Adapts to screen size
- 👆 **Touch-friendly:** Large tap targets
- 🎨 **Modern UI:** Clean, intuitive design
- 💾 **Auto-save:** Progress saved automatically

---

#### Step 5: Submit & View Score

Tap `Submit Quiz` → Confirm → View instant results!

**Mobile Results:**

<table>
<tr><td>

```html
┌─────────────────────────────┐
│        🎉 Results           │
├─────────────────────────────┤
│                             │
│   Score: 8/10 (80%)         │
│                             │
│   ✅ Correct: 8             │
│   ❌ Wrong: 2               │
│                             │
│   Rank: #3                  │
│                             │
│ ┌─────────────────────────┐ │
│ │   View Detailed Report  │ │
│ └─────────────────────────┘ │
│                             │
└─────────────────────────────┘
```

</td></tr>
</table>

---

### 📱 Mobile Best Practices

| Tip | Description |
|-----|-------------|
| 🔋 **Battery** | Ensure device is charged before starting |
| 📶 **Connection** | Stable WiFi connection required |
| 🔕 **Notifications** | Enable Do Not Disturb mode |
| 🌐 **Browser** | Use Chrome or Safari for best experience |
| 📏 **Orientation** | Portrait mode recommended |
| 💾 **Background Apps** | Close unnecessary apps |

---

### ❓ Student FAQ

<details>
<summary><strong>Can I join from my phone?</strong></summary>

Yes! Use the web client for the best mobile experience. Just open your browser and enter the provided URL.
</details>

<details>
<summary><strong>What if I lose internet connection during the quiz?</strong></summary>

Your progress is saved. Rejoin using the same Session ID and OTP to continue where you left off.
</details>

<details>
<summary><strong>Can I change my answers?</strong></summary>

Yes, you can change answers before submitting. Use the Previous/Next buttons to navigate.
</details>

<details>
<summary><strong>How much time do I have?</strong></summary>

Time limit (if any) is set by your faculty and displayed at the top of the quiz.
</details>

<details>
<summary><strong>Can I see the correct answers after submission?</strong></summary>

This depends on settings configured by your faculty. Some quizzes show answers, others don't.
</details>

<details>
<summary><strong>What browsers are supported?</strong></summary>

Chrome, Firefox, Safari, and Edge (latest versions). Chrome recommended for mobile devices.
</details>

---

### 🎓 Quiz Tips

- ✅ Read questions carefully before answering
- ⏰ Manage your time wisely
- 🤔 If unsure, mark your best guess and move on
- 🔄 Review all answers before submitting
- 📱 Keep device charged and connected
- 🎯 Stay focused and calm

---

## 📁 Project Structure

### 🗂️ Directory Tree

```
Quiz/
│
├── 📁 backend/                          # Spring Boot Backend
│   ├── 📁 src/
│   │   ├── 📁 main/
│   │   │   ├── 📁 java/
│   │   │   │   └── 📁 com/quiz/
│   │   │   │       ├── 📁 controller/   # REST API Controllers
│   │   │   │       │   ├── AuthController.java
│   │   │   │       │   ├── SessionController.java
│   │   │   │       │   ├── QuestionController.java
│   │   │   │       │   └── SubmissionController.java
│   │   │   │       │
│   │   │   │       ├── 📁 model/        # Entity Models (JPA)
│   │   │   │       │   ├── User.java
│   │   │   │       │   ├── Session.java
│   │   │   │       │   ├── Question.java
│   │   │   │       │   ├── Answer.java
│   │   │   │       │   └── Submission.java
│   │   │   │       │
│   │   │   │       ├── 📁 repository/   # JPA Repositories
│   │   │   │       │   ├── UserRepository.java
│   │   │   │       │   ├── SessionRepository.java
│   │   │   │       │   ├── QuestionRepository.java
│   │   │   │       │   └── SubmissionRepository.java
│   │   │   │       │
│   │   │   │       ├── 📁 service/      # Business Logic
│   │   │   │       │   ├── AuthService.java
│   │   │   │       │   ├── SessionService.java
│   │   │   │       │   ├── QuestionGenerationService.java
│   │   │   │       │   ├── AIService.java
│   │   │   │       │   └── ScoringService.java
│   │   │   │       │
│   │   │   │       ├── 📁 security/     # Security Configuration
│   │   │   │       │   ├── SecurityConfig.java
│   │   │   │       │   ├── JwtTokenProvider.java
│   │   │   │       │   └── JwtAuthenticationFilter.java
│   │   │   │       │
│   │   │   │       ├── 📁 dto/          # Data Transfer Objects
│   │   │   │       │   ├── LoginRequest.java
│   │   │   │       │   ├── SessionRequest.java
│   │   │   │       │   └── SubmissionRequest.java
│   │   │   │       │
│   │   │   │       ├── 📁 util/         # Utility Classes
│   │   │   │       │   ├── PDFParser.java
│   │   │   │       │   └── OTPGenerator.java
│   │   │   │       │
│   │   │   │       └── QuizApplication.java  # Main Application
│   │   │   │
│   │   │   └── 📁 resources/
│   │   │       ├── 📁 static/           # Web Client Files
│   │   │       │   ├── index.html       # Student Web App
│   │   │       │   ├── 📁 css/
│   │   │       │   │   ├── styles.css
│   │   │       │   │   └── mobile.css
│   │   │       │   ├── 📁 js/
│   │   │       │   │   ├── app.js
│   │   │       │   │   ├── quiz.js
│   │   │       │   │   └── api.js
│   │   │       │   └── 📁 assets/
│   │   │       │       └── images/
│   │   │       │
│   │   │       ├── application.properties   # Configuration
│   │   │       ├── application.yml
│   │   │       └── data.sql             # Sample Data (optional)
│   │   │
│   │   └── 📁 test/                     # Unit Tests
│   │       └── 📁 java/
│   │           └── 📁 com/quiz/
│   │               ├── ControllerTests.java
│   │               ├── ServiceTests.java
│   │               └── RepositoryTests.java
│   │
│   ├── pom.xml                          # Maven Dependencies
│   └── README.md
│
├── 📁 client/                           # JavaFX Desktop Client
│   ├── 📁 src/
│   │   ├── 📁 main/
│   │   │   ├── 📁 java/
│   │   │   │   └── 📁 com/quiz/client/
│   │   │   │       ├── Main.java                 # Entry Point
│   │   │   │       ├── LoginScreen.java          # Login UI
│   │   │   │       ├── FacultyDashboard.java     # Faculty UI
│   │   │   │       ├── StudentApp.java           # Student UI
│   │   │   │       ├── ScoreboardView.java       # Live Scoreboard
│   │   │   │       │
│   │   │   │       ├── 📁 controller/            # UI Controllers
│   │   │   │       │   ├── LoginController.java
│   │   │   │       │   ├── FacultyController.java
│   │   │   │       │   └── StudentController.java
│   │   │   │       │
│   │   │   │       ├── 📁 model/                 # Client Models
│   │   │   │       │   ├── Question.java
│   │   │   │       │   ├── Session.java
│   │   │   │       │   └── Score.java
│   │   │   │       │
│   │   │   │       └── 📁 util/                  # Utilities
│   │   │   │           ├── APIClient.java        # REST API Client
│   │   │   │           ├── SessionManager.java
│   │   │   │           └── FileHandler.java
│   │   │   │
│   │   │   └── 📁 resources/
│   │   │       ├── 📁 css/                       # JavaFX Styles
│   │   │       │   ├── faculty-dashboard.css
│   │   │       │   ├── student-app.css
│   │   │       │   └── common.css
│   │   │       │
│   │   │       ├── 📁 fxml/                      # JavaFX Layouts
│   │   │       │   ├── login.fxml
│   │   │       │   ├── faculty-dashboard.fxml
│   │   │       │   ├── student-app.fxml
│   │   │       │   └── scoreboard.fxml
│   │   │       │
│   │   │       └── 📁 images/                    # Icons & Assets
│   │   │           ├── logo.png
│   │   │           └── icons/
│   │   │
│   │   └── 📁 test/                              # Client Tests
│   │       └── 📁 java/
│   │
│   ├── pom.xml                                   # Maven Dependencies
│   └── README.md
│
├── 📁 docs/                                      # Documentation
│   ├── API.md                                    # API Documentation
│   ├── SETUP.md                                  # Setup Guide
│   └── CONTRIBUTING.md                           # Contribution Guide
│
├── 📁 scripts/                                   # Utility Scripts
│   ├── start-backend.sh                          # Start Backend
│   ├── start-faculty.sh                          # Start Faculty App
│   └── start-student.sh                          # Start Student App
│
├── .gitignore                                    # Git Ignore File
├── LICENSE                                       # License Information
└── README.md                                     # Project README
```

---

### 📦 Key Components

#### Backend Components

| Component | Purpose | Technology |
|-----------|---------|------------|
| **Controllers** | Handle HTTP requests and responses | Spring MVC |
| **Services** | Business logic and AI integration | Spring Service |
| **Repositories** | Database operations | Spring Data JPA |
| **Models** | Entity definitions | JPA Entities |
| **Security** | Authentication & authorization | Spring Security + JWT |
| **Static Resources** | Student web client | HTML/CSS/JS |

#### Client Components

| Component | Purpose | Technology |
|-----------|---------|------------|
| **Main** | Application entry point | JavaFX |
| **Views** | User interface screens | JavaFX + FXML |
| **Controllers** | UI event handling | JavaFX Controllers |
| **API Client** | Backend communication | HTTP Client |
| **Styles** | Visual appearance | CSS3 |

---

### 🔧 Configuration Files

| File | Location | Purpose |
|------|----------|---------|
| `application.properties` | `backend/src/main/resources/` | Backend configuration |
| `pom.xml` | `backend/` & `client/` | Maven dependencies |
| `*.css` | `client/src/main/resources/css/` | JavaFX styling |
| `*.fxml` | `client/src/main/resources/fxml/` | JavaFX layouts |

---

### 📊 Database Schema

The H2 database includes these tables:

```sql
users
├── id (PK)
├── username
├── password (hashed)
├── role (FACULTY/STUDENT)
└── created_at

sessions
├── id (PK)
├── session_id (unique)
├── otp
├── faculty_id (FK)
├── status
├── created_at
└── expires_at

questions
├── id (PK)
├── session_id (FK)
├── question_text
├── option_a
├── option_b
├── option_c
├── option_d
├── correct_answer
└── created_at

submissions
├── id (PK)
├── session_id (FK)
├── student_name
├── enrollment_number
├── score
├── answers (JSON)
└── submitted_at
```

---

**Related Pages:** [Architecture][def] | [API Reference](#-api-reference) | [Configuration](#-configuration)

---

## 🔌 API Reference

Complete REST API documentation for the Quiz Application.

### 📍 Base URL

```
http://localhost:8080/api
```

### 🔐 Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```http
Authorization: Bearer <jwt_token>
```

---

### 🔑 Authentication Endpoints

#### 1. Faculty Login

**POST** `/api/auth/faculty/login`

Authenticate faculty member and receive JWT token.

**Request:**
```json
{
  "username": "faculty@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "faculty@example.com",
    "role": "FACULTY"
  },
  "expiresIn": 3600
}
```

**Status Codes:**
- `200 OK` - Authentication successful
- `401 Unauthorized` - Invalid credentials
- `400 Bad Request` - Missing fields

---

#### 2. Student Registration

**POST** `/api/auth/student/register`

Register a new student (no authentication required).

**Request:**
```json
{
  "name": "John Doe",
  "enrollmentNumber": "2021001"
}
```

**Response:**
```json
{
  "success": true,
  "studentId": "abc123def456",
  "message": "Registration successful"
}
```

**Status Codes:**
- `201 Created` - Registration successful
- `409 Conflict` - Enrollment number already exists
- `400 Bad Request` - Invalid input

---

### 📝 Question Generation Endpoints

#### 3. Generate Questions

**POST** `/api/questions/generate`

Generate quiz questions from syllabus content.

**Authentication:** Required (Faculty only)

**Request:**
```json
{
  "syllabusContent": "Java is a high-level programming language...",
  "questionCount": 10,
  "difficulty": "MEDIUM"
}
```

**Or with PDF (Base64):**
```json
{
  "syllabusFile": "data:application/pdf;base64,JVBERi0xLjQK...",
  "questionCount": 15,
  "difficulty": "HARD"
}
```

**Response:**
```json
{
  "success": true,
  "questions": [
    {
      "id": 1,
      "questionText": "What is Java?",
      "optionA": "A programming language",
      "optionB": "A coffee brand",
      "optionC": "An operating system",
      "optionD": "A database",
      "correctAnswer": "A",
      "difficulty": "EASY"
    }
  ],
  "totalGenerated": 10
}
```

**Status Codes:**
- `200 OK` - Questions generated successfully
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not a faculty member
- `400 Bad Request` - Invalid syllabus or count

---

### 🎮 Session Management Endpoints

#### 4. Start Quiz Session

**POST** `/api/session/start`

Start a new quiz session with generated questions.

**Authentication:** Required (Faculty only)

**Request:**
```json
{
  "questions": [
    {
      "questionText": "What is Java?",
      "optionA": "A programming language",
      "optionB": "A coffee brand",
      "optionC": "An operating system",
      "optionD": "A database",
      "correctAnswer": "A"
    }
  ],
  "duration": 30,
  "allowReview": true,
  "shuffleQuestions": false
}
```

**Response:**
```json
{
  "success": true,
  "sessionId": "ABC123",
  "otp": "456789",
  "questionCount": 10,
  "duration": 30,
  "createdAt": "2025-11-23T10:00:00Z",
  "expiresAt": "2025-11-23T11:00:00Z"
}
```

**Status Codes:**
- `201 Created` - Session created successfully
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not a faculty member
- `400 Bad Request` - Invalid questions

---

#### 5. Join Quiz Session

**POST** `/api/session/join`

Student joins an active quiz session.

**Request:**
```json
{
  "sessionId": "ABC123",
  "otp": "456789",
  "studentName": "John Doe",
  "enrollmentNumber": "2021001"
}
```

**Response:**
```json
{
  "success": true,
  "sessionToken": "student_session_token_here",
  "questions": [
    {
      "id": 1,
      "questionText": "What is Java?",
      "optionA": "A programming language",
      "optionB": "A coffee brand",
      "optionC": "An operating system",
      "optionD": "A database"
    }
  ],
  "duration": 30,
  "startTime": "2025-11-23T10:05:00Z"
}
```

> **Note:** `correctAnswer` is not included in the response.

**Status Codes:**
- `200 OK` - Successfully joined
- `404 Not Found` - Invalid Session ID
- `401 Unauthorized` - Invalid OTP
- `410 Gone` - Session expired

---

#### 6. Get Session Details

**GET** `/api/session/{sessionId}`

Get details of a specific session.

**Authentication:** Required

**Response:**
```json
{
  "sessionId": "ABC123",
  "status": "ACTIVE",
  "facultyName": "Prof. Smith",
  "questionCount": 10,
  "participantCount": 25,
  "startTime": "2025-11-23T10:00:00Z",
  "expiresAt": "2025-11-23T11:00:00Z"
}
```

**Status Codes:**
- `200 OK` - Session found
- `404 Not Found` - Session doesn't exist
- `401 Unauthorized` - Not authenticated

---

### 📤 Submission Endpoints

#### 7. Submit Quiz Answers

**POST** `/api/submission/submit`

Student submits quiz answers.

**Authentication:** Required (Session token)

**Request:**
```json
{
  "sessionId": "ABC123",
  "answers": {
    "1": "A",
    "2": "B",
    "3": "C",
    "4": "A",
    "5": "D"
  },
  "timeSpent": 1234
}
```

**Response:**
```json
{
  "success": true,
  "submissionId": "sub_12345",
  "score": 8,
  "totalQuestions": 10,
  "percentage": 80,
  "correctAnswers": 8,
  "wrongAnswers": 2,
  "rank": 3,
  "submittedAt": "2025-11-23T10:25:30Z",
  "feedback": "Great job!"
}
```

**Status Codes:**
- `200 OK` - Submission successful
- `400 Bad Request` - Invalid answers format
- `409 Conflict` - Already submitted
- `410 Gone` - Session expired

---

#### 8. Get Submission Details

**GET** `/api/submission/{submissionId}`

Retrieve details of a specific submission.

**Authentication:** Required

**Response:**
```json
{
  "submissionId": "sub_12345",
  "studentName": "John Doe",
  "enrollmentNumber": "2021001",
  "score": 8,
  "totalQuestions": 10,
  "percentage": 80,
  "timeSpent": 1234,
  "submittedAt": "2025-11-23T10:25:30Z",
  "answers": [
    {
      "questionId": 1,
      "questionText": "What is Java?",
      "selectedAnswer": "A",
      "correctAnswer": "A",
      "isCorrect": true
    }
  ]
}
```

---

### 📊 Scoreboard Endpoints

#### 9. Get Live Scoreboard

**GET** `/api/scoreboard/{sessionId}`

Get real-time scoreboard for a session.

**Authentication:** Required (Faculty only)

**Response:**
```json
{
  "sessionId": "ABC123",
  "participants": [
    {
      "rank": 1,
      "studentName": "John Doe",
      "enrollmentNumber": "2021001",
      "score": 9,
      "percentage": 90,
      "status": "SUBMITTED",
      "submittedAt": "2025-11-23T10:20:15Z"
    },
    {
      "rank": 2,
      "studentName": "Jane Smith",
      "enrollmentNumber": "2021002",
      "score": 8,
      "percentage": 80,
      "status": "SUBMITTED",
      "submittedAt": "2025-11-23T10:22:30Z"
    },
    {
      "rank": null,
      "studentName": "Bob Johnson",
      "enrollmentNumber": "2021003",
      "score": null,
      "percentage": null,
      "status": "IN_PROGRESS",
      "submittedAt": null
    }
  ],
  "statistics": {
    "totalParticipants": 30,
    "submitted": 25,
    "inProgress": 5,
    "averageScore": 7.5,
    "highestScore": 10,
    "lowestScore": 4
  },
  "lastUpdated": "2025-11-23T10:25:00Z"
}
```

**Status Codes:**
- `200 OK` - Scoreboard retrieved
- `404 Not Found` - Session not found
- `403 Forbidden` - Not authorized

---

#### 10. Export Results

**GET** `/api/scoreboard/{sessionId}/export?format=csv`

Export session results in various formats.

**Authentication:** Required (Faculty only)

**Query Parameters:**
- `format` - Export format: `csv`, `json`, or `pdf`

**Response (CSV):**
```csv
Rank,Name,Enrollment,Score,Percentage,Submitted At
1,John Doe,2021001,9,90%,2025-11-23 10:20:15
2,Jane Smith,2021002,8,80%,2025-11-23 10:22:30
```

**Status Codes:**
- `200 OK` - Export successful
- `404 Not Found` - Session not found
- `403 Forbidden` - Not authorized

---

### 🔧 Utility Endpoints

#### 11. Health Check

**GET** `/api/health`

Check if the API is running.

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2025-11-23T10:00:00Z",
  "version": "1.0.0"
}
```

---

#### 12. Get Question Statistics

**GET** `/api/questions/{questionId}/stats`

Get statistics for a specific question.

**Authentication:** Required (Faculty only)

**Response:**
```json
{
  "questionId": 1,
  "questionText": "What is Java?",
  "totalAttempts": 30,
  "correctAnswers": 25,
  "wrongAnswers": 5,
  "accuracy": 83.33,
  "optionDistribution": {
    "A": 25,
    "B": 3,
    "C": 1,
    "D": 1
  }
}
```

---

### 📝 Error Responses

All error responses follow this format:

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": "Additional error details (optional)"
  },
  "timestamp": "2025-11-23T10:00:00Z"
}
```

**Common Error Codes:**

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `AUTH_REQUIRED` | 401 | Authentication required |
| `INVALID_TOKEN` | 401 | JWT token invalid or expired |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `ALREADY_EXISTS` | 409 | Resource already exists |
| `VALIDATION_ERROR` | 400 | Input validation failed |
| `SESSION_EXPIRED` | 410 | Quiz session expired |
| `SERVER_ERROR` | 500 | Internal server error |

---

### 🔄 Rate Limiting

| Endpoint Type | Rate Limit |
|---------------|------------|
| Authentication | 5 requests/minute |
| Question Generation | 10 requests/hour |
| Submissions | 1 request/minute per student |
| Scoreboard | 100 requests/minute |
| Other endpoints | 60 requests/minute |

---

### 💡 Usage Examples

#### Using cURL

```bash
# Faculty Login
curl -X POST http://localhost:8080/api/auth/faculty/login \
  -H "Content-Type: application/json" \
  -d '{"username":"faculty@example.com","password":"password"}'

# Generate Questions (with token)
curl -X POST http://localhost:8080/api/questions/generate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"syllabusContent":"Java programming basics...","questionCount":10}'
```

#### Using JavaScript (Fetch API)

```javascript
// Student joins session
const response = await fetch('http://localhost:8080/api/session/join', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    sessionId: 'ABC123',
    otp: '456789',
    studentName: 'John Doe',
    enrollmentNumber: '2021001'
  })
});

const data = await response.json();
console.log(data);
```

---

**Related Pages:** [Architecture][def] | [Project Structure](#-project-structure) |[Troubleshooting](#-troubleshooting)

---

## 🔧 Troubleshooting

Common issues and their solutions.

### 🚨 Installation Issues

<details>
<summary><strong>❌ Java version mismatch error</strong></summary>

**Error:**
```
Unsupported class file major version XX
```

**Solution:**
1. Check Java version:
   ```bash
   java -version
   ```
2. Ensure Java 17 or higher is installed
3. Update `JAVA_HOME` environment variable
4. Restart terminal/IDE

</details>

<details>
<summary><strong>❌ Maven not found</strong></summary>

**Error:**
```
'mvn' is not recognized as an internal or external command
```

**Solution:**
1. Verify Maven installation:
   ```bash
   mvn -version
   ```
2. Add Maven to PATH:
   - Windows: `%MAVEN_HOME%\bin`
   - Linux/Mac: `/usr/local/maven/bin`
3. Restart terminal

</details>

<details>
<summary><strong>❌ Build fails with dependency errors</strong></summary>

**Error:**
```
Could not resolve dependencies for project
```

**Solution:**
```bash
# Clear Maven cache
mvn clean

# Update dependencies
mvn clean install -U

# Force update
mvn clean install -U -DskipTests
```

</details>

---

### 🖥️ Backend Issues

<details>
<summary><strong>❌ Port 8080 already in use</strong></summary>

**Error:**
```
Port 8080 was already in use
```

**Solution 1:** Kill process using port 8080

**Windows:**
```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**macOS/Linux:**
```bash
lsof -i :8080
kill -9 <PID>
```

**Solution 2:** Change port in `application.properties`
```properties
server.port=8081
```

</details>

<details>
<summary><strong>❌ Database connection error</strong></summary>

**Error:**
```
Unable to open JDBC Connection
```

**Solution:**
1. Check H2 database configuration
2. Verify database file permissions
3. Delete `data/` folder and restart:
   ```bash
   rm -rf data/
   mvn spring-boot:run
   ```

</details>

<details>
<summary><strong>❌ Application fails to start</strong></summary>

**Error:**
```
APPLICATION FAILED TO START
```

**Solution:**
1. Check logs in console
2. Verify all dependencies are installed
3. Ensure Java 17+ is being used
4. Clean and rebuild:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

</details>

---

### 🖼️ JavaFX Client Issues

<details>
<summary><strong>❌ JavaFX runtime components missing</strong></summary>

**Error:**
```
Error: JavaFX runtime components are missing
```

**Solution:**
1. Use Maven command instead of direct java:
   ```bash
   mvn javafx:run
   ```
2. Don't use `mvn exec:java`
3. Verify `pom.xml` has JavaFX dependencies

</details>

<details>
<summary><strong>❌ Cannot connect to backend</strong></summary>

**Error:**
```
Connection refused: connect
```

**Solution:**
1. Ensure backend server is running
2. Check backend URL in client config
3. Verify port number (default: 8080)
4. Check firewall settings

</details>

<details>
<summary><strong>❌ UI not displaying correctly</strong></summary>

**Symptoms:** Blank window, missing elements

**Solution:**
1. Check CSS files are loaded
2. Verify FXML files exist
3. Clear JavaFX cache:
   ```bash
   rm -rf ~/.m2/repository/org/openjfx
   mvn clean install
   ```

</details>

---

### 🌐 Web Client Issues

<details>
<summary><strong>❌ Cannot access from mobile device</strong></summary>

**Problem:** `http://localhost:8080` not working on phone

**Solution:**
1. Ensure both devices are on same WiFi network
2. Find computer's IP address:
   ```bash
   # Windows
   ipconfig
   
   # Mac/Linux
   ifconfig
   ```
3. Use IP address instead:
   ```
   http://192.168.1.5:8080
   ```
4. Check firewall allows port 8080

</details>

<details>
<summary><strong>❌ 404 Not Found on web client</strong></summary>

**Error:** Page not found

**Solution:**
1. Verify backend is running
2. Check static files in `backend/src/main/resources/static/`
3. Clear browser cache (Ctrl+Shift+Del)
4. Try different browser

</details>

<details>
<summary><strong>❌ JavaScript errors in console</strong></summary>

**Symptoms:** Features not working, console errors

**Solution:**
1. Open browser DevTools (F12)
2. Check Console tab for errors
3. Verify API calls are successful
4. Check Network tab for failed requests
5. Clear browser cache

</details>

---

### 🎮 Session & Quiz Issues

<details>
<summary><strong>❌ Invalid Session ID or OTP</strong></summary>

**Error:** Cannot join session

**Solution:**
1. Verify Session ID is correct (case-sensitive)
2. Check OTP hasn't expired
3. Ensure faculty started session
4. Try creating new session

</details>

<details>
<summary><strong>❌ Questions not generating</strong></summary>

**Problem:** AI fails to generate questions

**Solution:**
1. Check syllabus content is clear and structured
2. Verify PDF is not corrupted
3. Try plain text instead of PDF
4. Check backend logs for errors:
   ```bash
   tail -f logs/quiz-app.log
   ```
5. Ensure AI service is configured

</details>

<details>
<summary><strong>❌ Scoreboard not updating</strong></summary>

**Problem:** Scores not showing in real-time

**Solution:**
1. Refresh scoreboard manually
2. Check auto-refresh is enabled
3. Verify students submitted answers
4. Check backend logs
5. Restart faculty dashboard

</details>

---

### 🔐 Authentication Issues

<details>
<summary><strong>❌ Invalid credentials</strong></summary>

**Error:** Login failed

**Solution:**
1. Check username and password
2. Verify faculty account exists
3. Reset password in database
4. Check JWT configuration

</details>

<details>
<summary><strong>❌ Token expired</strong></summary>

**Error:** Unauthorized access

**Solution:**
1. Log in again to get new token
2. Increase token expiration in config:
   ```properties
   jwt.expiration=7200000
   ```
3. Clear session and login again

</details>

---

### 📱 Performance Issues

<details>
<summary><strong>⚠️ Slow application performance</strong></summary>

**Solution:**
1. Increase JVM memory:
   ```bash
   export MAVEN_OPTS="-Xmx2g"
   mvn spring-boot:run
   ```
2. Optimize database queries
3. Enable caching
4. Reduce question count
5. Close unused applications

</details>

<details>
<summary><strong>⚠️ High memory usage</strong></summary>

**Solution:**
1. Monitor with Task Manager/Activity Monitor
2. Restart application periodically
3. Limit concurrent sessions
4. Increase system RAM
5. Use production profile with optimizations

</details>

---

### 🔍 Debugging Tips

#### Enable Debug Logging

```properties
# application.properties
logging.level.com.quiz=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### View H2 Database

```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/quizdb
```

#### Check API Health

```bash
curl http://localhost:8080/api/health
```

#### View Application Logs

```bash
# Real-time logs
tail -f logs/quiz-app.log

# Search for errors
grep ERROR logs/quiz-app.log
```

---

### 🆘 Getting Help

If you still face issues:

1. **📖 Check Documentation:** Review relevant wiki pages
2. **🔍 Search Issues:** Check [GitHub Issues](https://github.com/Daku3011/Quiz/issues)
3. **🐛 Report Bug:** Create detailed issue with:
   - Steps to reproduce
   - Error messages
   - System information
   - Screenshots
4. **💬 Ask Community:** Use GitHub Discussions

---

### 📋 System Information Template

When reporting issues, include:

```
OS: [Windows 10 / macOS 12 / Ubuntu 22.04]
Java Version: [java -version output]
Maven Version: [mvn -version output]
Browser: [Chrome 120 / Firefox 121]
Error Message: [Full error text]
Steps to Reproduce: [Numbered steps]
Expected Behavior: [What should happen]
Actual Behavior: [What actually happens]
```

---

**Related Pages:** [Installation][#-installation-guide] | [Configuration](#-configuration) | [FAQ](#-frequently-asked-questions-faq)

---

## ⚙️ Configuration

Customize the Quiz Application to meet your requirements.

### 🔧 Backend Configuration

Configuration file: `backend/src/main/resources/application.properties`

#### Server Settings

```properties
# Server Port
server.port=8080

# Server Address (0.0.0.0 for all interfaces)
server.address=0.0.0.0

# Context Path (optional)
# server.servlet.context-path=/quiz

# Session Timeout (in seconds)
server.servlet.session.timeout=1800
```

---

#### Database Configuration

```properties
# H2 Database Settings
spring.datasource.url=jdbc:h2:file:./data/quizdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=admin
spring.datasource.password=admin123

# JPA Settings
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console (for debugging)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**H2 Console Access:**
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/quizdb
Username: admin
Password: admin123
```

---

#### File Upload Configuration

```properties
# Maximum file size
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# File upload directory
file.upload.dir=./uploads

# Allowed file types
file.allowed.extensions=txt,pdf
```

---

#### Security Configuration

```properties
# JWT Settings
jwt.secret=yourSecretKeyHere123456789012345678901234567890
jwt.expiration=3600000

# CORS Settings
cors.allowed.origins=http://localhost:3000,http://localhost:8080
cors.allowed.methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed.headers=*
```

> ⚠️ **Security Note:** Change `jwt.secret` to a strong, unique value in production!

---

#### AI Service Configuration

```properties
# AI Service Type (mock, openai, gemini)
ai.service.type=mock

# OpenAI Settings (if using OpenAI)
ai.openai.api-key=your_openai_api_key_here
ai.openai.model=gpt-3.5-turbo
ai.openai.max-tokens=1000

# Gemini Settings (if using Gemini)
ai.gemini.api-key=your_gemini_api_key_here
ai.gemini.model=gemini-pro
```

---

#### Logging Configuration

```properties
# Log Level
logging.level.root=INFO
logging.level.com.quiz=DEBUG
logging.level.org.springframework.web=DEBUG

# Log File
logging.file.name=./logs/quiz-app.log
logging.file.max-size=10MB
logging.file.max-history=7
```

---

### 🎨 Client Configuration

#### JavaFX Application Settings

File: `client/src/main/resources/config.properties`

```properties
# Backend API URL
api.base.url=http://localhost:8080/api

# Connection Timeout (milliseconds)
api.connection.timeout=5000

# Read Timeout (milliseconds)
api.read.timeout=10000

# UI Theme (light, dark)
ui.theme=light

# Window Size
ui.window.width=1200
ui.window.height=800

# Auto-refresh Scoreboard (seconds)
scoreboard.refresh.interval=5
```

---

#### Web Client Configuration

File: `backend/src/main/resources/static/js/config.js`

```javascript
const CONFIG = {
  // API Base URL
  API_BASE_URL: '/api',
  
  // Timeouts
  REQUEST_TIMEOUT: 10000,
  REFRESH_INTERVAL: 5000,
  
  // UI Settings
  QUESTIONS_PER_PAGE: 1,
  ENABLE_ANIMATIONS: true,
  
  // Quiz Settings
  SHOW_TIMER: true,
  ALLOW_REVIEW: true,
  AUTO_SUBMIT: false
};
```

---

### 🎯 Session Configuration

#### Session Settings

```properties
# Session ID Length
session.id.length=6

# OTP Length
session.otp.length=6

# Session Expiration (minutes)
session.expiration=60

# Maximum Participants per Session
session.max.participants=100

# Allow Multiple Attempts
session.allow.multiple.attempts=false
```

---

### 📊 Quiz Configuration

#### Question Settings

```properties
# Default Question Count
quiz.default.question.count=10

# Maximum Question Count
quiz.max.question.count=50

# Question Types
quiz.question.types=MULTIPLE_CHOICE,TRUE_FALSE

# Shuffle Questions
quiz.shuffle.questions=false

# Shuffle Options
quiz.shuffle.options=false
```

---

### 📧 Notification Configuration

```properties
# Enable Email Notifications
notification.email.enabled=false

# SMTP Settings
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

### 🔐 Advanced Security Configuration

#### Spring Security Configuration

File: `backend/src/main/java/com/quiz/security/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/session/join").permitAll()
                .requestMatchers("/api/faculty/**").hasRole("FACULTY")
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
}
```

---

### 🎨 Styling Configuration

#### JavaFX CSS

File: `client/src/main/resources/css/theme.css`

```css
/* Color Scheme */
:root {
    -fx-primary-color: #2196F3;
    -fx-secondary-color: #FFC107;
    -fx-success-color: #4CAF50;
    -fx-danger-color: #F44336;
    -fx-text-color: #212121;
    -fx-background-color: #FFFFFF;
}

/* Custom Styles */
.quiz-button {
    -fx-background-color: -fx-primary-color;
    -fx-text-fill: white;
    -fx-padding: 10 20;
    -fx-font-size: 14px;
}
```

---

### 📱 Mobile Configuration

#### Responsive Breakpoints

File: `backend/src/main/resources/static/css/mobile.css`

```css
/* Mobile Devices */
@media (max-width: 767px) {
    .quiz-container {
        padding: 10px;
        font-size: 14px;
    }
}

/* Tablets */
@media (min-width: 768px) and (max-width: 1023px) {
    .quiz-container {
        padding: 20px;
        font-size: 16px;
    }
}

/* Desktop */
@media (min-width: 1024px) {
    .quiz-container {
        padding: 30px;
        font-size: 18px;
    }
}
```

---

### 🔄 Environment-Specific Configuration

#### Development Environment

File: `application-dev.properties`

```properties
spring.profiles.active=dev
spring.jpa.show-sql=true
logging.level.com.quiz=DEBUG
ai.service.type=mock
```

#### Production Environment

File: `application-prod.properties`

```properties
spring.profiles.active=prod
spring.jpa.show-sql=false
logging.level.com.quiz=INFO
ai.service.type=openai
server.port=80
```

**Run with specific profile:**
```bash
# Development
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

### 💾 Backup Configuration

```properties
# Automatic Backup
backup.enabled=true
backup.directory=./backups
backup.schedule.cron=0 0 2 * * ?
backup.retention.days=7
```

---

### 📊 Performance Tuning

```properties
# Thread Pool
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10

# Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000

# Cache Settings
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=10m
```

---

### ⚡ Quick Configuration Examples

#### Example 1: Change Port

```properties
server.port=9090
```

Restart server, access at: `http://localhost:9090`

#### Example 2: Enable H2 Console

```properties
spring.h2.console.enabled=true
```

Access at: `http://localhost:8080/h2-console`

#### Example 3: Increase File Upload Limit

```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

---

**Related Pages:** [Installation][#-installation-guide] | [Getting Started](#-getting-started) | [Troubleshooting](#-troubleshooting)

---

## 🤝 Contributing

We welcome contributions from the community! Here's how you can help make the Quiz Application better.

### 🌟 Ways to Contribute

| Type | Description | Skill Level |
|------|-------------|-------------|
| 🐛 **Bug Reports** | Report issues you encounter | Beginner |
| 💡 **Feature Requests** | Suggest new features | Beginner |
| 📖 **Documentation** | Improve docs and wiki | Beginner |
| 🎨 **UI/UX** | Enhance user interface | Intermediate |
| 🔧 **Bug Fixes** | Fix reported issues | Intermediate |
| ✨ **New Features** | Implement new functionality | Advanced |
| 🧪 **Testing** | Write unit/integration tests | Intermediate |

---

### 🚀 Getting Started

#### 1️⃣ Fork the Repository

Click the "Fork" button on [GitHub](https://github.com/Daku3011/Quiz)

#### 2️⃣ Clone Your Fork

```bash
git clone https://github.com/YOUR_USERNAME/Quiz.git
cd Quiz
```

#### 3️⃣ Add Upstream Remote

```bash
git remote add upstream https://github.com/Daku3011/Quiz.git
```

#### 4️⃣ Create a Branch

```bash
# For features
git checkout -b feature/your-feature-name

# For bug fixes
git checkout -b fix/bug-description

# For documentation
git checkout -b docs/doc-description
```

**Branch Naming Convention:**
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Adding tests
- `style/` - UI/styling changes

---

### 💻 Development Workflow

#### 1. Make Your Changes

```bash
# Edit files
nano backend/src/main/java/com/quiz/...

# Test your changes
mvn test
```

#### 2. Follow Code Style

**Java Code Style:**
- Use 4 spaces for indentation
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Add JavaDoc for public methods
- Keep methods under 50 lines

**Example:**
```java
/**
 * Generates quiz questions from syllabus content.
 * 
 * @param syllabusContent The input syllabus text
 * @param questionCount Number of questions to generate
 * @return List of generated questions
 * @throws InvalidInputException if syllabus is empty
 */
public List<Question> generateQuestions(
    String syllabusContent, 
    int questionCount
) throws InvalidInputException {
    // Implementation
}
```

**JavaScript Code Style:**
- Use 2 spaces for indentation
- Use `const` and `let`, avoid `var`
- Use arrow functions
- Add JSDoc comments

---

#### 3. Write Tests

**Backend Tests (JUnit):**
```java
@Test
public void testQuestionGeneration() {
    String syllabus = "Java is a programming language";
    List<Question> questions = service.generateQuestions(syllabus, 5);
    
    assertNotNull(questions);
    assertEquals(5, questions.size());
}
```

**Run Tests:**
```bash
# Backend tests
cd backend
mvn test

# Client tests
cd client
mvn test
```

---

#### 4. Commit Your Changes

**Commit Message Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting, missing semicolons
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance

**Examples:**
```bash
git commit -m "feat(backend): add support for multiple choice questions"
git commit -m "fix(client): resolve scoreboard refresh issue"
git commit -m "docs(wiki): update installation instructions"
```

---

#### 5. Push to Your Fork

```bash
git push origin feature/your-feature-name
```

---

### 📬 Submitting a Pull Request

#### 1. Create Pull Request

1. Go to your fork on GitHub
2. Click "Pull Request" button
3. Select base: `main` ← compare: `your-branch`
4. Fill in the PR template

#### 2. PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Code refactoring

## Testing
- [ ] All tests pass locally
- [ ] Added new tests for changes
- [ ] Tested manually

## Checklist
- [ ] Code follows project style guidelines
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No breaking changes

## Screenshots (if applicable)
[Add screenshots here]

## Related Issues
Closes #123
```

#### 3. Code Review Process

1. **Automated Checks:** CI/CD runs tests
2. **Review:** Maintainers review your code
3. **Changes:** Address review comments if needed
4. **Approval:** Once approved, PR will be merged
5. **Merge:** Your contribution is now part of the project! 🎉

---

### 🐛 Reporting Issues

#### Before Creating an Issue

1. **Search Existing Issues:** Check if already reported
2. **Update to Latest:** Ensure you're on latest version
3. **Reproduce:** Verify the issue is reproducible

#### Issue Template

**Bug Report:**
```markdown
**Description:**
Clear description of the bug

**Steps to Reproduce:**
1. Go to '...'
2. Click on '...'
3. See error

**Expected Behavior:**
What should happen

**Actual Behavior:**
What actually happens

**Environment:**
- OS: [e.g., Windows 10]
- Java Version: [e.g., 17.0.1]
- Browser: [e.g., Chrome 120]

**Screenshots:**
[If applicable]

**Additional Context:**
Any other relevant information
```

**Feature Request:**
```markdown
**Feature Description:**
Clear description of the feature

**Use Case:**
Why this feature would be useful

**Proposed Solution:**
How you think it should work

**Alternatives Considered:**
Other approaches you've thought about

**Additional Context:**
Mockups, examples, etc.
```

---

### 📚 Documentation Contributions

#### Wiki Pages

Help improve our wiki:

1. **Fix Typos:** Correct spelling/grammar errors
2. **Add Examples:** Provide code examples
3. **Clarify Instructions:** Make steps clearer
4. **Add Screenshots:** Visual aids help understanding
5. **Translate:** Help with internationalization

#### Code Comments

```java
// ❌ Bad comment
int x = 5; // set x to 5

// ✅ Good comment
// Calculate maximum allowed questions based on syllabus length
// to prevent excessive AI processing time
int maxQuestions = calculateMaxQuestions(syllabusLength);
```

---

### 🎨 UI/UX Contributions

#### Design Guidelines

- **Consistency:** Follow existing design patterns
- **Accessibility:** Ensure WCAG 2.1 compliance
- **Responsive:** Test on multiple screen sizes
- **Performance:** Optimize images and animations

#### Submitting Design Changes

1. Create mockups/prototypes
2. Share in GitHub Discussions
3. Get feedback from community
4. Implement approved designs

---

### 🧪 Testing Contributions

Help improve code quality:

**Unit Tests:**
```java
@Test
public void testSessionCreation() {
    Session session = sessionService.createSession(questions);
    assertNotNull(session.getSessionId());
    assertEquals(6, session.getSessionId().length());
}
```

**Integration Tests:**
```java
@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testStartSession() throws Exception {
        mockMvc.perform(post("/api/session/start")
            .contentType(MediaType.APPLICATION_JSON)
            .content(sessionJson))
            .andExpect(status().isCreated());
    }
}
```

---

### 📋 Development Checklist

Before submitting PR:

- [ ] Code compiles without errors
- [ ] All tests pass (`mvn test`)
- [ ] No new warnings introduced
- [ ] Code follows style guidelines
- [ ] Documentation updated
- [ ] Commit messages follow convention
- [ ] Branch is up-to-date with main
- [ ] Changes are focused and atomic
- [ ] No commented-out code
- [ ] No debug statements (console.log, System.out)

---

### 🏆 Recognition

Contributors will be:
- Listed in [CONTRIBUTORS.md](https://github.com/Daku3011/Quiz/blob/main/CONTRIBUTORS.md)
- Mentioned in release notes
- Given credit in documentation
- Recognized in commit history

---

### ❓ Questions?

- 💬 **Discussions:** [GitHub Discussions](https://github.com/Daku3011/Quiz/discussions)
- 🐛 **Issues:** [GitHub Issues](https://github.com/Daku3011/Quiz/issues)
- 📧 **Email:** Contact maintainers through GitHub

---

### 📜 Code of Conduct

We follow the [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/). Please be respectful and inclusive.

**Key Points:**
- Be welcoming and friendly
- Respect differing viewpoints
- Accept constructive criticism
- Focus on what's best for the community

---

### 🙏 Thank You!

Every contribution, no matter how small, makes this project better. We appreciate your time and effort! 

**Happy Contributing! 🎉**

---

**Related Pages:** [Project Structure](#project-structure) | [API Reference](#-api-reference) | [Troubleshooting](#-troubleshooting)

---

## ❓ Frequently Asked Questions (FAQ)

### 📘 General Questions

<details>
<summary><strong>What is the Quiz Application?</strong></summary>

The Quiz Application is a hybrid platform that combines a JavaFX desktop application for faculty management with a web-based interface for student participation. It features AI-powered question generation, real-time scoring, and multi-platform support.
</details>

<details>
<summary><strong>Is this application free to use?</strong></summary>

Yes! The Quiz Application is open-source and completely free to use, modify, and distribute.
</details>

<details>
<summary><strong>What platforms are supported?</strong></summary>

- **Faculty:** Windows, macOS, Linux (JavaFX Desktop App)
- **Students:** 
  - Desktop: Windows, macOS, Linux (JavaFX App)
  - Mobile/Web: Any device with a modern browser (Chrome, Firefox, Safari, Edge)
</details>

<details>
<summary><strong>Do I need internet to use this application?</strong></summary>

No, the application works on a local network. Internet is only needed if you want to use AI features with external APIs (OpenAI/Gemini) in future versions.
</details>

---

### 🔧 Installation & Setup

<details>
<summary><strong>What are the system requirements?</strong></summary>

**Minimum:**
- Java 17 or higher
- Maven 3.6+
- 4 GB RAM
- 500 MB free storage

**Recommended:**
- Java 17+
- Maven 3.8+
- 8 GB RAM
- 1 GB free storage
</details>

<details>
<summary><strong>How long does installation take?</strong></summary>

Typically 10-15 minutes:
- Java installation: ~5 minutes
- Maven installation: ~3 minutes
- Project download & build: ~5-7 minutes
</details>

<details>
<summary><strong>Can I install on a Raspberry Pi?</strong></summary>

Yes, but performance may vary. Recommended for small groups (≤20 students). Ensure you have:
- Raspberry Pi 4 (4GB+ RAM)
- 64-bit OS
- Java 17 ARM version
</details>

---

### 👨‍🏫 Faculty Questions

<details>
<summary><strong>How many students can join a single session?</strong></summary>

Default limit is 100 concurrent students. This can be increased in configuration for more powerful servers.
</details>

<details>
<summary><strong>Can I create quizzes without AI generation?</strong></summary>

Yes! You can:
- Manually create questions
- Import from existing files
- Mix AI-generated and manual questions
</details>

<details>
<summary><strong>How does AI generate questions?</strong></summary>

Currently uses a mock AI service. Future versions will support:
- OpenAI GPT models
- Google Gemini
- Custom AI endpoints

The AI analyzes syllabus content and creates multiple-choice questions based on key concepts.
</details>

<details>
<summary><strong>Can I reuse questions?</strong></summary>

Currently, questions are session-specific. A question bank feature is planned for future releases where you can save and reuse questions.
</details>

<details>
<summary><strong>Can I see who hasn't submitted yet?</strong></summary>

Yes! The live scoreboard shows:
- ✅ Submitted students (with scores)
- ⏳ In-progress students
- ❌ Not started students
</details>

<details>
<summary><strong>Can I export quiz results?</strong></summary>

Yes! Export options include:
- CSV (for Excel/Google Sheets)
- PDF (for printing)
- JSON (for data processing)
</details>

<details>
<summary><strong>How long does a session stay active?</strong></summary>

Default: 60 minutes from creation. This can be configured in `application.properties`:
```properties
session.expiration=90  # 90 minutes
```
</details>

---

### 👨‍🎓 Student Questions

<details>
<summary><strong>Can I take the quiz on my phone?</strong></summary>

Yes! Use the web client by opening your browser and navigating to the URL provided by your faculty (e.g., `http://192.168.1.5:8080`).
</details>

<details>
<summary><strong>What if I lose internet connection during the quiz?</strong></summary>

Your progress is automatically saved. Rejoin using the same Session ID and OTP to continue where you left off.
</details>

<details>
<summary><strong>Can I change my answers before submitting?</strong></summary>

Yes! You can navigate between questions and change answers until you click "Submit Quiz".
</details>

<details>
<summary><strong>Will I see the correct answers after submitting?</strong></summary>

This depends on your faculty's settings. Some quizzes show correct answers immediately, others don't.
</details>

<details>
<summary><strong>Can I take the quiz multiple times?</strong></summary>

No, currently only one attempt per session is allowed. Each student can submit only once.
</details>

<details>
<summary><strong>Is my data secure?</strong></summary>

Yes! All communication uses secure protocols, and sessions are protected with OTP verification. Data is stored locally on the server.
</details>

---

### 🔐 Security & Privacy

<details>
<summary><strong>How is student data protected?</strong></summary>

- OTP-based session access
- JWT token authentication
- Local database (no cloud storage)
- Encrypted communication
- No personal data collected beyond name and enrollment number
</details>

<details>
<summary><strong>Can students see each other's answers?</strong></summary>

No. Students only see their own questions and scores. Faculty can view all submissions.
</details>

<details>
<summary><strong>Where is data stored?</strong></summary>

All data is stored locally in an H2 database file (`./data/quizdb`) on the server machine. No data is sent to external servers (except AI API if configured).
</details>

---

### 🛠️ Technical Questions

<details>
<summary><strong>Which database does it use?</strong></summary>

H2 embedded database by default. Can be configured to use:
- PostgreSQL
- MySQL
- MariaDB
- Any JDBC-compatible database
</details>

<details>
<summary><strong>Can I use this in production?</strong></summary>

Yes, but consider:
- Using a production database (PostgreSQL/MySQL)
- Implementing proper security measures
- Setting up backups
- Using production profiles
- Deploying on a reliable server
</details>

<details>
<summary><strong>Is it scalable?</strong></summary>

Yes! The architecture supports:
- Horizontal scaling (multiple servers)
- Load balancing
- Database replication
- Caching layers

For very large deployments (500+ concurrent users), consider cloud deployment with proper infrastructure.
</details>

<details>
<summary><strong>Can I integrate with existing systems?</strong></summary>

Yes! The REST API allows integration with:
- Learning Management Systems (LMS)
- Student Information Systems (SIS)
- Authentication systems (LDAP, OAuth)
- Analytics platforms
</details>

<details>
<summary><strong>What ports does it use?</strong></summary>

- Backend API & Web Client: **8080** (configurable)
- H2 Console: **8080/h2-console** (optional)

Ensure these ports are open in your firewall.
</details>

---

### 🎨 Customization

<details>
<summary><strong>Can I customize the UI?</strong></summary>

Yes! Customize by editing:
- JavaFX CSS files: `client/src/main/resources/css/`
- Web CSS files: `backend/src/main/resources/static/css/`
- Color schemes, fonts, layouts are all customizable
</details>

<details>
<summary><strong>Can I change the application name/logo?</strong></summary>

Yes! Update:
- Logo images in `resources/images/`
- Application title in configuration
- Window titles in JavaFX code
</details>

<details>
<summary><strong>Can I add more question types?</strong></summary>

Currently supports multiple choice. Planned additions:
- True/False
- Fill in the blanks
- Short answer
- Essay questions
- Matching questions

Contributions welcome!
</details>

---

### 🚀 Deployment

<details>
<summary><strong>Can I deploy on cloud?</strong></summary>

Yes! Compatible with:
- AWS (EC2, Elastic Beanstalk)
- Google Cloud Platform
- Microsoft Azure
- Heroku
- DigitalOcean
- Any VPS provider
</details>

<details>
<summary><strong>Is Docker support available?</strong></summary>

Docker support is planned for future releases. Meanwhile, you can create your own Dockerfile:

```dockerfile
FROM openjdk:17-jdk-slim
COPY backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
</details>

<details>
<summary><strong>How do I enable HTTPS?</strong></summary>

Configure SSL in `application.properties`:
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your_password
server.ssl.key-store-type=PKCS12
```
</details>

---

### 🔄 Updates & Maintenance

<details>
<summary><strong>How do I update to the latest version?</strong></summary>

```bash
# Backup your data first!
cp -r data/ data-backup/

# Update from repository
git pull origin main

# Rebuild
mvn clean install

# Restart application
```
</details>

<details>
<summary><strong>How often is the project updated?</strong></summary>

Check the [GitHub repository](https://github.com/Daku3011/Quiz) for:
- Latest releases
- Changelog
- Upcoming features
- Security updates
</details>

<details>
<summary><strong>How do I backup my data?</strong></summary>

Simply copy the database folder:
```bash
# Backup
cp -r data/ backups/data-$(date +%Y%m%d)/

# Restore
cp -r backups/data-YYYYMMDD/ data/
```

For automated backups, enable in configuration:
```properties
backup.enabled=true
backup.schedule.cron=0 0 2 * * ?
```
</details>

---

### 🐛 Common Issues

<details>
<summary><strong>Why can't I generate questions?</strong></summary>

Check:
1. Backend server is running
2. Syllabus content is clear and structured
3. AI service is configured
4. Check backend logs for errors
5. Try with sample text first
</details>

<details>
<summary><strong>Students can't connect from mobile - why?</strong></summary>

Ensure:
1. Backend server is running
2. Devices are on same WiFi network
3. Firewall allows port 8080
4. Using correct IP address (not localhost)
5. URL format: `http://192.168.x.x:8080`
</details>

---

### 📖 Learning Resources

<details>
<summary><strong>Where can I learn more?</strong></summary>

**Official Documentation:**
- [[Installation]] - Setup guide
- [[Getting Started]] - Quick start
- [[Faculty Guide]] - For teachers
- [[Student Guide]] - For learners
- [[API Reference]] - For developers

**External Resources:**
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [JavaFX Documentation](https://openjfx.io/)
- [Maven Guide](https://maven.apache.org/guides/)
</details>

---

### 💡 Feature Requests

<details>
<summary><strong>Can I request a new feature?</strong></summary>

Yes! We welcome feature requests:
1. Check [existing issues](https://github.com/Daku3011/Quiz/issues) first
2. Create a new issue with "Feature Request" label
3. Describe the feature and use case
4. Community will discuss and prioritize
</details>

<details>
<summary><strong>What features are planned?</strong></summary>

**Upcoming Features:**
- Real OpenAI/Gemini integration
- Question bank system
- Timed quizzes
- Multiple question types
- Email notifications
- Advanced analytics
- Mobile native apps
- Multi-language support

See [[Contributing#Future-Enhancements]] for full list.
</details>

---

### 📞 Getting Help

**Still have questions?**

1. 📖 **Wiki:** Read detailed guides
2. 🔍 **Search:** Check existing issues
3. 💬 **Discuss:** Use GitHub Discussions
4. 🐛 **Report:** Create an issue
5. 📧 **Contact:** Reach out to maintainers

---

## 🚀 Roadmap & Future Enhancements

### 📅 Version 2.0 (Planned)

#### 🤖 AI Integration
- [ ] OpenAI GPT-3.5/GPT-4 integration
- [ ] Google Gemini integration
- [ ] Custom AI endpoint support
- [ ] Question quality scoring
- [ ] Automatic difficulty assessment

#### 📝 Question Types
- [ ] True/False questions
- [ ] Fill in the blanks
- [ ] Short answer questions
- [ ] Essay questions
- [ ] Matching questions
- [ ] Ordering questions

#### 💾 Question Bank
- [ ] Save questions to bank
- [ ] Categorize by subject/topic
- [ ] Search and filter questions
- [ ] Import/Export question sets
- [ ] Share question banks between faculty

#### ⏱️ Quiz Features
- [ ] Timed quizzes with countdown
- [ ] Question randomization
- [ ] Option shuffling
- [ ] Partial credit scoring
- [ ] Negative marking support
- [ ] Review mode for students

---

### 📅 Version 3.0 (Future)

#### 📊 Advanced Analytics
- [ ] Student performance reports
- [ ] Question difficulty analysis
- [ ] Topic-wise score breakdown
- [ ] Historical trends
- [ ] Comparative analytics
- [ ] Export to Power BI/Tableau

#### 📧 Notifications & Communication
- [ ] Email notifications
- [ ] SMS alerts (optional)
- [ ] In-app notifications
- [ ] Announcement system
- [ ] Discussion forums

#### 🔐 Advanced Security
- [ ] OAuth 2.0 integration
- [ ] LDAP/Active Directory
- [ ] Two-factor authentication
- [ ] Role-based access control
- [ ] Audit logs

#### 🌐 Internationalization
- [ ] Multi-language support
- [ ] RTL (Right-to-Left) layouts
- [ ] Localized date/time formats
- [ ] Currency support for paid features

---

### 📅 Version 4.0 (Long-term Vision)

#### 📱 Mobile Apps
- [ ] Native Android app
- [ ] Native iOS app
- [ ] Offline mode support
- [ ] Push notifications
- [ ] Mobile-optimized UI

#### ☁️ Cloud & Enterprise
- [ ] Cloud deployment templates
- [ ] Multi-tenancy support
- [ ] Horizontal scaling
- [ ] Load balancing
- [ ] CDN integration
- [ ] Enterprise SSO

#### 🎮 Gamification
- [ ] Badges and achievements
- [ ] Leaderboards
- [ ] Points system
- [ ] Streak tracking
- [ ] Challenges and tournaments

#### 🔌 Integrations
- [ ] LMS integration (Moodle, Canvas, Blackboard)
- [ ] Google Classroom sync
- [ ] Microsoft Teams integration
- [ ] Zoom/Webex integration
- [ ] Calendar sync
- [ ] Webhook support

---

### 🎨 UI/UX Improvements

- [ ] Dark mode
- [ ] Customizable themes
- [ ] Accessibility improvements (WCAG 2.1 AAA)
- [ ] Keyboard shortcuts
- [ ] Gesture support for mobile
- [ ] Animated transitions
- [ ] Progressive Web App (PWA)

---

### 🧪 Quality Improvements

- [ ] Comprehensive test coverage (>80%)
- [ ] Performance benchmarking
- [ ] Security audits
- [ ] Automated CI/CD pipeline
- [ ] Docker and Kubernetes support
- [ ] Monitoring and alerting

---

### 📚 Documentation

- [ ] Video tutorials
- [ ] Interactive demos
- [ ] API playground
- [ ] Admin handbook
- [ ] Student handbook
- [ ] Deployment guides

---

**Want to contribute to these features?** Check out our [Contributing](#-contributing) guide!

---

## 📄 License

This project is **open source** and available under the **MIT License**.

```
MIT License

Copyright (c) 2025 Daku3011

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

### ✅ What You Can Do

✓ Use commercially  
✓ Modify  
✓ Distribute  
✓ Private use  
✓ Patent use  

### ❌ What You Can't Do

✗ Hold liable  
✗ Use without attribution (must include copyright notice)

---

## 👥 Credits & Acknowledgments

### 🌟 Core Developers

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Daku3011">
        <img src="https://github.com/Daku3011.png" width="100px;"/><br/>
        <sub><b>Daku3011</b></sub>
      </a><br/>
      Lead Developer
    </td>
    <td align="center">
      <sub><b>Moonshine</b></sub><br/>
      Co-Developer
    </td>
  </tr>
</table>

### 🙏 Special Thanks

**Technologies & Frameworks:**
- ☕ [Spring Boot Team](https://spring.io/) - Excellent backend framework
- 🖼️ [OpenJFX Community](https://openjfx.io/) - JavaFX platform
- 🗄️ [H2 Database](https://www.h2database.com/) - Embedded database
- 📦 [Apache Maven](https://maven.apache.org/) - Build automation
- 📄 [Apache PDFBox](https://pdfbox.apache.org/) - PDF processing

**Libraries & Dependencies:**
- Spring Security - Authentication & Authorization
- Spring Data JPA - Data access
- JWT (JSON Web Tokens) - Secure communication
- Lombok - Reducing boilerplate code

**Community:**
- All [contributors](https://github.com/Daku3011/Quiz/graphs/contributors)
- Issue reporters and testers
- Documentation contributors
- Everyone who provided feedback

---

## 📞 Support & Contact

### 💬 Community Support

| Platform | Purpose | Link |
|----------|---------|------|
| 🐛 **GitHub Issues** | Bug reports, feature requests | [Create Issue](https://github.com/Daku3011/Quiz/issues) |
| 💡 **GitHub Discussions** | Q&A, ideas, general discussion | [Join Discussion](https://github.com/Daku3011/Quiz/discussions) |
| 📖 **Wiki** | Documentation and guides | [View Wiki](https://github.com/Daku3011/Quiz/wiki) |
| ⭐ **GitHub Stars** | Show your support! | [Star Repository](https://github.com/Daku3011/Quiz) |

### 📧 Direct Contact

**For Security Issues:**
- 🔒 Report via GitHub Security Advisories
- 📧 Email maintainers (available on GitHub profiles)

**For Business Inquiries:**
- 💼 Contact through GitHub profiles
- 💬 Reach out via LinkedIn (if available)

---

## 📊 Project Stats

![GitHub stars](https://img.shields.io/github/stars/Daku3011/Quiz?style=social)
![GitHub forks](https://img.shields.io/github/forks/Daku3011/Quiz?style=social)
![GitHub issues](https://img.shields.io/github/issues/Daku3011/Quiz)
![GitHub pull requests](https://img.shields.io/github/issues-pr/Daku3011/Quiz)
![License](https://img.shields.io/github/license/Daku3011/Quiz)
![Last commit](https://img.shields.io/github/last-commit/Daku3011/Quiz)

---

## 📝 Changelog

### Version 1.0.1 (November 2025) - Initial Release

#### ✨ Features
- ✅ JavaFX Desktop Application for Faculty
- ✅ JavaFX Desktop Application for Students
- ✅ Responsive Web Client for Students
- ✅ Spring Boot REST API backend
- ✅ H2 Database integration
- ✅ AI-powered question generation (Mock service)
- ✅ Real-time scoreboard
- ✅ Session management with OTP
- ✅ PDF syllabus parsing
- ✅ Multiple-choice questions
- ✅ CSV/PDF/JSON export

#### 🔧 Technical
- Java 17 support
- Maven build system
- Spring Security with JWT
- Modern JavaFX UI
- Responsive web design

#### 📖 Documentation
- Comprehensive wiki
- Installation guide
- User guides (Faculty & Student)
- API documentation
- Troubleshooting guide
- Contributing guidelines

---

## 🎓 Educational Use

This project is perfect for:

- 🏫 **Schools & Colleges** - Conduct quizzes and assessments
- 👨‍🏫 **Teachers** - Create interactive quizzes quickly
- 🎯 **Training Centers** - Test learner knowledge
- 💼 **Corporate Training** - Employee assessments
- 📚 **Study Groups** - Practice and revision
- 🏆 **Competitions** - Quiz competitions and contests

**Free for educational use!** No licensing fees, no limitations.

---

## 🌍 Testimonials

> *"Easy to set up and use. The AI question generation saved me hours of work!"*  
> — Faculty Member

> *"Love the mobile web interface. I can take quizzes from anywhere!"*  
> — Student

> *"Perfect for our remote learning setup. Highly recommended!"*  
> — Institute Coordinator

**Want to share your experience?** [Add a testimonial](https://github.com/Daku3011/Quiz/discussions)

---

## 🎉 Thank You!

Thank you for choosing the Quiz Application! We hope it makes your teaching and learning experience better.

### Show Your Support ⭐

If you find this project helpful:
- ⭐ Star the repository
- 🍴 Fork and contribute
- 📢 Share with others
- 💬 Provide feedback
- 🐛 Report issues
- 📝 Improve documentation

---


## 📅 Last Updated

**Wiki Version:** 1.0.1 
**Last Updated:** November 23, 2025  
**Project Status:** ✅ Active Development

---

<div align="center">

**Made with ❤️ by [Daku3011](https://github.com/Daku3011) and Moonshine**

**[⬆ Back to Top](#quiz-application-wiki)**

---

### 🌟 Don't forget to star the repo! 🌟

[![GitHub stars](https://img.shields.io/github/stars/Daku3011/Quiz?style=social)](https://github.com/Daku3011/Quiz)

</div>



