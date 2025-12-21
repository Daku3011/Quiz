const API_BASE = '/api';
let studentId = null;
let sessionId = null;
let questions = [];
let currentIndex = 0;
let answers = {}; // Map<questionId, selectedOption>
let hasCheated = false;

// We keep track of the different "screens" or sections of the app here.
const sections = {
    register: document.getElementById('register-section'),
    waiting: document.getElementById('waiting-section'),
    quiz: document.getElementById('quiz-section'),
    result: document.getElementById('result-section'),
    cheating: document.getElementById('cheating-section')
};

// These listeners are our "anti-cheating" system. 
// We detect if the student switches tabs, loses focus, or exits fullscreen mode.
document.addEventListener('visibilitychange', () => {
    if (document.hidden && !sections.quiz.classList.contains('hidden')) {
        handleCheating();
    }
});

window.addEventListener('blur', () => {
    if (!sections.quiz.classList.contains('hidden')) {
        // Focus lost (e.g. clicked on overlay, notification, or alt-tab)
        handleCheating();
    }
});

document.addEventListener('fullscreenchange', () => {
    if (!document.fullscreenElement && !sections.quiz.classList.contains('hidden')) {
        // User exited fullscreen
        handleCheating();
    }
});

async function requestFullScreen() {
    try {
        if (!document.fullscreenElement) {
            await document.documentElement.requestFullscreen();
        }
    } catch (e) {
        console.warn("Fullscreen request denied or failed", e);
    }
}

function handleCheating() {
    hasCheated = true;

    // Persist cheating state immediately
    const saved = sessionStorage.getItem('quizState');
    if (saved) {
        const state = JSON.parse(saved);
        state.cheated = true;
        sessionStorage.setItem('quizState', JSON.stringify(state));
    }

    // Submit with 0 score or just disqualify
    // For now, disqualify locally
    showSection('cheating');

    // Optional: Send a "disqualified" flag to backend if needed
    submitQuiz(true);
}

function showSection(name) {
    Object.values(sections).forEach(el => el.classList.add('hidden'));
    sections[name].classList.remove('hidden');
}

async function registerAndJoin() {
    const name = document.getElementById('reg-name').value;
    const enrollment = document.getElementById('reg-enrollment').value;
    sessionId = document.getElementById('session-id').value;
    const otp = document.getElementById('otp').value;
    const status = document.getElementById('reg-status');

    if (!name || !enrollment || !sessionId || !otp) {
        status.textContent = "Please fill all fields.";
        status.style.color = "red";
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/student/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, enrollment, sessionId, otp })
        });

        if (res.ok) {
            const data = await res.json();
            studentId = data.studentId;
            status.textContent = "Joined Successfully!";
            status.style.color = "green";

            // Persist state
            sessionStorage.setItem('quizState', JSON.stringify({
                sessionId, studentId, otp, name, enrollment, cheated: false
            }));

            // Check Session Status immediately
            checkSessionStatus();
        } else {
            const txt = await res.text();
            status.textContent = "Join Failed: " + txt;
            status.style.color = "red";
        }
    } catch (e) {
        console.error(e);
        status.textContent = "Error connecting to server.";
    }
}

// This part is crucial! If the student refreshes the page, we try to restore 
// their previous session so they don't have to start all over again.
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
            // Block reentry locally
            document.body.innerHTML = '<div style="text-align:center; margin-top:50px;"><h2>You have completed this quiz.</h2><p>Multiple submissions are not allowed.</p><button onclick="logoutAndHome()" class="btn secondary" style="margin-top:20px;">Exit / New Quiz</button></div>';
            return;
        }

        sessionId = state.sessionId;
        studentId = state.studentId;
        // Restore input values if needed, or just jump to status check
        document.getElementById('session-id').value = sessionId;
        document.getElementById('otp').value = state.otp;
        document.getElementById('reg-name').value = state.name;
        document.getElementById('reg-enrollment').value = state.enrollment;

        console.log("Restoring session...", state);
        checkSessionStatus();
    }
};

function logoutAndHome() {
    sessionStorage.clear();
    window.location.reload();
}

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
                document.getElementById('wait-status').textContent =
                    `Exam starts at: ${data.startTime ? new Date(data.startTime).toLocaleString() : 'Soon'}. Waiting for faculty...`;
                setTimeout(checkSessionStatus, 5000); // Poll every 5s
            } else {
                alert("This session has ENDED.");
                showSection('register');
            }
        } else {
            // If error (e.g. 404), maybe retry or fail
            document.getElementById('wait-status').textContent = "Error checking status. Retrying...";
            setTimeout(checkSessionStatus, 5000);
        }
    } catch (e) {
        console.error(e);
        setTimeout(checkSessionStatus, 5000);
    }
}

async function loadQuestions() {
    try {
        const res = await fetch(`${API_BASE}/session/${sessionId}/questions?studentId=${studentId}`);
        if (res.ok) {
            questions = await res.json();
            if (questions.length > 0) {
                currentIndex = 0;
                await requestFullScreen(); // Enforce Fullscreen
                showSection('quiz');
                renderQuestion();
            } else {
                alert("No questions in this session.");
            }
        } else {
            alert("Failed to load questions.");
        }
    } catch (e) {
        console.error(e);
        alert("Error loading questions.");
    }
}

function renderQuestion() {
    const q = questions[currentIndex];
    document.getElementById('q-number').textContent = `Question ${currentIndex + 1} / ${questions.length}`;

    // Format text: parsing markdown-style code blocks
    let formattedText = q.text.replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>');
    // Simple inline code replacement
    formattedText = formattedText.replace(/`([^`]+)`/g, '<code>$1</code>');

    document.getElementById('q-text').innerHTML = formattedText; // Use innerHTML to render formatted code

    // Handle Image
    const imgEl = document.getElementById('q-image');
    if (q.image) {
        imgEl.src = q.image;
        imgEl.classList.remove('hidden');
        imgEl.style.display = 'block';
    } else {
        imgEl.style.display = 'none';
    }

    document.getElementById('label-a').textContent = "A) " + (q.optionA || "");
    document.getElementById('label-b').textContent = "B) " + (q.optionB || "");
    document.getElementById('label-c').textContent = "C) " + (q.optionC || "");
    document.getElementById('label-d').textContent = "D) " + (q.optionD || "");

    // Restore selection
    const saved = answers[q.id];
    const radios = document.getElementsByName('choice');
    radios.forEach(r => {
        r.checked = (r.value === saved);
        r.onchange = () => { answers[q.id] = r.value; };
    });

    // Buttons
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

async function submitQuiz(autoSubmit = false) {
    if (!autoSubmit && !confirm("Are you sure you want to submit?")) return;

    const saved = sessionStorage.getItem('quizState');
    let name = "Unknown";
    let enrollment = "Unknown";
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
            document.getElementById('score-display').textContent = `Your Score: ${data.score}`;

            // Mark completed
            const saved = sessionStorage.getItem('quizState');
            if (saved) {
                const state = JSON.parse(saved);
                state.completed = true;
                sessionStorage.setItem('quizState', JSON.stringify(state));
            }

            const details = document.getElementById('results-details');
            details.innerHTML = '<h3>Detailed Results</h3>';

            if (data.results) {
                data.results.forEach((r, idx) => {
                    const div = document.createElement('div');
                    div.style.border = "1px solid #ddd";
                    div.style.padding = "10px";
                    div.style.marginBottom = "10px";
                    div.style.borderRadius = "5px";
                    div.style.backgroundColor = r.isCorrect ? "#e8f5e9" : "#ffebee";

                    div.innerHTML = `
                        <p><strong>Q${idx + 1}:</strong> <pre style="font-family:inherit; white-space:pre-wrap;">${r.text}</pre></p>
                        <div style="margin-left: 20px; font-size: 0.9em; color: #555;">
                            <p>A) ${r.optionA}</p>
                            <p>B) ${r.optionB}</p>
                            <p>C) ${r.optionC}</p>
                            <p>D) ${r.optionD}</p>
                        </div>
                        <p style="margin-top:10px;">Your Answer: <strong>${r.selected || "None"}</strong> ${r.isCorrect ? "✅" : "❌"}</p>
                        ${!r.isCorrect ? `<p>Correct Answer: <strong>${r.correctOption}</strong></p>` : ""}
                        <hr>
                        <p><strong>Explanation:</strong> ${r.explanation || "No explanation provided."}</p>
                    `;
                    details.appendChild(div);
                });
            }

            showSection('result');
            // Remove navigation warning
            window.onbeforeunload = null;

        } else {
            alert("Submission failed.");
        }
    } catch (e) {
        console.error(e);
        alert("Error submitting quiz.");
    }
}
