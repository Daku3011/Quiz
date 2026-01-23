/**
 * Reusable Components
 * Common UI components and component utilities
 */

/**
 * Modal Component
 */
class Modal {
    constructor(id, options = {}) {
        this.id = id;
        this.element = document.getElementById(id);
        this.options = {
            backdrop: true,
            keyboard: true,
            ...options,
        };
        this.isOpen = false;
        this.init();
    }

    init() {
        if (!this.element) {
            console.error(`Modal with id "${this.id}" not found`);
            return;
        }

        // Close on backdrop click
        if (this.options.backdrop) {
            this.element.addEventListener('click', (e) => {
                if (e.target === this.element) {
                    this.close();
                }
            });
        }

        // Close on escape key
        if (this.options.keyboard) {
            document.addEventListener('keydown', (e) => {
                if (e.key === 'Escape' && this.isOpen) {
                    this.close();
                }
            });
        }

        // Close button
        const closeBtn = this.element.querySelector('[data-dismiss="modal"]');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.close());
        }
    }

    open() {
        this.element.classList.add('show');
        this.isOpen = true;
        document.body.style.overflow = 'hidden';
    }

    close() {
        this.element.classList.remove('show');
        this.isOpen = false;
        document.body.style.overflow = '';
    }

    toggle() {
        this.isOpen ? this.close() : this.open();
    }
}

/**
 * Dropdown Component
 */
class Dropdown {
    constructor(triggerId, menuId) {
        this.trigger = document.getElementById(triggerId);
        this.menu = document.getElementById(menuId);
        this.isOpen = false;
        this.init();
    }

    init() {
        if (!this.trigger || !this.menu) {
            console.error('Dropdown trigger or menu not found');
            return;
        }

        this.trigger.addEventListener('click', () => this.toggle());

        // Close on item click
        this.menu.querySelectorAll('.dropdown-item').forEach((item) => {
            item.addEventListener('click', () => this.close());
        });

        // Close on outside click
        document.addEventListener('click', (e) => {
            if (!this.trigger.contains(e.target) && !this.menu.contains(e.target)) {
                this.close();
            }
        });
    }

    open() {
        this.menu.classList.add('show');
        this.isOpen = true;
    }

    close() {
        this.menu.classList.remove('show');
        this.isOpen = false;
    }

    toggle() {
        this.isOpen ? this.close() : this.open();
    }
}

/**
 * Tabs Component
 */
class Tabs {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.tabs = [];
        this.activeTab = null;
        this.init();
    }

    init() {
        if (!this.container) {
            console.error(`Tabs container with id "${this.container}" not found`);
            return;
        }

        const tabButtons = this.container.querySelectorAll('.tab');
        const tabContents = this.container.querySelectorAll('.tab-content');

        tabButtons.forEach((button, index) => {
            this.tabs.push({
                button,
                content: tabContents[index],
            });

            button.addEventListener('click', () => this.activate(index));
        });

        // Activate first tab
        if (this.tabs.length > 0) {
            this.activate(0);
        }
    }

    activate(index) {
        // Deactivate all tabs
        this.tabs.forEach((tab) => {
            tab.button.classList.remove('active');
            tab.content.classList.remove('active');
        });

        // Activate selected tab
        if (this.tabs[index]) {
            this.tabs[index].button.classList.add('active');
            this.tabs[index].content.classList.add('active');
            this.activeTab = index;
        }
    }

    next() {
        if (this.activeTab < this.tabs.length - 1) {
            this.activate(this.activeTab + 1);
        }
    }

    prev() {
        if (this.activeTab > 0) {
            this.activate(this.activeTab - 1);
        }
    }
}

/**
 * Accordion Component
 */
class Accordion {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.items = [];
        this.init();
    }

    init() {
        if (!this.container) {
            console.error(`Accordion container with id "${this.container}" not found`);
            return;
        }

        const items = this.container.querySelectorAll('.accordion-item');

        items.forEach((item) => {
            const header = item.querySelector('.accordion-header');
            const body = item.querySelector('.accordion-body');

            this.items.push({ header, body, isOpen: false });

            header.addEventListener('click', () => this.toggle(this.items.indexOf({ header, body })));
        });
    }

    toggle(index) {
        const item = this.items[index];
        if (!item) return;

        if (item.isOpen) {
            this.close(index);
        } else {
            this.open(index);
        }
    }

    open(index) {
        const item = this.items[index];
        if (!item) return;

        item.header.classList.add('active');
        item.body.classList.add('active');
        item.isOpen = true;
    }

    close(index) {
        const item = this.items[index];
        if (!item) return;

        item.header.classList.remove('active');
        item.body.classList.remove('active');
        item.isOpen = false;
    }

    openAll() {
        this.items.forEach((_, index) => this.open(index));
    }

    closeAll() {
        this.items.forEach((_, index) => this.close(index));
    }
}

/**
 * Form Validator Component
 */
class FormValidator {
    constructor(formId) {
        this.form = document.getElementById(formId);
        this.fields = {};
        this.errors = {};
        this.init();
    }

    init() {
        if (!this.form) {
            console.error(`Form with id "${this.form}" not found`);
            return;
        }

        const inputs = this.form.querySelectorAll('[data-validate]');
        inputs.forEach((input) => {
            const rules = input.dataset.validate.split('|');
            this.fields[input.name] = { input, rules };

            input.addEventListener('blur', () => this.validateField(input.name));
            input.addEventListener('change', () => this.validateField(input.name));
        });

        this.form.addEventListener('submit', (e) => {
            e.preventDefault();
            if (this.validate()) {
                this.form.submit();
            }
        });
    }

    validateField(fieldName) {
        const field = this.fields[fieldName];
        if (!field) return true;

        const { input, rules } = field;
        const value = input.value.trim();
        const errors = [];

        rules.forEach((rule) => {
            const [ruleName, ...params] = rule.split(':');

            switch (ruleName) {
                case 'required':
                    if (!value) {
                        errors.push(`${input.name} is required`);
                    }
                    break;

                case 'email':
                    if (value && !isValidEmail(value)) {
                        errors.push(`${input.name} must be a valid email`);
                    }
                    break;

                case 'min':
                    if (value && value.length < parseInt(params[0])) {
                        errors.push(`${input.name} must be at least ${params[0]} characters`);
                    }
                    break;

                case 'max':
                    if (value && value.length > parseInt(params[0])) {
                        errors.push(`${input.name} must not exceed ${params[0]} characters`);
                    }
                    break;

                case 'pattern':
                    if (value && !new RegExp(params[0]).test(value)) {
                        errors.push(`${input.name} format is invalid`);
                    }
                    break;

                case 'match':
                    const matchField = this.form.querySelector(`[name="${params[0]}"]`);
                    if (value && matchField && value !== matchField.value) {
                        errors.push(`${input.name} does not match`);
                    }
                    break;
            }
        });

        this.errors[fieldName] = errors;
        this.displayFieldError(input, errors);

        return errors.length === 0;
    }

    validate() {
        let isValid = true;

        Object.keys(this.fields).forEach((fieldName) => {
            if (!this.validateField(fieldName)) {
                isValid = false;
            }
        });

        return isValid;
    }

    displayFieldError(input, errors) {
        const feedback = input.nextElementSibling;

        if (errors.length > 0) {
            input.classList.add('is-invalid');
            input.classList.remove('is-valid');

            if (feedback && feedback.classList.contains('form-feedback')) {
                feedback.textContent = errors[0];
                feedback.classList.add('invalid');
                feedback.classList.remove('valid');
            }
        } else {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');

            if (feedback && feedback.classList.contains('form-feedback')) {
                feedback.textContent = '';
                feedback.classList.remove('invalid');
                feedback.classList.add('valid');
            }
        }
    }

    getErrors() {
        return this.errors;
    }

    clearErrors() {
        this.errors = {};
        Object.values(this.fields).forEach(({ input }) => {
            input.classList.remove('is-invalid', 'is-valid');
            const feedback = input.nextElementSibling;
            if (feedback && feedback.classList.contains('form-feedback')) {
                feedback.textContent = '';
            }
        });
    }
}

/**
 * Pagination Component
 */
class Pagination {
    constructor(containerId, options = {}) {
        this.container = document.getElementById(containerId);
        this.currentPage = options.currentPage || 1;
        this.totalPages = options.totalPages || 1;
        this.onPageChange = options.onPageChange || (() => {});
        this.render();
    }

    render() {
        if (!this.container) return;

        this.container.innerHTML = '';

        // Previous button
        const prevBtn = document.createElement('a');
        prevBtn.className = `pagination-item ${this.currentPage === 1 ? 'disabled' : ''}`;
        prevBtn.textContent = 'Previous';
        prevBtn.href = '#';
        prevBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if (this.currentPage > 1) {
                this.goToPage(this.currentPage - 1);
            }
        });
        this.container.appendChild(prevBtn);

        // Page numbers
        const startPage = Math.max(1, this.currentPage - 2);
        const endPage = Math.min(this.totalPages, this.currentPage + 2);

        if (startPage > 1) {
            const firstBtn = document.createElement('a');
            firstBtn.className = 'pagination-item';
            firstBtn.textContent = '1';
            firstBtn.href = '#';
            firstBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.goToPage(1);
            });
            this.container.appendChild(firstBtn);

            if (startPage > 2) {
                const dots = document.createElement('span');
                dots.className = 'pagination-item';
                dots.textContent = '...';
                this.container.appendChild(dots);
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            const btn = document.createElement('a');
            btn.className = `pagination-item ${i === this.currentPage ? 'active' : ''}`;
            btn.textContent = i;
            btn.href = '#';
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                this.goToPage(i);
            });
            this.container.appendChild(btn);
        }

        if (endPage < this.totalPages) {
            if (endPage < this.totalPages - 1) {
                const dots = document.createElement('span');
                dots.className = 'pagination-item';
                dots.textContent = '...';
                this.container.appendChild(dots);
            }

            const lastBtn = document.createElement('a');
            lastBtn.className = 'pagination-item';
            lastBtn.textContent = this.totalPages;
            lastBtn.href = '#';
            lastBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.goToPage(this.totalPages);
            });
            this.container.appendChild(lastBtn);
        }

        // Next button
        const nextBtn = document.createElement('a');
        nextBtn.className = `pagination-item ${this.currentPage === this.totalPages ? 'disabled' : ''}`;
        nextBtn.textContent = 'Next';
        nextBtn.href = '#';
        nextBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if (this.currentPage < this.totalPages) {
                this.goToPage(this.currentPage + 1);
            }
        });
        this.container.appendChild(nextBtn);
    }

    goToPage(page) {
        if (page >= 1 && page <= this.totalPages) {
            this.currentPage = page;
            this.render();
            this.onPageChange(page);
        }
    }

    setTotalPages(totalPages) {
        this.totalPages = totalPages;
        this.render();
    }
}

/**
 * Toast Notification Component
 */
class Toast {
    constructor(message, options = {}) {
        this.message = message;
        this.type = options.type || 'info';
        this.duration = options.duration || 3000;
        this.position = options.position || 'top-right';
        this.show();
    }

    show() {
        const toast = document.createElement('div');
        toast.className = `alert alert-${this.type}`;
        toast.textContent = this.message;
        toast.style.position = 'fixed';
        toast.style.zIndex = '9999';
        toast.style.minWidth = '300px';

        // Position
        const [vertical, horizontal] = this.position.split('-');
        toast.style[vertical] = '20px';
        toast.style[horizontal] = '20px';

        document.body.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, this.duration);
    }
}

/**
 * Progress Bar Component
 */
class ProgressBar {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.progress = 0;
        this.init();
    }

    init() {
        if (!this.container) {
            console.error(`Progress bar container with id "${this.container}" not found`);
            return;
        }

        this.container.innerHTML = `
            <div class="progress">
                <div class="progress-bar" style="width: 0%"></div>
            </div>
        `;
    }

    setProgress(value) {
        this.progress = Math.min(100, Math.max(0, value));
        const bar = this.container.querySelector('.progress-bar');
        if (bar) {
            bar.style.width = `${this.progress}%`;
        }
    }

    increment(value = 10) {
        this.setProgress(this.progress + value);
    }

    reset() {
        this.setProgress(0);
    }
}

