window.CONFIG = (() => {
    const hostname = window.location.hostname;

    // Check if running locally: localhost, 127.0.0.1, or any private/LAN IP
    const isLocal = (
        hostname === 'localhost' ||
        hostname === '127.0.0.1' ||
        hostname.startsWith('192.168.') ||
        hostname.startsWith('10.') ||
        /^172\.(1[6-9]|2[0-9]|3[0-1])\./.test(hostname)
    );

    return {
        API_BASE_URL: isLocal
            ? `http://${hostname}:9090`
            : 'https://quiz-backend-cdxz.onrender.com'
    };
})();
