const CONFIG = {
    // API_BASE_URL: 'http://localhost:8080' // Local Development
    API_BASE_URL: window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' 
        ? 'http://localhost:9090' 
        : 'https://quiz-31dy.onrender.com' // Placeholder for Render URL
};

// Auto-detect if we are in production (you can change this logic)
if (window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
    // You will need to update this URL after deploying your backend to Render
    // Or we can leave it dynamic if the frontend and backend are on the same domain (unlikely for separated repos)
}
