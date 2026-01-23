/**
 * API Client
 * Fetch wrapper for making HTTP requests to the backend API
 */

const API_BASE = '/api';

/**
 * API Client class for making HTTP requests
 */
class APIClient {
    constructor(baseURL = API_BASE) {
        this.baseURL = baseURL;
        this.defaultHeaders = {
            'Content-Type': 'application/json',
        };
    }

    /**
     * Make a fetch request
     * @param {string} endpoint - API endpoint
     * @param {object} options - Fetch options
     * @returns {Promise<Response>}
     */
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            headers: { ...this.defaultHeaders, ...options.headers },
            ...options,
        };

        try {
            const response = await fetch(url, config);
            return response;
        } catch (error) {
            console.error(`API Error: ${error.message}`);
            throw error;
        }
    }

    /**
     * GET request
     * @param {string} endpoint - API endpoint
     * @param {object} options - Fetch options
     * @returns {Promise<any>}
     */
    async get(endpoint, options = {}) {
        const response = await this.request(endpoint, {
            method: 'GET',
            ...options,
        });
        return this.handleResponse(response);
    }

    /**
     * POST request
     * @param {string} endpoint - API endpoint
     * @param {object} data - Request body
     * @param {object} options - Fetch options
     * @returns {Promise<any>}
     */
    async post(endpoint, data = {}, options = {}) {
        const response = await this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data),
            ...options,
        });
        return this.handleResponse(response);
    }

    /**
     * PUT request
     * @param {string} endpoint - API endpoint
     * @param {object} data - Request body
     * @param {object} options - Fetch options
     * @returns {Promise<any>}
     */
    async put(endpoint, data = {}, options = {}) {
        const response = await this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data),
            ...options,
        });
        return this.handleResponse(response);
    }

    /**
     * PATCH request
     * @param {string} endpoint - API endpoint
     * @param {object} data - Request body
     * @param {object} options - Fetch options
     * @returns {Promise<any>}
     */
    async patch(endpoint, data = {}, options = {}) {
        const response = await this.request(endpoint, {
            method: 'PATCH',
            body: JSON.stringify(data),
            ...options,
        });
        return this.handleResponse(response);
    }

    /**
     * DELETE request
     * @param {string} endpoint - API endpoint
     * @param {object} options - Fetch options
     * @returns {Promise<any>}
     */
    async delete(endpoint, options = {}) {
        const response = await this.request(endpoint, {
            method: 'DELETE',
            ...options,
        });
        return this.handleResponse(response);
    }

    /**
     * Handle API response
     * @param {Response} response - Fetch response
     * @returns {Promise<any>}
     */
    async handleResponse(response) {
        const contentType = response.headers.get('content-type');
        let data;

        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) {
            const error = new Error(data.message || `HTTP ${response.status}`);
            error.status = response.status;
            error.data = data;
            throw error;
        }

        return data;
    }
}

// Create a singleton instance
const api = new APIClient();

/**
 * Student API endpoints
 */
const studentAPI = {
    /**
     * Register and join a session
     * @param {object} data - Registration data
     * @returns {Promise<any>}
     */
    register: (data) => api.post('/student/register', data),

    /**
     * Get student profile
     * @param {string} studentId - Student ID
     * @returns {Promise<any>}
     */
    getProfile: (studentId) => api.get(`/student/${studentId}`),

    /**
     * Update student profile
     * @param {string} studentId - Student ID
     * @param {object} data - Profile data
     * @returns {Promise<any>}
     */
    updateProfile: (studentId, data) => api.put(`/student/${studentId}`, data),
};

/**
 * Session API endpoints
 */
const sessionAPI = {
    /**
     * Get session status
     * @param {string} sessionId - Session ID
     * @returns {Promise<any>}
     */
    getStatus: (sessionId) => api.get(`/session/${sessionId}/status`),

    /**
     * Get session questions
     * @param {string} sessionId - Session ID
     * @param {object} params - Query parameters
     * @returns {Promise<any>}
     */
    getQuestions: (sessionId, params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        return api.get(`/session/${sessionId}/questions${queryString ? '?' + queryString : ''}`);
    },

    /**
     * Get session details
     * @param {string} sessionId - Session ID
     * @returns {Promise<any>}
     */
    getDetails: (sessionId) => api.get(`/session/${sessionId}`),

    /**
     * Get session scoreboard
     * @param {string} sessionId - Session ID
     * @returns {Promise<any>}
     */
    getScoreboard: (sessionId) => api.get(`/session/${sessionId}/scoreboard`),

    /**
     * Get all sessions
     * @param {object} params - Query parameters
     * @returns {Promise<any>}
     */
    getAll: (params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        return api.get(`/session${queryString ? '?' + queryString : ''}`);
    },

    /**
     * Create a new session
     * @param {object} data - Session data
     * @returns {Promise<any>}
     */
    create: (data) => api.post('/session', data),

    /**
     * Update session
     * @param {string} sessionId - Session ID
     * @param {object} data - Session data
     * @returns {Promise<any>}
     */
    update: (sessionId, data) => api.put(`/session/${sessionId}`, data),

    /**
     * Delete session
     * @param {string} sessionId - Session ID
     * @returns {Promise<any>}
     */
    delete: (sessionId) => api.delete(`/session/${sessionId}`),

    /**
     * Start session
     * @param {string} sessionId - Session ID
     * @returns {Promise<any>}
     */
    start: (sessionId) => api.post(`/session/${sessionId}/start`),

    /**
     * End session
     * @param {string} sessionId - Session ID
     * @returns {Promise<any>}
     */
    end: (sessionId) => api.post(`/session/${sessionId}/end`),
};

/**
 * Quiz API endpoints
 */
const quizAPI = {
    /**
     * Submit quiz answers
     * @param {object} data - Quiz submission data
     * @returns {Promise<any>}
     */
    submit: (data) => api.post('/quiz/submit', data),

    /**
     * Get quiz results
     * @param {string} quizId - Quiz ID
     * @returns {Promise<any>}
     */
    getResults: (quizId) => api.get(`/quiz/${quizId}/results`),

    /**
     * Get student quiz results
     * @param {string} studentId - Student ID
     * @param {string} sessionId - Session ID
     * @returns {Promise<any>}
     */
    getStudentResults: (studentId, sessionId) => 
        api.get(`/quiz/student/${studentId}/session/${sessionId}/results`),

    /**
     * Create quiz
     * @param {object} data - Quiz data
     * @returns {Promise<any>}
     */
    create: (data) => api.post('/quiz', data),

    /**
     * Update quiz
     * @param {string} quizId - Quiz ID
     * @param {object} data - Quiz data
     * @returns {Promise<any>}
     */
    update: (quizId, data) => api.put(`/quiz/${quizId}`, data),

    /**
     * Delete quiz
     * @param {string} quizId - Quiz ID
     * @returns {Promise<any>}
     */
    delete: (quizId) => api.delete(`/quiz/${quizId}`),

    /**
     * Get all quizzes
     * @param {object} params - Query parameters
     * @returns {Promise<any>}
     */
    getAll: (params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        return api.get(`/quiz${queryString ? '?' + queryString : ''}`);
    },
};

/**
 * Question API endpoints
 */
const questionAPI = {
    /**
     * Get question by ID
     * @param {string} questionId - Question ID
     * @returns {Promise<any>}
     */
    get: (questionId) => api.get(`/question/${questionId}`),

    /**
     * Get all questions
     * @param {object} params - Query parameters
     * @returns {Promise<any>}
     */
    getAll: (params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        return api.get(`/question${queryString ? '?' + queryString : ''}`);
    },

    /**
     * Create question
     * @param {object} data - Question data
     * @returns {Promise<any>}
     */
    create: (data) => api.post('/question', data),

    /**
     * Update question
     * @param {string} questionId - Question ID
     * @param {object} data - Question data
     * @returns {Promise<any>}
     */
    update: (questionId, data) => api.put(`/question/${questionId}`, data),

    /**
     * Delete question
     * @param {string} questionId - Question ID
     * @returns {Promise<any>}
     */
    delete: (questionId) => api.delete(`/question/${questionId}`),
};

/**
 * Faculty API endpoints
 */
const facultyAPI = {
    /**
     * Get faculty profile
     * @param {string} facultyId - Faculty ID
     * @returns {Promise<any>}
     */
    getProfile: (facultyId) => api.get(`/faculty/${facultyId}`),

    /**
     * Update faculty profile
     * @param {string} facultyId - Faculty ID
     * @param {object} data - Profile data
     * @returns {Promise<any>}
     */
    updateProfile: (facultyId, data) => api.put(`/faculty/${facultyId}`, data),

    /**
     * Get faculty sessions
     * @param {string} facultyId - Faculty ID
     * @returns {Promise<any>}
     */
    getSessions: (facultyId) => api.get(`/faculty/${facultyId}/sessions`),

    /**
     * Get faculty analytics
     * @param {string} facultyId - Faculty ID
     * @param {object} params - Query parameters
     * @returns {Promise<any>}
     */
    getAnalytics: (facultyId, params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        return api.get(`/faculty/${facultyId}/analytics${queryString ? '?' + queryString : ''}`);
    },
};

/**
 * Authentication API endpoints
 */
const authAPI = {
    /**
     * Login
     * @param {object} data - Login credentials
     * @returns {Promise<any>}
     */
    login: (data) => api.post('/auth/login', data),

    /**
     * Logout
     * @returns {Promise<any>}
     */
    logout: () => api.post('/auth/logout'),

    /**
     * Register
     * @param {object} data - Registration data
     * @returns {Promise<any>}
     */
    register: (data) => api.post('/auth/register', data),

    /**
     * Forgot password
     * @param {object} data - Email data
     * @returns {Promise<any>}
     */
    forgotPassword: (data) => api.post('/auth/forgot-password', data),

    /**
     * Reset password
     * @param {object} data - Reset data
     * @returns {Promise<any>}
     */
    resetPassword: (data) => api.post('/auth/reset-password', data),

    /**
     * Verify email
     * @param {object} data - Verification data
     * @returns {Promise<any>}
     */
    verifyEmail: (data) => api.post('/auth/verify-email', data),

    /**
     * Refresh token
     * @returns {Promise<any>}
     */
    refreshToken: () => api.post('/auth/refresh-token'),
};

/**
 * Analytics API endpoints
 */
const analyticsAPI = {
    /**
     * Get dashboard analytics
     * @param {object} params - Query parameters
     * @returns {Promise<any>}
     */
    getDashboard: (params = {}) => {
        const queryString = new URLSearchParams(params).toString();
        return api.get(`/analytics/dashboard${queryString ? '?' + queryString : ''}`);
    },

    /**
     * Get session analytics
     * @param {string} sessionId - Session ID
     * @returns {Promise<any>}
     */
    getSession: (sessionId) => api.get(`/analytics/session/${sessionId}`),

    /**
     * Get question analytics
     * @param {string} questionId - Question ID
     * @returns {Promise<any>}
     */
    getQuestion: (questionId) => api.get(`/analytics/question/${questionId}`),

    /**
     * Get student analytics
     * @param {string} studentId - Student ID
     * @returns {Promise<any>}
     */
    getStudent: (studentId) => api.get(`/analytics/student/${studentId}`),
};

