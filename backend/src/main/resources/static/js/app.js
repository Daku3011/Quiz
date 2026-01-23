
let studentId = null;
let sessionId = null;
let questions = [];
let currentIndex = 0;
let answers = {};
let quizTimer = null;
let countdownTimer = null;
let hasCheated = false;
let isSubmitting = false;

const sections = {
    login: document.getElementById('login-section'),
    joinSession: document.getElementById('join-session-section'),
    waiting: document.getElementById('waiting-section'),
    quiz: document.getElementById('quiz-section'),
    result: document.getElementById('result-section'),
    cheating: document.getElementById('cheating-section')
};

// Anti-cheating system
document.addEventListener('visibilitychange', () => {
    if (isSubmitting) return;
    if (document.hidden && !sections.quiz.classList.contains('hidden')) {
        handleCheating();
    }
});

window.addEventListener('blur', () => {
    if (isSubmitting) return;
    if (!sections.quiz.classList.contains('hidden')) {
        handleCheating();
    }
});

document.addEventListener('fullscreenchange', () => {
    if (isSubmitting) return;
    if (!document.fullscreenElement && !sections.quiz.classList.contains('hidden')) {
        handleCheating();
    }
});

// Form validation
function validateEmail(email) {
    // Allow email format OR alphanumeric strings (enrollment IDs)
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$|^[A-Za-z0-9]+$/;
    return emailRegex.test(email);
}

function validatePassword(password) {
    return password.length >= 6;
}

function validateName(name) {
    return name.trim().length >= 2;
}

function validateEnrollment(enrollment) {
    return enrollment.trim().length >= 2;
}

function validateSessionId(sessionId) {
    return sessionId.trim().length >= 1;
}

function validateOTP(otp) {
    return otp.trim().length >= 4;
}

function clearErrors() {
    document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
}

// Login page (2.1)
async function handleLogin(event) {
    event.preventDefault();
    clearErrors();

    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;
    const remember = document.getElementById('login-remember').checked;

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
        const res = await fetch(`${API_BASE}/student/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (res.ok) {
            const data = await res.json();
            studentId = data.studentId;

            if (remember) {
                localStorage.setItem('student_email', email);
            }

            sessionStorage.setItem('quizState', JSON.stringify({
                studentId, email, authenticated: true
            }));

            showSection('joinSession');
            document.getElementById('join-enrollment').value = email;
            document.getElementById('join-name').focus();
        } else {
            const errorText = await res.text();
            document.getElementById('login-error').textContent = 'Login failed: ' + errorText;
        }
    } catch (e) {
        console.error(e);
        document.getElementById('login-error').textContent = 'Error connecting to server';
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

    // Constraint relaxed: Allow shared devices. Backend handles duplicate submissions per student.
    /*
    const completedSessions = JSON.parse(localStorage.getItem('completed_sessions') || '[]');
    if (completedSessions.includes(sessionId)) {
        document.getElementById('join-error').textContent = 'You have already completed this session on this device';
        return;
    }
    */

    const joinBtn = document.getElementById('join-btn');
    const joinBtnText = document.getElementById('join-btn-text');
    const joinSpinner = document.getElementById('join-spinner');
    joinBtn.disabled = true;
    joinBtnText.textContent = 'Joining...';
    joinSpinner.classList.remove('hidden');

    try {
        const res = await fetch(`${API_BASE}/student/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, enrollment, sessionId, otp })
        });

        if (res.ok) {
            const data = await res.json();
            studentId = data.studentId;

            sessionStorage.setItem('quizState', JSON.stringify({
                sessionId, studentId, otp, name, enrollment, cheated: false
            }));

            checkSessionStatus();
        } else {
            const errorText = await res.text();
            document.getElementById('join-error').textContent = 'Join failed: ' + errorText;
        }
    } catch (e) {
        console.error(e);
        document.getElementById('join-error').textContent = 'Error connecting to server';
    } finally {
        joinBtn.disabled = false;
        joinBtnText.textContent = 'Join Session';
        joinSpinner.classList.add('hidden');
    }
}

// Waiting room (2.3)
async function checkSessionStatus() {
    try {
        const res = await fetch(`${API_BASE}/session/${sessionId}/status`);
        if (res.ok) {
            const data = await res.json();
            if (data.status === 'ACTIVE') {
                showSection('quiz');
                await loadQuestions();
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
        } else {
            document.getElementById('waiting-status').textContent = 'Error checking status. Retrying...';
            setTimeout(checkSessionStatus, 5000);
        }
    } catch (e) {
        console.error(e);
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
        const res = await fetch(`${API_BASE}/session/${sessionId}/questions?studentId=${studentId}`);
        if (res.ok) {
            questions = await res.json();
            if (questions.length > 0) {
                currentIndex = 0;
                answers = {};
                await requestFullScreen();
                showSection('quiz');
                startQuizTimer();
                renderQuestion();
            } else {
                alert('No questions in this session');
            }
        } else {
            alert('Failed to load questions');
        }
    } catch (e) {
        console.error(e);
        alert('Error loading questions');
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
        if (!confirm('Are you sure you want to submit?')) {
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
        answers: Object.entries(answers).map(([qid, opt]) => ({
            questionId: parseInt(qid),
            selectedOption: opt
        }))
    };

    try {
        const res = await fetch(`${API_BASE}/quiz/submit`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const data = await res.json();
            displayResults(data);

            /*
            const completedSessions = JSON.parse(localStorage.getItem('completed_sessions') || '[]');
            if (!completedSessions.includes(sessionId)) {
                completedSessions.push(sessionId);
                localStorage.setItem('completed_sessions', JSON.stringify(completedSessions));
            }
            */

            const state = JSON.parse(sessionStorage.getItem('quizState'));
            state.completed = true;
            sessionStorage.setItem('quizState', JSON.stringify(state));

            showSection('result');
            window.onbeforeunload = null;
        } else {
            alert('Submission failed');
        }
    } catch (e) {
        console.error(e);
        alert('Error submitting quiz');
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
    submitQuiz(true);
}

// Utility functions
function showSection(name) {
    Object.values(sections).forEach(el => el.classList.add('hidden'));
    if (sections[name]) {
        sections[name].classList.remove('hidden');
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
            showSection('joinSession');
            if (state.email) {
                document.getElementById('join-enrollment').value = state.email;
            }
            document.getElementById('join-name').focus();
        }
    } else {
        const rememberedEmail = localStorage.getItem('student_email');
        if (rememberedEmail) {
            document.getElementById('login-email').value = rememberedEmail;
            document.getElementById('login-remember').checked = true;
        }
        showSection('login');
    }
};

window.onbeforeunload = function () {
    if (!sections.quiz.classList.contains('hidden')) {
        return 'Are you sure you want to leave? Your progress will be lost.';
    }
};
