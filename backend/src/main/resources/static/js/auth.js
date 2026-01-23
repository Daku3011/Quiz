/**
 * Authentication Module
 * Handles user authentication, session management, and authorization
 */

class AuthManager {
    constructor() {
        this.user = null;
        this.token = null;
        this.isAuthenticated = false;
        this.userRole = null;
        this.loadFromStorage();
    }

    /**
     * Load authentication data from storage
     */
    loadFromStorage() {
        const stored = localStorage.getItem('auth');
        if (stored) {
            try {
                const auth = JSON.parse(stored);
                this.user = auth.user;
                this.token = auth.token;
                this.userRole = auth.userRole;
                this.isAuthenticated = !!this.token;
            } catch (error) {
                console.error('Failed to load auth from storage:', error);
                this.clearAuth();
            }
        }
    }

    /**
     * Save authentication data to storage
     */
    saveToStorage() {
        localStorage.setItem('auth', JSON.stringify({
            user: this.user,
            token: this.token,
            userRole: this.userRole,
        }));
    }

    /**
     * Clear authentication data
     */
    clearAuth() {
        this.user = null;
        this.token = null;
        this.isAuthenticated = false;
        this.userRole = null;
        localStorage.removeItem('auth');
        sessionStorage.clear();
    }

    /**
     * Login user
     * @param {string} email - User email
     * @param {string} password - User password
     * @returns {Promise<object>}
     */
    async login(email, password) {
        try {
            const response = await authAPI.login({ email, password });
            
            this.user = response.user;
            this.token = response.token;
            this.userRole = response.user.role;
            this.isAuthenticated = true;
            
            this.saveToStorage();
            this.setAuthHeader();
            
            return response;
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        }
    }

    /**
     * Logout user
     * @returns {Promise<void>}
     */
    async logout() {
        try {
            await authAPI.logout();
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            this.clearAuth();
        }
    }

    /**
     * Register new user
     * @param {object} data - Registration data
     * @returns {Promise<object>}
     */
    async register(data) {
        try {
            const response = await authAPI.register(data);
            
            this.user = response.user;
            this.token = response.token;
            this.userRole = response.user.role;
            this.isAuthenticated = true;
            
            this.saveToStorage();
            this.setAuthHeader();
            
            return response;
        } catch (error) {
            console.error('Registration failed:', error);
            throw error;
        }
    }

    /**
     * Forgot password
     * @param {string} email - User email
     * @returns {Promise<object>}
     */
    async forgotPassword(email) {
        try {
            const response = await authAPI.forgotPassword({ email });
            return response;
        } catch (error) {
            console.error('Forgot password failed:', error);
            throw error;
        }
    }

    /**
     * Reset password
     * @param {string} token - Reset token
     * @param {string} password - New password
     * @returns {Promise<object>}
     */
    async resetPassword(token, password) {
        try {
            const response = await authAPI.resetPassword({ token, password });
            return response;
        } catch (error) {
            console.error('Reset password failed:', error);
            throw error;
        }
    }

    /**
     * Verify email
     * @param {string} token - Verification token
     * @returns {Promise<object>}
     */
    async verifyEmail(token) {
        try {
            const response = await authAPI.verifyEmail({ token });
            return response;
        } catch (error) {
            console.error('Email verification failed:', error);
            throw error;
        }
    }

    /**
     * Refresh authentication token
     * @returns {Promise<object>}
     */
    async refreshToken() {
        try {
            const response = await authAPI.refreshToken();
            
            this.token = response.token;
            this.saveToStorage();
            this.setAuthHeader();
            
            return response;
        } catch (error) {
            console.error('Token refresh failed:', error);
            this.clearAuth();
            throw error;
        }
    }

    /**
     * Set authorization header for API requests
     */
    setAuthHeader() {
        if (this.token) {
            api.defaultHeaders['Authorization'] = `Bearer ${this.token}`;
        } else {
            delete api.defaultHeaders['Authorization'];
        }
    }

    /**
     * Check if user is authenticated
     * @returns {boolean}
     */
    isLoggedIn() {
        return this.isAuthenticated && !!this.token;
    }

    /**
     * Check if user has a specific role
     * @param {string} role - Role to check
     * @returns {boolean}
     */
    hasRole(role) {
        return this.userRole === role;
    }

    /**
     * Check if user is student
     * @returns {boolean}
     */
    isStudent() {
        return this.hasRole('STUDENT');
    }

    /**
     * Check if user is faculty
     * @returns {boolean}
     */
    isFaculty() {
        return this.hasRole('FACULTY');
    }

    /**
     * Check if user is admin
     * @returns {boolean}
     */
    isAdmin() {
        return this.hasRole('ADMIN');
    }

    /**
     * Get current user
     * @returns {object|null}
     */
    getCurrentUser() {
        return this.user;
    }

    /**
     * Get user ID
     * @returns {string|null}
     */
    getUserId() {
        return this.user?.id || null;
    }

    /**
     * Get user email
     * @returns {string|null}
     */
    getUserEmail() {
        return this.user?.email || null;
    }

    /**
     * Get user name
     * @returns {string|null}
     */
    getUserName() {
        return this.user?.name || null;
    }

    /**
     * Get authentication token
     * @returns {string|null}
     */
    getToken() {
        return this.token;
    }

    /**
     * Update user profile
     * @param {object} data - Profile data
     * @returns {Promise<object>}
     */
    async updateProfile(data) {
        try {
            const response = await api.put(`/user/profile`, data);
            this.user = { ...this.user, ...response };
            this.saveToStorage();
            return response;
        } catch (error) {
            console.error('Profile update failed:', error);
            throw error;
        }
    }

    /**
     * Change password
     * @param {string} currentPassword - Current password
     * @param {string} newPassword - New password
     * @returns {Promise<object>}
     */
    async changePassword(currentPassword, newPassword) {
        try {
            const response = await api.post(`/user/change-password`, {
                currentPassword,
                newPassword,
            });
            return response;
        } catch (error) {
            console.error('Password change failed:', error);
            throw error;
        }
    }

    /**
     * Enable two-factor authentication
     * @returns {Promise<object>}
     */
    async enableTwoFactor() {
        try {
            const response = await api.post(`/user/2fa/enable`);
            return response;
        } catch (error) {
            console.error('2FA enable failed:', error);
            throw error;
        }
    }

    /**
     * Disable two-factor authentication
     * @returns {Promise<object>}
     */
    async disableTwoFactor() {
        try {
            const response = await api.post(`/user/2fa/disable`);
            return response;
        } catch (error) {
            console.error('2FA disable failed:', error);
            throw error;
        }
    }

    /**
     * Verify two-factor authentication code
     * @param {string} code - 2FA code
     * @returns {Promise<object>}
     */
    async verify2FA(code) {
        try {
            const response = await api.post(`/user/2fa/verify`, { code });
            return response;
        } catch (error) {
            console.error('2FA verification failed:', error);
            throw error;
        }
    }
}

// Create a singleton instance
const auth = new AuthManager();

/**
 * Check if user is authenticated before accessing protected routes
 * @returns {boolean}
 */
function requireAuth() {
    if (!auth.isLoggedIn()) {
        window.location.href = '/login';
        return false;
    }
    return true;
}

/**
 * Check if user has required role
 * @param {string} role - Required role
 * @returns {boolean}
 */
function requireRole(role) {
    if (!auth.isLoggedIn()) {
        window.location.href = '/login';
        return false;
    }
    
    if (!auth.hasRole(role)) {
        window.location.href = '/unauthorized';
        return false;
    }
    
    return true;
}

/**
 * Redirect to login if not authenticated
 */
function redirectIfNotAuthenticated() {
    if (!auth.isLoggedIn()) {
        window.location.href = '/login';
    }
}

/**
 * Redirect to home if already authenticated
 */
function redirectIfAuthenticated() {
    if (auth.isLoggedIn()) {
        if (auth.isStudent()) {
            window.location.href = '/student/dashboard';
        } else if (auth.isFaculty()) {
            window.location.href = '/faculty/dashboard';
        } else {
            window.location.href = '/dashboard';
        }
    }
}

