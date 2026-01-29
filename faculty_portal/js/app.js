const API_BASE = (typeof CONFIG !== 'undefined' && CONFIG.API_BASE_URL) 
    ? CONFIG.API_BASE_URL 
    : (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' 
        ? 'http://localhost:9090' 
        : `http://${window.location.hostname}:9090`); // Fallback

console.log('Faculty App.js loaded');

// State
let questions = [];
let currentSessionId = null;
let currentOtp = null;
let currentUser = null;

// Auth Logic
function checkAuth() {
    const user = sessionStorage.getItem('faculty_user');
    if (!user) {
        // If not on login page, redirect
        if (!window.location.href.includes('login.html')) {
            window.location.href = 'login.html';
        }
    } else {
        currentUser = JSON.parse(user);
        // If on login page, redirect to index
        if (window.location.href.includes('login.html')) {
            window.location.href = 'index.html';
        }
        updateProfileUI();
    }
}

async function login(username, password) {
    try {
        const res = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (res.ok) {
            const data = await res.json();
            if (data.status === 'success' && (data.role === 'ADMIN' || data.role === 'FACULTY')) {
                sessionStorage.setItem('faculty_user', JSON.stringify({
                    username: data.username,
                    role: data.role
                }));
                return true;
            }
        }
        return false;
    } catch (e) {
        console.error("Login Check Failed", e);
        throw e;
    }
}
window.login = login;

function logout() {
    if (confirm("Are you sure you want to logout?")) {
        sessionStorage.removeItem('faculty_user');
        window.location.href = 'login.html';
    }
}

function updateProfileUI() {
    const items = document.querySelectorAll('.user-profile .name');
    const roles = document.querySelectorAll('.user-profile .role');

    if (items.length > 0 && currentUser) {
        items.forEach(el => el.textContent = currentUser.username);
        roles.forEach(el => el.textContent = currentUser.role);
    }
}


// Tab Switching
function switchTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.nav-btn').forEach(el => el.classList.remove('active'));

    document.getElementById(tabId).classList.add('active');
    document.querySelector(`button[data-tab="${tabId}"]`).classList.add('active');
}

// File Upload (Text Only)
const fileInput = document.getElementById('file-upload');
if (fileInput) {
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Display File Details
        const detailsDiv = document.getElementById('file-details');
        if (detailsDiv) {
            const size = (file.size / 1024).toFixed(2);
            detailsDiv.innerHTML = `<strong>Selected:</strong> ${file.name} <br>
                                  <small>Type: ${file.type || 'Unknown'} | Size: ${size} KB</small>`;
            detailsDiv.classList.remove('hidden');
        }

        if (file.type === 'application/pdf') {
            const fileReader = new FileReader();
            fileReader.onload = async function () {
                const typedarray = new Uint8Array(this.result);
                try {
                    const pdf = await pdfjsLib.getDocument(typedarray).promise;
                    let fullText = '';

                    // Show loading
                    document.getElementById('file-details').innerHTML += '<br><span>⏳ Parsing PDF...</span>';

                    for (let i = 1; i <= pdf.numPages; i++) {
                        const page = await pdf.getPage(i);
                        const textContent = await page.getTextContent();
                        const pageText = textContent.items.map(item => item.str).join(' ');
                        fullText += pageText + '\n\n';
                    }

                    document.getElementById('syllabus-text').value = fullText;
                    document.getElementById('file-details').innerHTML += ' <span style="color:var(--success-color)">✅ Done!</span>';
                } catch (error) {
                    console.error(error);
                    alert("Error parsing PDF: " + error.message);
                }
            };
            fileReader.readAsArrayBuffer(file);
        } else if (file.type === 'text/plain') {
            const reader = new FileReader();
            reader.onload = (e) => {
                document.getElementById('syllabus-text').value = e.target.result;
            };
            reader.readAsText(file);
        } else {
            alert("Supported formats: .txt and .pdf only.");
        }
    });
}

// Clear Syllabus
function clearSyllabus() {
    document.getElementById('syllabus-text').value = '';
    fileInput.value = '';
    const details = document.getElementById('file-details');
    if (details) {
        details.innerHTML = '';
        details.classList.add('hidden');
    }
    const weightsContainer = document.getElementById('weights-container');
    if (weightsContainer) {
        weightsContainer.classList.add('hidden');
        document.getElementById('weights-list').innerHTML = '';
    }
}

// Analyze Syllabus for Chapters and Weights
async function analyzeSyllabus() {
    const text = document.getElementById('syllabus-text').value;
    if (!text.trim()) {
        alert("Please enter or upload syllabus text first.");
        return;
    }

    const btn = document.getElementById('analyze-btn');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<span class="btn-icon">⏳</span> Analyzing...';
    btn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/api/syllabus/analyze`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ text: text })
        });

        if (response.ok) {
            const data = await response.json();
            // data = { credits: "...", hours: "...", chapters: [{name: "...", weight: 20}, ...] }
            renderWeights(data.chapters || []);
            document.getElementById('weights-container').classList.remove('hidden');
            
            // Optionally update UI with credits/hours
            if (data.credits || data.hours) {
                const detailsDiv = document.getElementById('file-details');
                detailsDiv.classList.remove('hidden');
                detailsDiv.innerHTML += `<br><strong>Extracted:</strong> ${data.credits ? data.credits + ' Credits' : ''} ${data.hours ? '| ' + data.hours + ' Hours' : ''}`;
            }
        } else {
            alert("Analysis failed: " + await response.text());
        }
    } catch (e) {
        console.error(e);
        alert("Error connecting to backend for analysis.");
    } finally {
        btn.innerHTML = originalText;
        btn.disabled = false;
    }
}

function renderWeights(chapters) {
    const list = document.getElementById('weights-list');
    list.innerHTML = chapters.map((ch, idx) => {
        const shortName = ch.name.length > 50 ? ch.name.substring(0, 47) + '...' : ch.name;
        return `
            <div class="form-group">
                <label style="font-size: 0.75rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${ch.name}">${shortName} <span class="badge" style="background:#eee; color:#555; font-size:0.7em;">${ch.mappedCO || 'CO?'}</span></label>
                <input type="number" class="chapter-weight-input" data-name="${ch.name}" data-co="${ch.mappedCO || ''}" value="${ch.weight}" min="0" max="100" onchange="updateTotalWeight()">
            </div>
        `;
    }).join('');
    updateTotalWeight();
}

function updateTotalWeight() {
    const inputs = document.querySelectorAll('.chapter-weight-input');
    let total = 0;
    inputs.forEach(input => total += parseInt(input.value) || 0);
    const totalEl = document.getElementById('total-weight');
    if (totalEl) {
        totalEl.textContent = total;
        totalEl.style.color = (total === 100) ? 'var(--success-color)' : 'var(--danger-color)';
    }
}

function getWeights() {
    const inputs = document.querySelectorAll('.chapter-weight-input');
    const weights = [];
    inputs.forEach(input => {
        weights.push({
            name: input.dataset.name,
            weight: parseInt(input.value) || 0,
            mappedCO: input.dataset.co || null 
        });
    });
    return weights;
}

// Generate Questions
async function generateQuestions() {
    const text = document.getElementById('syllabus-text').value;
    const count = document.getElementById('q-count').value;
    const weights = getWeights();

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
            body: JSON.stringify({ text: text, count: count, weights: weights })
        });

        if (response.ok) {
            const newQuestions = await response.json();
            questions = [...questions, ...newQuestions];
            populateFilters();
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
function renderQuestions(listToRender = null) {
    const list = document.getElementById('questions-list');
    const badge = document.getElementById('q-badge');
    const startBtn = document.getElementById('start-session-btn');

    // Use passed list or default to all questions
    const data = listToRender || questions;

    badge.textContent = data.length;
    // Only disable start button if GLOBAL list is empty, not just filtered view
    startBtn.disabled = questions.length === 0;

    if (data.length === 0) {
        list.innerHTML = `
            <div class="empty-state">
                <div class="icon">📄</div>
                <p>No questions found.</p>
            </div>`;
        return;
    }

    list.innerHTML = data.map((q, idx) => {
        // We need to find the original index for editing/deleting if we are in a filtered view
        // But for simplicity, let's pass the object logic. 
        // Actually, existing edit/delete uses index. We need to handle that.
        // Simple fix: Store original index in data if filtered? 
        // OR: Just find index in global 'questions' array by object reference.
        const realIndex = questions.indexOf(q);
        
        return `
        <div class="question-item">
            <div class="q-header">
                <span class="q-num">#${idx + 1} <small class="text-muted">(${q.courseOutcome || '-'})</small></span>
                <span class="badge" style="background: #eee; color: #333; margin-left: auto; margin-right: 10px;">${q.chapter || ' General'}</span>
                <div class="q-actions">
                    <button class="icon-btn" onclick="editQuestion(${realIndex})">✏️</button>
                    <button class="icon-btn" onclick="deleteQuestion(${realIndex})">🗑️</button>
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
    `}).join('');
}

function populateFilters() {
    const chapterSet = new Set();
    const coSet = new Set();

    questions.forEach(q => {
        if (q.chapter) chapterSet.add(q.chapter);
        if (q.courseOutcome) coSet.add(q.courseOutcome);
    });

    const chSelect = document.getElementById('filter-chapter');
    const coSelect = document.getElementById('filter-co');

    // Save current selection to restore if possible
    const currentCh = chSelect.value;
    const currentCo = coSelect.value;

    chSelect.innerHTML = '<option value="">All Chapters</option>' + 
        Array.from(chapterSet).sort().map(c => `<option value="${c}">${c}</option>`).join('');
    
    coSelect.innerHTML = '<option value="">All COs</option>' + 
        Array.from(coSet).sort().map(c => `<option value="${c}">${c}</option>`).join('');

    if (chapterSet.has(currentCh)) chSelect.value = currentCh;
    if (coSet.has(currentCo)) coSelect.value = currentCo;
}

function applyFilters() {
    const ch = document.getElementById('filter-chapter').value;
    const co = document.getElementById('filter-co').value;

    const filtered = questions.filter(q => {
        const matchCh = ch === "" || q.chapter === ch;
        const matchCo = co === "" || q.courseOutcome === co;
        return matchCh && matchCo;
    });

    renderQuestions(filtered);
}

// Clear All
function clearAllQuestions() {
    if (confirm('Delete all questions?')) {
        questions = [];
        populateFilters();
        renderQuestions();
    }
}

// Delete Single
function deleteQuestion(idx) {
    questions.splice(idx, 1);
    populateFilters();
    applyFilters(); // Re-render with current filters
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
    populateFilters();
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

            // Persist session state
            localStorage.setItem('activeSession', JSON.stringify({ sessionId: currentSessionId, otp: currentOtp }));

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
                    <td>${s.otpDetails ? s.otpDetails.code : 'Hidden'}</td>
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
            loadSessions();
            if (currentSessionId == id) {
                document.getElementById('current-session-card').classList.add('hidden');
                currentSessionId = null;
                localStorage.removeItem('activeSession');
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
    // Auth Check
    checkAuth();

    if (window.location.href.includes('login.html')) return; // Stop here if login page

    // Set default datetime to now
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    document.getElementById('start-time').value = now.toISOString().slice(0, 16);

    // Restore active session if exists
    const storedSession = localStorage.getItem('activeSession');
    if (storedSession) {
        try {
            const sess = JSON.parse(storedSession);
            currentSessionId = sess.sessionId;
            currentOtp = sess.otp;

            document.getElementById('disp-sess-id').textContent = currentSessionId;
            document.getElementById('disp-otp').textContent = currentOtp;
            document.getElementById('current-session-card').classList.remove('hidden');
            
            // Optional: Switch to active sessions tab if restoring
            switchTab('active-sessions');
            loadSessions();
        } catch (e) {
            console.error("Failed to restore session", e);
            localStorage.removeItem('activeSession');
        }
    }
});
