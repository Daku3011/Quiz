
let studentId = null;
let sessionId = null;
let questions = [];
let currentIndex = 0;
let answers = {};
let quizTimer = null;
let countdownTimer = null;
let hasCheated = false;
let antiCheatingActive = false;
let isSubmitting = false;
// API_BASE is defined in api.js

const sections = {
    login: document.getElementById('login-section'),
    joinSession: document.getElementById('join-session-section'),
    waiting: document.getElementById('waiting-section'),
    quiz: document.getElementById('quiz-section'),
    coding: document.getElementById('coding-exam-section'),
    result: document.getElementById('result-section'),
    cheating: document.getElementById('cheating-section')
};

// Anti-cheating system
document.addEventListener('visibilitychange', () => {
    if (isSubmitting || !antiCheatingActive) return;
    const isExamActive = !sections.quiz.classList.contains('hidden') || (sections.coding && !sections.coding.classList.contains('hidden'));
    if (document.hidden && isExamActive) {
        handleCheating();
    }
});

document.addEventListener('fullscreenchange', () => {
    if (isSubmitting || !antiCheatingActive) return;
    const isExamActive = !sections.quiz.classList.contains('hidden') || (sections.coding && !sections.coding.classList.contains('hidden'));
    if (!document.fullscreenElement && isExamActive) {
        handleCheating();
    }
});

function clearErrors() {
    document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
}

// Login page (2.1)
async function handleLogin(event) {
    event.preventDefault();
    clearErrors();

    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;

    let isValid = true;

    if (!email) {
        document.getElementById('login-email-error').textContent = 'Email or enrollment ID is required';
        isValid = false;
    } else if (!validateEmail(email)) {
        document.getElementById('login-email-error').textContent = 'Please enter a valid email or enrollment ID';
        isValid = false;
    }

    if (!password) {
        document.getElementById('login-password-error').textContent = 'Password is required';
        isValid = false;
    } else if (!validatePassword(password)) {
        document.getElementById('login-password-error').textContent = 'Password must be at least 6 characters';
        isValid = false;
    }

    if (!isValid) return;

    const loginBtn = document.getElementById('login-btn');
    const loginBtnText = document.getElementById('login-btn-text');
    const loginSpinner = document.getElementById('login-spinner');
    loginBtn.disabled = true;
    loginBtnText.textContent = 'Logging in...';
    loginSpinner.classList.remove('hidden');

    try {
        const data = await studentAPI.login({ email, password });

        if (data) {
            studentId = data.studentId;



            sessionStorage.setItem('quizState', JSON.stringify({
                studentId, email, authenticated: true
            }));

            showSection('waiting');
            document.getElementById('waiting-session-title').textContent = 'Connecting...';
            document.getElementById('waiting-instructor').textContent = 'Checking for active sessions...';
            document.getElementById('students-joined').parentElement.classList.add('hidden'); // Hide count
            document.getElementById('waiting-status').textContent = 'Please wait...';

            checkForActiveSessions();
        }
    } catch (error) {
        console.error(error);
        document.getElementById('login-error').textContent = error.message || 'Login failed';
    } finally {
        loginBtn.disabled = false;
        loginBtnText.textContent = 'Login';
        loginSpinner.classList.add('hidden');
    }
}

function handleForgotPassword(event) {
    event.preventDefault();
    alert('Password reset functionality coming soon. Please contact your instructor.');
}

function goBackToLogin() {
    showSection('login');
    clearErrors();
}

// Join session page (2.2)
async function handleJoinSession(event) {
    event.preventDefault();
    clearErrors();

    const name = document.getElementById('join-name').value.trim();
    const enrollment = document.getElementById('join-enrollment').value.trim();
    sessionId = document.getElementById('join-session-id').value.trim();
    const otp = document.getElementById('join-otp').value.trim();

    let isValid = true;

    if (!name) {
        document.getElementById('join-name-error').textContent = 'Full name is required';
        isValid = false;
    } else if (!validateName(name)) {
        document.getElementById('join-name-error').textContent = 'Please enter a valid name';
        isValid = false;
    }

    if (!enrollment) {
        document.getElementById('join-enrollment-error').textContent = 'Enrollment ID is required';
        isValid = false;
    } else if (!validateEnrollment(enrollment)) {
        document.getElementById('join-enrollment-error').textContent = 'Please enter a valid enrollment ID';
        isValid = false;
    }

    if (!sessionId) {
        document.getElementById('join-session-id-error').textContent = 'Session ID is required';
        isValid = false;
    } else if (!validateSessionId(sessionId)) {
        document.getElementById('join-session-id-error').textContent = 'Please enter a valid session ID';
        isValid = false;
    }

    if (!otp) {
        document.getElementById('join-otp-error').textContent = 'OTP is required';
        isValid = false;
    } else if (!validateOTP(otp)) {
        document.getElementById('join-otp-error').textContent = 'Please enter a valid OTP';
        isValid = false;
    }

    if (!isValid) return;

    // Constraint: One device can only give one exam per session ID
    const completedSessions = JSON.parse(localStorage.getItem('completed_sessions') || '[]');
    if (completedSessions.some(id => String(id) === String(sessionId))) {
        document.getElementById('join-error').textContent = 'You have already completed this session on this device';
        return;
    }

    const joinBtn = document.getElementById('join-btn');
    const joinBtnText = document.getElementById('join-btn-text');
    const joinSpinner = document.getElementById('join-spinner');
    joinBtn.disabled = true;
    joinBtnText.textContent = 'Joining...';
    joinSpinner.classList.remove('hidden');

    try {
        const data = await studentAPI.register({ name, enrollment, sessionId, otp });

        if (data) {
            studentId = data.studentId;

            sessionStorage.setItem('quizState', JSON.stringify({
                sessionId, studentId, otp, name, enrollment, cheated: false
            }));

            checkSessionStatus();
        }
    } catch (error) {
        console.error(error);
        document.getElementById('join-error').textContent = error.message || 'Join failed';
    } finally {
        joinBtn.disabled = false;
        joinBtnText.textContent = 'Join Session';
        joinSpinner.classList.add('hidden');
    }
}

// Waiting room (2.3)
async function checkSessionStatus() {
    try {
        const data = await sessionAPI.getStatus(sessionId);
        if (data) {
            if (data.status === 'ACTIVE') {
                if (data.coding === true) {
                    showSection('coding');
                    await loadCodingExam();
                } else {
                    showSection('quiz');
                    await loadQuestions();
                }
            } else if (data.status === 'WAITING') {
                showSection('waiting');
                document.getElementById('waiting-session-title').textContent = data.title || 'Waiting Room';
                document.getElementById('waiting-instructor').textContent = `Instructor: ${data.instructor || '-'}`;
                document.getElementById('students-joined').textContent = data.studentCount || 0;

                startCountdownTimer(data.startTime);
                setTimeout(checkSessionStatus, 5000);
            } else {
                alert('This session has ended');
                showSection('login');
            }
        }
    } catch (error) {
        console.error(error);
        // Only show status update if we are in the waiting room
        if (!sections.waiting.classList.contains('hidden')) {
            document.getElementById('waiting-status').textContent = 'Error checking status. Retrying...';
        }
        setTimeout(checkSessionStatus, 5000);
    }
}

function startCountdownTimer(startTime) {
    if (countdownTimer) clearInterval(countdownTimer);

    const updateTimer = () => {
        const now = new Date().getTime();
        const start = new Date(startTime).getTime();
        const distance = start - now;

        if (distance <= 0) {
            clearInterval(countdownTimer);
            document.getElementById('countdown-timer').textContent = '00:00';
            return;
        }

        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        document.getElementById('countdown-timer').textContent =
            `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    };

    updateTimer();
    countdownTimer = setInterval(updateTimer, 1000);
}

function leaveSession() {
    if (confirm('Are you sure you want to leave this session?')) {
        sessionStorage.clear();
        showSection('login');
    }
}

// Quiz interface (2.4)
async function loadQuestions() {
    try {
        const data = await sessionAPI.getQuestions(sessionId, { studentId });
        if (data) {
            questions = data;
            if (questions.length > 0) {
                currentIndex = 0;
                answers = {};
                await requestFullScreen();
                showSection('quiz');
                startQuizTimer();
                renderQuestion();
                setTimeout(() => { antiCheatingActive = true; }, 3000);
            } else {
                alert('No questions in this session');
            }
        }
    } catch (error) {
        console.error(error);
        alert('Error loading questions: ' + (error.message || 'Unknown error'));
    }
}

async function requestFullScreen() {
    try {
        if (!document.fullscreenElement) {
            await document.documentElement.requestFullscreen();
        }
    } catch (e) {
        console.warn('Fullscreen request denied or failed', e);
    }
}

function startQuizTimer() {
    if (quizTimer) clearInterval(quizTimer);

    let timeRemaining = 3600;

    const updateTimer = () => {
        const minutes = Math.floor(timeRemaining / 60);
        const seconds = timeRemaining % 60;
        document.getElementById('timer').textContent =
            `Time: ${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;

        if (timeRemaining <= 0) {
            clearInterval(quizTimer);
            submitQuiz(true);
        }

        timeRemaining--;
    };

    updateTimer();
    quizTimer = setInterval(updateTimer, 1000);
}

function renderQuestion() {
    const q = questions[currentIndex];
    const totalQuestions = questions.length;
    const progressPercent = ((currentIndex + 1) / totalQuestions) * 100;

    document.getElementById('q-number').textContent = `Question ${currentIndex + 1}/${totalQuestions}`;

    const progressBar = document.getElementById('progress-bar');
    progressBar.style.background = `linear-gradient(to right, var(--primary) 0%, var(--primary) ${progressPercent}%, #E5E7EB ${progressPercent}%, #E5E7EB 100%)`;
    progressBar.setAttribute('aria-valuenow', Math.round(progressPercent));
    document.getElementById('progress-text').textContent = `${Math.round(progressPercent)}% Complete`;

    let formattedText = q.text.replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>');
    formattedText = formattedText.replace(/`([^`]+)`/g, '<code>$1</code>');
    document.getElementById('q-text').innerHTML = formattedText;

    const imgEl = document.getElementById('q-image');
    if (q.image) {
        imgEl.src = q.image;
        imgEl.style.display = 'block';
    } else {
        imgEl.style.display = 'none';
    }

    document.getElementById('label-a').textContent = 'A) ' + (q.optionA || '');
    document.getElementById('label-b').textContent = 'B) ' + (q.optionB || '');
    document.getElementById('label-c').textContent = 'C) ' + (q.optionC || '');
    document.getElementById('label-d').textContent = 'D) ' + (q.optionD || '');

    const saved = answers[q.id];
    const radios = document.getElementsByName('choice');
    radios.forEach(r => {
        r.checked = (r.value === saved);
        r.onchange = () => { answers[q.id] = r.value; };
    });

    document.getElementById('btn-prev').disabled = (currentIndex === 0);
    const isLast = (currentIndex === questions.length - 1);
    document.getElementById('btn-next').classList.toggle('hidden', isLast);
    document.getElementById('btn-submit').classList.toggle('hidden', !isLast);
}

function nextQuestion() {
    if (currentIndex < questions.length - 1) {
        currentIndex++;
        renderQuestion();
    }
}

function prevQuestion() {
    if (currentIndex > 0) {
        currentIndex--;
        renderQuestion();
    }
}

document.addEventListener('keydown', (e) => {
    if (!sections.quiz.classList.contains('hidden')) {
        if (e.key === 'ArrowRight') {
            nextQuestion();
        } else if (e.key === 'ArrowLeft') {
            prevQuestion();
        }
    }
});

// Results page (2.5)
async function submitQuiz(autoSubmit = false) {
    if (!autoSubmit) {
        isSubmitting = true;

        // Count unattempted questions
        const totalQuestions = questions.length;
        const answeredCount = Object.keys(answers).length;
        const unattemptedCount = totalQuestions - answeredCount;

        let message = 'Are you sure you want to submit?';
        if (unattemptedCount > 0) {
            message = `You have missed ${unattemptedCount} question(s). even then try to submit then make those empty question to worng`;
        } // User phrasing: "you have miss the question then even then try to submit then make those empty question to worng"
        // I will make it slightly more grammatical but keep the spirit or use the user's exact if they prefer, 
        // but effective communication is better. 
        // User asked: "ask them one time that you have miss the question then even then try to submit then make those empty question to worng"
        // I'll stick to a clear message: "You have missed X question(s). Submitting now will mark them as wrong. Do you want to proceed?" 
        // Re-reading user request: "ask them one time that you have miss the question then even then try to submit then make those empty question to worng"
        // I will use a clean professional message.
        if (unattemptedCount > 0) {
            message = `You have missed ${unattemptedCount} question(s). Submitting now will mark them as wrong. Do you want to proceed?`;
        }

        if (!confirm(message)) {
            isSubmitting = false;
            return;
        }
    } else {
        isSubmitting = true;
    }

    if (quizTimer) clearInterval(quizTimer);

    const saved = sessionStorage.getItem('quizState');
    let name = 'Unknown';
    let enrollment = 'Unknown';
    if (saved) {
        const state = JSON.parse(saved);
        name = state.name;
        enrollment = state.enrollment;
    }

    const payload = {
        sessionId: sessionId,
        studentId: studentId,
        name: name,
        enrollment: enrollment,
        cheated: hasCheated,
        answers: questions.map(q => ({
            questionId: q.id,
            selectedOption: answers[q.id] || null
        }))
    };

    try {
        const data = await quizAPI.submit(payload);

        if (data) {
            displayResults(data);

            const completedSessions = JSON.parse(localStorage.getItem('completed_sessions') || '[]');
            if (!completedSessions.some(id => String(id) === String(sessionId))) {
                completedSessions.push(String(sessionId));
                localStorage.setItem('completed_sessions', JSON.stringify(completedSessions));
            }

            const state = JSON.parse(sessionStorage.getItem('quizState'));
            state.completed = true;
            sessionStorage.setItem('quizState', JSON.stringify(state));

            showSection('result');
            window.onbeforeunload = null;
        }
    } catch (error) {
        console.error(error);
        alert('Error submitting quiz: ' + (error.message || 'Unknown error'));
    }
}

function displayResults(data) {
    const totalQuestions = questions.length;
    const score = data.score || 0;
    const percentage = Math.round((score / totalQuestions) * 100);

    document.getElementById('score-display').textContent = `${score}/${totalQuestions}`;
    document.getElementById('score-percentage').textContent = `${percentage}%`;

    const badge = document.getElementById('performance-badge');
    badge.className = 'performance-badge';
    if (percentage >= 80) {
        badge.textContent = '⭐ Excellent!';
        badge.classList.add('badge-excellent');
    } else if (percentage >= 60) {
        badge.textContent = '👍 Good!';
        badge.classList.add('badge-good');
    } else if (percentage >= 40) {
        badge.textContent = '📚 Average';
        badge.classList.add('badge-average');
    } else {
        badge.textContent = '💪 Keep Trying!';
        badge.classList.add('badge-poor');
    }

    const details = document.getElementById('results-details');
    details.innerHTML = '<h3>Detailed Results</h3>';

    if (data.results) {
        data.results.forEach((r, idx) => {
            const resultItem = document.createElement('div');
            resultItem.className = 'result-item';

            const isCorrect = r.isCorrect;
            const headerClass = isCorrect ? 'correct' : 'incorrect';
            const statusIcon = isCorrect ? '✅' : '❌';

            resultItem.innerHTML = `
            <div class="result-item-header ${headerClass}" onclick="toggleResultItem(this)">
                <span>${statusIcon} Question ${idx + 1}: ${isCorrect ? 'Correct' : 'Incorrect'}</span>
                <span>▼</span>
            </div>
            <div class="result-item-content">
                <div class="result-answer">
                    <div class="result-answer-label">Question:</div>
                    <div class="result-answer-value">${r.text}</div>
                </div>
                <div class="result-answer">
                    <div class="result-answer-label">Your Answer:</div>
                    <div class="result-answer-value">${r.selected || 'Not answered'}</div>
                </div>
                ${!isCorrect ? `
                    <div class="result-answer">
                        <div class="result-answer-label">Correct Answer:</div>
                        <div class="result-answer-value">${r.correctOption}</div>
                    </div>
                ` : ''}
                <div class="result-explanation">
                    <div class="result-explanation-label">Explanation:</div>
                    <p>${r.explanation || 'No explanation provided.'}</p>
                </div>
            </div>
        `;

            details.appendChild(resultItem);
        });
    }
}

function toggleResultItem(header) {
    const content = header.nextElementSibling;
    content.classList.toggle('open');
    const arrow = header.querySelector('span:last-child');
    arrow.textContent = content.classList.contains('open') ? '▲' : '▼';
}

function downloadPDF() {
    alert('PDF download functionality coming soon');
}

function exitQuiz() {
    sessionStorage.clear();
    showSection('login');
    window.location.reload();
}

// Cheating detection
function handleCheating() {
    hasCheated = true;

    const saved = sessionStorage.getItem('quizState');
    if (saved) {
        const state = JSON.parse(saved);
        state.cheated = true;
        sessionStorage.setItem('quizState', JSON.stringify(state));
    }

    showSection('cheating');

    // Auto-submit based on current active section
    if (!sections.coding.classList.contains('hidden')) {
        submitCodingSolution(true);
    } else {
        submitQuiz(true);
    }
}

// Standby Mode Logic
async function checkForActiveSessions() {
    try {
        const sessions = await sessionAPI.getActive();
        if (sessions) {


            if (sessions.length > 0) {
                // Session found!
                const session = sessions[0]; // Auto-pick the first one
                showSection('joinSession');

                // Pre-fill
                document.getElementById('join-session-id').value = session.id;
                document.getElementById('join-name').focus();

                const state = JSON.parse(sessionStorage.getItem('quizState'));
                if (state && state.email) {
                    document.getElementById('join-enrollment').value = state.email;
                }
            } else {
                // No session
                showSection('waiting');
                document.getElementById('waiting-session-title').textContent = 'Standby Mode';
                document.getElementById('waiting-instructor').textContent = 'Waiting for Faculty to start a session...';
                document.getElementById('students-joined').parentElement.classList.add('hidden');
                document.getElementById('waiting-status').textContent = 'Polling for sessions...';

                // Poll again in 5s
                setTimeout(checkForActiveSessions, 5000);
            }

        }
    } catch (error) {
        console.error("Network error checking sessions", error);
        setTimeout(checkForActiveSessions, 5000);
    }
}

// Utility functions
function showSection(name) {
    Object.values(sections).forEach(el => el.classList.add('hidden'));
    if (sections[name]) {
        sections[name].classList.remove('hidden');
    }
    // Re-show joined count if leaving waiting/standby (simple reset)
    if (name !== 'waiting') {
        document.getElementById('students-joined').parentElement.classList.remove('hidden');
    }

    if (name !== 'quiz' && name !== 'coding') {
        antiCheatingActive = false;
    }

    // Dynamic width expansion for exam screens to allow spacious coding and quiz layout
    const container = document.getElementById('main-content');
    if (container) {
        if (name === 'coding') {
            container.style.maxWidth = '2200px';
            container.style.width = '95%';
        } else if (name === 'quiz') {
            container.style.maxWidth = '850px';
            container.style.width = '95%';
        } else {
            container.style.maxWidth = '';
            container.style.width = '';
        }
    }
}

// Page initialization
window.onload = function () {
    const saved = sessionStorage.getItem('quizState');
    if (saved) {
        const state = JSON.parse(saved);

        if (state.cheated) {
            hasCheated = true;
            sessionId = state.sessionId;
            studentId = state.studentId;
            showSection('cheating');
            return;
        }

        if (state.completed) {
            document.body.innerHTML = '<div style="text-align:center; margin-top:50px;"><h2>You have completed this quiz.</h2><p>Multiple submissions are not allowed.</p><button onclick="exitQuiz()" class="btn btn-primary" style="margin-top:20px;">Exit</button></div>';
            return;
        }

        if (state.authenticated) {
            sessionId = state.sessionId;
            studentId = state.studentId;
            // Instead of going straight to join, check if we need to standby
            if (!sessionId) {
                checkForActiveSessions();
            } else {
                showSection('joinSession');
                if (state.email) {
                    document.getElementById('join-enrollment').value = state.email;
                }
                document.getElementById('join-name').focus();
            }
        }
    } else {
        const rememberedEmail = localStorage.getItem('student_email');
        if (rememberedEmail) {
            document.getElementById('login-email').value = rememberedEmail;
        }
        showSection('login');
    }
};

window.onbeforeunload = function () {
    const isExamActive = !sections.quiz.classList.contains('hidden') || (sections.coding && !sections.coding.classList.contains('hidden'));
    if (isExamActive) {
        return 'Are you sure you want to leave? Your progress will be lost.';
    }
};

// ==========================================
// Practical Coding Exam Supporting Functions
// ==========================================
let codingTimerInterval = null;

async function loadCodingExam() {
    try {
        const res = await fetch(`${API_BASE}/api/session/${sessionId}/coding-details?studentId=${studentId}`);
        if (res.ok) {
            const data = await res.json();

            document.getElementById('coding-exam-title').textContent = data.title || "Practical Coding Exam";
            document.getElementById('coding-assigned-problem-name').textContent = data.assignedProblemName || "Assigned Problem";
            document.getElementById('coding-problem-text').textContent = data.problemStatement || "No problem statement loaded.";
            document.getElementById('editor-lang-indicator').textContent = `Monaco Editor - ${data.programmingLanguage.toUpperCase()} Mode`;

            // Enforce fullscreen
            await requestFullScreen();

            // Load and init Monaco Editor
            if (typeof require !== 'undefined') {
                require.config({ paths: { vs: 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.45.0/min/vs' } });
                require(['vs/editor/editor.main'], function () {
                    const container = document.getElementById('monaco-editor-placeholder');
                    container.innerHTML = ''; // Clear placeholder loading text

                    window.monacoEditor = monaco.editor.create(container, {
                        value: getBoilerplate(data.programmingLanguage),
                        language: data.programmingLanguage === 'cpp' ? 'cpp' : (data.programmingLanguage === 'c' ? 'c' : data.programmingLanguage),
                        theme: 'vs-dark',
                        automaticLayout: true,
                        fontSize: 14,
                        fontFamily: "'JetBrains Mono', monospace",
                        minimap: { enabled: false },
                        tabSize: 4
                    });
                });
            } else {
                alert("Monaco Editor loader not available in page. Please contact support.");
            }

            startCodingTimer(data.endTime);
            setTimeout(() => { antiCheatingActive = true; }, 3000);
        } else {
            alert("Failed to load coding details");
        }
    } catch (e) {
        console.error(e);
        alert("Error loading coding exam details");
    }
}

function getBoilerplate(lang) {
    switch (lang.toLowerCase()) {
        case 'python':
            return `def solve():\n    # Write your python code here\n    pass\n\nif __name__ == '__main__':\n    solve()`;
        case 'c':
            return `#include <stdio.h>\n\nint main() {\n    // Write your C code here\n    return 0;\n}`;
        case 'cpp':
            return `#include <iostream>\nusing namespace std;\n\nint main() {\n    // Write your C++ code here\n    return 0;\n}`;
        case 'java':
            return `public class Solution {\n    public static void main(String[] args) {\n        // Write your Java code here\n    }\n}`;
        case 'javascript':
            return `function solve() {\n    // Write your JavaScript code here\n}\n\nsolve();`;
        default:
            return `// Write your code here`;
    }
}

function startCodingTimer(endTime) {
    if (codingTimerInterval) clearInterval(codingTimerInterval);

    const updateTimer = () => {
        const now = new Date().getTime();
        const end = new Date(endTime).getTime();
        const distance = end - now;

        if (distance <= 0) {
            clearInterval(codingTimerInterval);
            document.getElementById('coding-timer').textContent = "Time: 00:00";
            submitCodingSolution(true);
            return;
        }

        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        document.getElementById('coding-timer').textContent =
            `Time: ${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    };

    updateTimer();
    codingTimerInterval = setInterval(updateTimer, 1000);
}

async function submitCodingSolution(autoSubmit = false) {
    if (!autoSubmit) {
        if (!confirm("Are you sure you want to submit your code solution?")) {
            return;
        }
    }

    isSubmitting = true;
    if (codingTimerInterval) clearInterval(codingTimerInterval);

    const code = window.monacoEditor ? window.monacoEditor.getValue() : "";

    const saved = sessionStorage.getItem('quizState');
    let name = 'Unknown';
    let enrollment = 'Unknown';
    if (saved) {
        const state = JSON.parse(saved);
        name = state.name;
        enrollment = state.enrollment;
    }

    const payload = {
        sessionId: sessionId,
        studentId: studentId,
        code: code,
        cheated: hasCheated,
        name: name,
        enrollment: enrollment
    };

    // Show a premium loading indicator in place of Monaco editor
    const container = document.getElementById('monaco-editor-placeholder');
    container.innerHTML = `
        <div style="display:flex; flex-direction:column; align-items:center; justify-content:center; height:100%; color:#94a3b8; font-family:'Outfit',sans-serif; gap:15px; background:#1e1e1e;">
            <div class="spinner-inline" style="border-top-color:#3b82f6; width:40px; height:40px; border-width:4px;"></div>
            <div style="font-size:1.1rem; font-weight:600;">Running Gemini AI Evaluation...</div>
            <div style="font-size:0.85rem; opacity:0.7;">This may take a few seconds as we analyze correctness and quality.</div>
        </div>
    `;

    try {
        const res = await fetch(`${API_BASE}/api/quiz/submit-code`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const data = await res.json();
            displayCodingResults(data);

            const completedSessions = JSON.parse(localStorage.getItem('completed_sessions') || '[]');
            if (!completedSessions.some(id => String(id) === String(sessionId))) {
                completedSessions.push(String(sessionId));
                localStorage.setItem('completed_sessions', JSON.stringify(completedSessions));
            }

            const state = JSON.parse(sessionStorage.getItem('quizState'));
            state.completed = true;
            sessionStorage.setItem('quizState', JSON.stringify(state));

            showSection('result');
            window.onbeforeunload = null;
        } else {
            alert("Submission failed: " + await res.text());
            isSubmitting = false;
        }
    } catch (e) {
        console.error(e);
        alert("Failed to submit code solution.");
        isSubmitting = false;
    }
}

function parseMarkdown(text) {
    if (!text) return '';
    
    const lines = text.split('\n');
    const processedLines = lines.map(line => {
        // Escape HTML to prevent XSS
        let escaped = line
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
            
        // Check for headers
        if (escaped.startsWith('### ')) {
            return `<h4 style="margin-top: 1.25rem; margin-bottom: 0.5rem; font-weight: 700; color: #1e293b; font-size: 1.05rem; text-align: left;">${escaped.substring(4)}</h4>`;
        }
        if (escaped.startsWith('## ')) {
            return `<h3 style="margin-top: 1.5rem; margin-bottom: 0.75rem; font-weight: 700; color: #0f172a; font-size: 1.2rem; text-align: left;">${escaped.substring(3)}</h3>`;
        }
        if (escaped.startsWith('# ')) {
            return `<h2 style="margin-top: 1.75rem; margin-bottom: 1rem; font-weight: 800; color: #0f172a; font-size: 1.4rem; text-align: left;">${escaped.substring(2)}</h2>`;
        }
        
        // Check for bullet points
        if (escaped.startsWith('- ')) {
            return `<li style="margin-left: 1.25rem; margin-bottom: 0.25rem; list-style-type: disc; text-align: left;">${escaped.substring(2)}</li>`;
        }
        if (escaped.startsWith('* ')) {
            return `<li style="margin-left: 1.25rem; margin-bottom: 0.25rem; list-style-type: disc; text-align: left;">${escaped.substring(2)}</li>`;
        }
        
        return escaped;
    });
    
    // Rejoin and process inline elements (bold, italic, code)
    let html = processedLines.join('\n');
    
    // Bold: **text**
    html = html.replace(/\*\*([\s\S]*?)\*\*/g, '<strong>$1</strong>');
    
    // Italic: *text*
    html = html.replace(/\*([\s\S]*?)\*/g, '<em>$1</em>');
    
    // Inline Code: `code`
    html = html.replace(/`([^`]+)`/g, '<code style="background: #f1f5f9; padding: 2px 6px; border-radius: 4px; font-family: \'JetBrains Mono\', monospace; font-size: 0.9em; color: #2563eb;">$1</code>');
    
    // Convert newlines between blocks to `<br>`
    const finalLines = html.split('\n').map(line => {
        const isBlock = line.startsWith('<h') || line.startsWith('<li') || line.startsWith('</li') || line.startsWith('</h');
        if (isBlock || line.trim() === '') {
            return line;
        }
        return line + '<br>';
    });
    
    return finalLines.join('\n');
}

function displayCodingResults(data) {
    document.getElementById('score-display').textContent = `${data.score}/100`;
    document.getElementById('score-percentage').textContent = `${data.score}%`;

    const badge = document.getElementById('performance-badge');
    badge.className = 'performance-badge';
    if (data.score >= 80) {
        badge.textContent = '⭐ Excellent!';
        badge.classList.add('badge-excellent');
    } else if (data.score >= 60) {
        badge.textContent = '👍 Good!';
        badge.classList.add('badge-good');
    } else if (data.score >= 40) {
        badge.textContent = '📚 Average';
        badge.classList.add('badge-average');
    } else {
        badge.textContent = '📚 Disqualified or Needs Practice';
        badge.classList.add('badge-poor');
    }

    const details = document.getElementById('results-details');
    details.innerHTML = `
        <h3>AI Correction Insight</h3>
        <div style="background:#f8fafc; border:1px solid #e2e8f0; padding:20px; border-radius:10px; color:#334155; font-size:0.95rem; line-height:1.5; font-family:'Outfit', sans-serif; text-align: left;">${parseMarkdown(data.aiFeedback)}</div>
    `;
}
