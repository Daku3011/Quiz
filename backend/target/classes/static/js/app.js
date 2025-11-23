const API_BASE = '/api';
let studentId = null;
let sessionId = null;
let questions = [];
let currentIndex = 0;
let answers = {}; // Map<questionId, selectedOption>

// DOM Elements
const sections = {
    register: document.getElementById('register-section'),
    join: document.getElementById('join-section'),
    quiz: document.getElementById('quiz-section'),
    result: document.getElementById('result-section')
};

function showSection(name) {
    Object.values(sections).forEach(el => el.classList.add('hidden'));
    sections[name].classList.remove('hidden');
}

async function register() {
    const name = document.getElementById('reg-name').value;
    const enrollment = document.getElementById('reg-enrollment').value;
    const status = document.getElementById('reg-status');

    if (!name || !enrollment) {
        status.textContent = "Please fill all fields.";
        status.style.color = "red";
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/student/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, enrollment })
        });

        if (res.ok) {
            const data = await res.json();
            studentId = data.studentId;
            status.textContent = "Registration Successful!";
            status.style.color = "green";
            setTimeout(() => showSection('join'), 1000);
        } else {
            status.textContent = "Registration Failed.";
            status.style.color = "red";
        }
    } catch (e) {
        console.error(e);
        status.textContent = "Error connecting to server.";
    }
}

async function joinSession() {
    sessionId = document.getElementById('session-id').value;
    const otp = document.getElementById('otp').value;
    const status = document.getElementById('join-status');

    if (!sessionId || !otp) {
        status.textContent = "Enter Session ID and OTP.";
        status.style.color = "red";
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/session/join`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sessionId, otp })
        });

        if (res.ok) {
            status.textContent = "Joined! Loading questions...";
            status.style.color = "green";
            await loadQuestions();
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

async function loadQuestions() {
    try {
        const res = await fetch(`${API_BASE}/session/${sessionId}/questions`);
        if (res.ok) {
            questions = await res.json();
            if (questions.length > 0) {
                currentIndex = 0;
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
    document.getElementById('q-text').textContent = q.text;

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

async function submitQuiz() {
    if (!confirm("Are you sure you want to submit?")) return;

    const payload = {
        sessionId: sessionId,
        studentId: studentId,
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
            showSection('result');
        } else {
            alert("Submission failed.");
        }
    } catch (e) {
        console.error(e);
        alert("Error submitting quiz.");
    }
}
