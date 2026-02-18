window.CONFIG = {
    // API_BASE_URL: 'http://localhost:9090' // Local Development
    API_BASE_URL: (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
        ? 'http://localhost:9090'
        : 'https://quiz-backend-cdxz.onrender.com'
};
