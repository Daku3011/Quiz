const API_BASE = '/api';
let studentId = null;
let sessionId = null;
let questions = [];
let currentIndex = 0;
let answers = {}; // Map<questionId, selectedOption>

// DOM Elements
const sections = {
    register: document.getElementById('register-section'),
    waiting: document.getElementById('waiting-section'),
    quiz: document.getElementById('quiz-section'),
    result: document.getElementById('result-section'),
    cheating: document.getElementById('cheating-section')
};

// Anti-Cheating: Detect Tab Switch
document.addEventListener('visibilitychange', () => {
    if (document.hidden && !sections.quiz.classList.contains('hidden')) {
        handleCheating();
    }
});

function handleCheating() {
    // Submit with 0 score or just disqualify
    // For now, disqualify locally
    showSection('cheating');

    // Optional: Send a "disqualified" flag to backend if needed
    // submitQuiz(true); 
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
        } else {
            alert("Submission failed.");
        }
    } catch (e) {
        console.error(e);
        alert("Error submitting quiz.");
    }
}
