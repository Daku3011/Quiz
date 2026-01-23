/**
 * Utility Functions
 * Common helper functions for the application
 */

/**
 * Format date to readable string
 * @param {Date|string} date - Date to format
 * @param {string} format - Format string (default: 'MM/DD/YYYY')
 * @returns {string}
 */
function formatDate(date, format = 'MM/DD/YYYY') {
    if (typeof date === 'string') {
        date = new Date(date);
    }

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return format
        .replace('DD', day)
        .replace('MM', month)
        .replace('YYYY', year)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds);
}

/**
 * Format time duration
 * @param {number} seconds - Duration in seconds
 * @returns {string}
 */
function formatTime(seconds) {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    if (hours > 0) {
        return `${hours}:${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
    }
    return `${minutes}:${String(secs).padStart(2, '0')}`;
}

/**
 * Format number with commas
 * @param {number} num - Number to format
 * @returns {string}
 */
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

/**
 * Format currency
 * @param {number} amount - Amount to format
 * @param {string} currency - Currency code (default: 'USD')
 * @returns {string}
 */
function formatCurrency(amount, currency = 'USD') {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: currency,
    }).format(amount);
}

/**
 * Format percentage
 * @param {number} value - Value to format
 * @param {number} decimals - Number of decimal places
 * @returns {string}
 */
function formatPercentage(value, decimals = 2) {
    return `${(value * 100).toFixed(decimals)}%`;
}

/**
 * Validate email
 * @param {string} email - Email to validate
 * @returns {boolean}
 */
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * Validate password strength
 * @param {string} password - Password to validate
 * @returns {object}
 */
function validatePassword(password) {
    const result = {
        isValid: true,
        strength: 'weak',
        errors: [],
    };

    if (password.length < 8) {
        result.errors.push('Password must be at least 8 characters');
        result.isValid = false;
    }

    if (!/[A-Z]/.test(password)) {
        result.errors.push('Password must contain uppercase letter');
        result.isValid = false;
    }

    if (!/[a-z]/.test(password)) {
        result.errors.push('Password must contain lowercase letter');
        result.isValid = false;
    }

    if (!/[0-9]/.test(password)) {
        result.errors.push('Password must contain number');
        result.isValid = false;
    }

    if (!/[!@#$%^&*]/.test(password)) {
        result.errors.push('Password must contain special character');
        result.isValid = false;
    }

    if (result.isValid) {
        result.strength = 'strong';
    } else if (password.length >= 8) {
        result.strength = 'medium';
    }

    return result;
}

/**
 * Validate phone number
 * @param {string} phone - Phone number to validate
 * @returns {boolean}
 */
function isValidPhone(phone) {
    const phoneRegex = /^[\d\s\-\+\(\)]+$/;
    return phoneRegex.test(phone) && phone.replace(/\D/g, '').length >= 10;
}

/**
 * Validate URL
 * @param {string} url - URL to validate
 * @returns {boolean}
 */
function isValidURL(url) {
    try {
        new URL(url);
        return true;
    } catch {
        return false;
    }
}

/**
 * Debounce function
 * @param {function} func - Function to debounce
 * @param {number} delay - Delay in milliseconds
 * @returns {function}
 */
function debounce(func, delay = 300) {
    let timeoutId;
    return function (...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(this, args), delay);
    };
}

/**
 * Throttle function
 * @param {function} func - Function to throttle
 * @param {number} limit - Limit in milliseconds
 * @returns {function}
 */
function throttle(func, limit = 300) {
    let inThrottle;
    return function (...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => (inThrottle = false), limit);
        }
    };
}

/**
 * Deep clone object
 * @param {object} obj - Object to clone
 * @returns {object}
 */
function deepClone(obj) {
    if (obj === null || typeof obj !== 'object') {
        return obj;
    }

    if (obj instanceof Date) {
        return new Date(obj.getTime());
    }

    if (obj instanceof Array) {
        return obj.map(item => deepClone(item));
    }

    if (obj instanceof Object) {
        const clonedObj = {};
        for (const key in obj) {
            if (obj.hasOwnProperty(key)) {
                clonedObj[key] = deepClone(obj[key]);
            }
        }
        return clonedObj;
    }
}

/**
 * Merge objects
 * @param {object} target - Target object
 * @param {object} source - Source object
 * @returns {object}
 */
function mergeObjects(target, source) {
    const result = { ...target };
    for (const key in source) {
        if (source.hasOwnProperty(key)) {
            if (typeof source[key] === 'object' && source[key] !== null) {
                result[key] = mergeObjects(result[key] || {}, source[key]);
            } else {
                result[key] = source[key];
            }
        }
    }
    return result;
}

/**
 * Get query parameter from URL
 * @param {string} param - Parameter name
 * @returns {string|null}
 */
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

/**
 * Set query parameter in URL
 * @param {string} param - Parameter name
 * @param {string} value - Parameter value
 */
function setQueryParam(param, value) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set(param, value);
    window.history.replaceState({}, '', `${window.location.pathname}?${urlParams}`);
}

/**
 * Remove query parameter from URL
 * @param {string} param - Parameter name
 */
function removeQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.delete(param);
    window.history.replaceState({}, '', `${window.location.pathname}?${urlParams}`);
}

/**
 * Generate random string
 * @param {number} length - Length of string
 * @returns {string}
 */
function generateRandomString(length = 10) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

/**
 * Generate UUID
 * @returns {string}
 */
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = (Math.random() * 16) | 0;
        const v = c === 'x' ? r : (r & 0x3) | 0x8;
        return v.toString(16);
    });
}

/**
 * Copy text to clipboard
 * @param {string} text - Text to copy
 * @returns {Promise<void>}
 */
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
    } catch (error) {
        console.error('Failed to copy to clipboard:', error);
        throw error;
    }
}

/**
 * Download file
 * @param {string} url - File URL
 * @param {string} filename - File name
 */
function downloadFile(url, filename) {
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

/**
 * Show notification
 * @param {string} message - Notification message
 * @param {string} type - Notification type (success, error, warning, info)
 * @param {number} duration - Duration in milliseconds
 */
function showNotification(message, type = 'info', duration = 3000) {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.textContent = message;
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.zIndex = '9999';
    notification.style.minWidth = '300px';

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, duration);
}

/**
 * Show loading spinner
 * @param {string} message - Loading message
 * @returns {HTMLElement}
 */
function showLoading(message = 'Loading...') {
    const loader = document.createElement('div');
    loader.className = 'spinner';
    loader.innerHTML = `
        <div style="text-align: center; margin-top: 20px;">
            <div class="spinner"></div>
            <p>${message}</p>
        </div>
    `;
    document.body.appendChild(loader);
    return loader;
}

/**
 * Hide loading spinner
 * @param {HTMLElement} loader - Loader element
 */
function hideLoading(loader) {
    if (loader) {
        loader.remove();
    }
}

/**
 * Capitalize string
 * @param {string} str - String to capitalize
 * @returns {string}
 */
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * Truncate string
 * @param {string} str - String to truncate
 * @param {number} length - Max length
 * @returns {string}
 */
function truncate(str, length = 50) {
    return str.length > length ? str.substring(0, length) + '...' : str;
}

/**
 * Slugify string
 * @param {string} str - String to slugify
 * @returns {string}
 */
function slugify(str) {
    return str
        .toLowerCase()
        .trim()
        .replace(/[^\w\s-]/g, '')
        .replace(/[\s_]+/g, '-')
        .replace(/^-+|-+$/g, '');
}

/**
 * Check if element is in viewport
 * @param {HTMLElement} element - Element to check
 * @returns {boolean}
 */
function isInViewport(element) {
    const rect = element.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
}

/**
 * Scroll to element
 * @param {HTMLElement} element - Element to scroll to
 * @param {object} options - Scroll options
 */
function scrollToElement(element, options = {}) {
    element.scrollIntoView({
        behavior: options.smooth ? 'smooth' : 'auto',
        block: options.block || 'start',
        inline: options.inline || 'nearest',
    });
}

/**
 * Get element by ID
 * @param {string} id - Element ID
 * @returns {HTMLElement|null}
 */
function getElement(id) {
    return document.getElementById(id);
}

/**
 * Get elements by class
 * @param {string} className - Class name
 * @returns {NodeList}
 */
function getElements(className) {
    return document.querySelectorAll(`.${className}`);
}

/**
 * Add event listener
 * @param {HTMLElement} element - Element
 * @param {string} event - Event name
 * @param {function} handler - Event handler
 */
function addListener(element, event, handler) {
    if (element) {
        element.addEventListener(event, handler);
    }
}

/**
 * Remove event listener
 * @param {HTMLElement} element - Element
 * @param {string} event - Event name
 * @param {function} handler - Event handler
 */
function removeListener(element, event, handler) {
    if (element) {
        element.removeEventListener(event, handler);
    }
}

/**
 * Add class to element
 * @param {HTMLElement} element - Element
 * @param {string} className - Class name
 */
function addClass(element, className) {
    if (element) {
        element.classList.add(className);
    }
}

/**
 * Remove class from element
 * @param {HTMLElement} element - Element
 * @param {string} className - Class name
 */
function removeClass(element, className) {
    if (element) {
        element.classList.remove(className);
    }
}

/**
 * Toggle class on element
 * @param {HTMLElement} element - Element
 * @param {string} className - Class name
 */
function toggleClass(element, className) {
    if (element) {
        element.classList.toggle(className);
    }
}

/**
 * Check if element has class
 * @param {HTMLElement} element - Element
 * @param {string} className - Class name
 * @returns {boolean}
 */
function hasClass(element, className) {
    return element ? element.classList.contains(className) : false;
}

