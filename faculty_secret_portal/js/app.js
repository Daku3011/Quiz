const API_BASE = 'http://localhost:8080';

// State
let questions = [];
let currentSessionId = null;
let currentOtp = null;

// Tab Switching
function switchTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.nav-btn').forEach(el => el.classList.remove('active'));

    document.getElementById(tabId).classList.add('active');
    document.querySelector(`button[data-tab="${tabId}"]`).classList.add('active');
}

// File Upload (Text Only)
const fileInput = document.getElementById('file-upload');
fileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (file.type === 'text/plain') {
        const reader = new FileReader();
        reader.onload = (e) => {
            document.getElementById('syllabus-text').value = e.target.result;
        };
        reader.readAsText(file);
    } else {
        alert("For Web Portal, please copy-paste PDF text or use a .txt file. PDF parsing is limited to Desktop App.");
    }
});

// Clear Syllabus
function clearSyllabus() {
    document.getElementById('syllabus-text').value = '';
    fileInput.value = '';
}

// Generate Questions
async function generateQuestions() {
    const text = document.getElementById('syllabus-text').value;
    const count = document.getElementById('q-count').value;

    if (!text.trim()) {
        alert("Please enter syllabus text first.");
        return;
    }

    const btn = document.getElementById('generate-btn');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<span class="btn-icon">⏳</span> Generating...';
    btn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/api/syllabus/generate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ text: text, count: count })
        });

        if (response.ok) {
            const newQuestions = await response.json();
            questions = [...questions, ...newQuestions];
            renderQuestions();
            alert(`Generated ${newQuestions.length} questions successfully!`);
        } else {
            const err = await response.text();
            alert("Error generating: " + err);
        }
    } catch (e) {
        console.error(e);
        alert("Failed to connect to backend. Is the Java Server running?");
    } finally {
        btn.innerHTML = originalText;
        btn.disabled = false;
    }
}

// Render Questions List
function renderQuestions() {
    const list = document.getElementById('questions-list');
    const badge = document.getElementById('q-badge');
    const startBtn = document.getElementById('start-session-btn');

    badge.textContent = questions.length;
    startBtn.disabled = questions.length === 0;

    if (questions.length === 0) {
        list.innerHTML = `
            <div class="empty-state">
                <div class="icon">📄</div>
                <p>No questions generated yet.</p>
            </div>`;
        return;
    }

    list.innerHTML = questions.map((q, idx) => `
        <div class="question-item">
            <div class="q-header">
                <span class="q-num">#${idx + 1}</span>
                <div class="q-actions">
                    <button class="icon-btn" onclick="editQuestion(${idx})">✏️</button>
                    <button class="icon-btn" onclick="deleteQuestion(${idx})">🗑️</button>
                    <span class="badge" style="background:var(--success-color)">${q.correct}</span>
                </div>
            </div>
            <div class="q-text">${q.text}</div>
            <div class="q-opts" style="font-size: 0.85rem; color: var(--text-muted);">
                A: ${q.optionA}<br>
                B: ${q.optionB}<br>
                C: ${q.optionC}<br>
                D: ${q.optionD}
            </div>
        </div>
    `).join('');
}

// Clear All
function clearAllQuestions() {
    if (confirm('Delete all questions?')) {
        questions = [];
        renderQuestions();
    }
}

// Delete Single
function deleteQuestion(idx) {
    questions.splice(idx, 1);
    renderQuestions();
}

// Edit Question
function editQuestion(idx) {
    const q = questions[idx];
    document.getElementById('edit-index').value = idx;
    document.getElementById('edit-text').value = q.text;
    document.getElementById('edit-opt-a').value = q.optionA;
    document.getElementById('edit-opt-b').value = q.optionB;
    document.getElementById('edit-opt-c').value = q.optionC;
    document.getElementById('edit-opt-d').value = q.optionD;
    document.getElementById('edit-correct').value = q.correct;

    document.getElementById('edit-modal').classList.add('active');
}

function closeModal() {
    document.getElementById('edit-modal').classList.remove('active');
}

function saveQuestionEdit() {
    const idx = document.getElementById('edit-index').value;
    const q = questions[idx];

    q.text = document.getElementById('edit-text').value;
    q.optionA = document.getElementById('edit-opt-a').value;
    q.optionB = document.getElementById('edit-opt-b').value;
    q.optionC = document.getElementById('edit-opt-c').value;
    q.optionD = document.getElementById('edit-opt-d').value;
    q.correct = document.getElementById('edit-correct').value;

    renderQuestions();
    closeModal();
}

// Add Manual Question
function addManually() {
    questions.push({
        text: "New Question",
        optionA: "Option A",
        optionB: "Option B",
        optionC: "Option C",
        optionD: "Option D",
        correct: "A",
        explanation: ""
    });
    renderQuestions();
    // Scroll to bottom
    setTimeout(() => {
        const list = document.getElementById('questions-list');
        list.scrollTop = list.scrollHeight;
        editQuestion(questions.length - 1);
    }, 100);
}

// Schedule Toggle
function toggleSchedule() {
    const checked = document.getElementById('schedule-check').checked;
    const inputs = document.getElementById('schedule-inputs');
    if (checked) {
        inputs.classList.remove('hidden');
    } else {
        inputs.classList.add('hidden');
    }
}

// Start Session
async function startSession() {
    if (questions.length === 0) return;

    const payload = {
        title: "Web Quiz " + new Date().toLocaleTimeString(),
        otp: "AUTO", // Backend ignores this usually and generates one, or we need to send?
        questions: questions.map(q => ({
            text: q.text,
            optionA: q.optionA || "A",
            optionB: q.optionB || "B",
            optionC: q.optionC || "C",
            optionD: q.optionD || "D",
            correct: q.correct || "A",
            explanation: q.explanation || ""
        })),
        numberOfSets: parseInt(document.getElementById('q-sets').value) || 1,
        durationMinutes: parseInt(document.getElementById('duration').value) || 60
    };

    // Schedule logic
    if (document.getElementById('schedule-check').checked) {
        const dt = document.getElementById('start-time').value;
        if (!dt) {
            alert("Please select a start time");
            return;
        }
        payload.startTime = dt; // Format 2023-12-21T10:00
    }

    try {
        const res = await fetch(`${API_BASE}/api/session/start`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const data = await res.json();
            // data = { sessionId: 123, otp: "123456" }
            currentSessionId = data.sessionId;
            currentOtp = data.otp;

            document.getElementById('disp-sess-id').textContent = currentSessionId;
            document.getElementById('disp-otp').textContent = currentOtp;
            document.getElementById('current-session-card').classList.remove('hidden');

            alert(`Session Started! ID: ${currentSessionId}, OTP: ${currentOtp}`);
            switchTab('active-sessions');

            // Also prep scoreboard
            document.getElementById('scoreboard-sess-id').value = currentSessionId;
        } else {
            alert("Error starting session: " + await res.text());
        }
    } catch (e) {
        console.error(e);
        alert("Network Error");
    }
}

// Active Sessions Management
async function loadSessions() {
    try {
        const res = await fetch(`${API_BASE}/api/session/active`);
        if (res.ok) {
            const sessions = await res.json();
            const tbody = document.getElementById('sessions-table-body');

            if (sessions.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center">No active sessions.</td></tr>';
                return;
            }

            tbody.innerHTML = sessions.map(s => `
                <tr>
                    <td>${s.id}</td>
                    <td>${s.title}</td>
                    <td><span class="badge" style="background:var(--success-color)">ACTIVE</span></td>
                    <td>${s.otp}</td>
                    <td>
                        <button class="btn-secondary" onclick="viewScoreboard(${s.id})">📊 Scoreboard</button>
                        <button class="btn-danger" onclick="stopSessionApi(${s.id})">Stop</button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

async function stopSessionApi(id) {
    if (!confirm(`Stop Session ${id}? Students will be kicked out.`)) return;

    try {
        const res = await fetch(`${API_BASE}/api/session/${id}/stop`, { method: 'POST' });
        if (res.ok) {
            alert("Session Stopped.");
            loadSessions();
            if (currentSessionId == id) {
                document.getElementById('current-session-card').classList.add('hidden');
                currentSessionId = null;
            }
        } else {
            alert("Failed to stop session.");
        }
    } catch (e) { console.error(e); }
}

function viewScoreboard(id) {
    switchTab('scoreboard');
    document.getElementById('scoreboard-sess-id').value = id;
    loadScoreboard();
}

function stopCurrentSession() {
    if (currentSessionId) stopSessionApi(currentSessionId);
}

// Copy Info
function copySessionInfo() {
    const text = `Join Quiz!\nSession ID: ${currentSessionId}\nOTP: ${currentOtp}`;
    navigator.clipboard.writeText(text);
    alert("Copied to clipboard!");
}

// Tab Listener to lazy load
document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        if (btn.dataset.tab === 'active-sessions') loadSessions();
    });
});


// Scoreboard
async function loadScoreboard() {
    const sid = document.getElementById('scoreboard-sess-id').value;
    if (!sid) {
        alert("Enter Session ID");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/session/${sid}/scoreboard`);
        if (res.ok) {
            const data = await res.json();
            renderScoreboard(data);
        } else {
            alert("Session not found or empty.");
        }
    } catch (e) {
        console.error(e);
    }
}

function renderScoreboard(data) {
    const tbody = document.getElementById('scoreboard-body');
    if (!data || data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No submissions yet.</td></tr>';
        return;
    }

    // Sort by score desc
    data.sort((a, b) => b.score - a.score);

    tbody.innerHTML = data.map((row, idx) => `
        <tr>
            <td>#${idx + 1}</td>
            <td>${row.studentName}</td>
            <td>${row.enrollment}</td>
            <td style="font-weight:bold; color: var(--primary-color)">${row.score}</td>
            <td>${new Date(row.submittedAt).toLocaleTimeString()}</td>
            <td class="${row.cheated ? 'status-cheated' : 'status-valid'}">
                ${row.cheated ? '⚠️ CHEATING' : 'Valid'}
            </td>
        </tr>
    `).join('');
}

// Initial Setup
document.addEventListener('DOMContentLoaded', () => {
    // Set default datetime to now
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    document.getElementById('start-time').value = now.toISOString().slice(0, 16);
});
