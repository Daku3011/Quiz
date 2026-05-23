function validateEmail(email) {
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
    return sessionId.trim().length >= 4;
}

function validateOTP(otp) {
    return otp.trim().length >= 4;
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = { validateEmail, validatePassword, validateName, validateEnrollment, validateSessionId, validateOTP };
}
