// ============= API Configuration =============

const API_BASE_URL = 'http://localhost:8000/api';
const API_TIMEOUT_MS = 8000;

async function fetchWithTimeout(url, options = {}) {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), API_TIMEOUT_MS);

    try {
        const response = await fetch(url, { ...options, signal: controller.signal });
        return response;
    } finally {
        clearTimeout(timeoutId);
    }
}

function wrapApiError(error) {
    if (error && error.name === 'AbortError') {
        return new Error('Timeout khi gọi API, vui lòng thử lại.');
    }
    return error;
}

// Helper function to make API calls
async function apiCall(endpoint, method = 'GET', data = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetchWithTimeout(`${API_BASE_URL}${endpoint}`, options);

        if (!response.ok) {
            throw new Error(`API Error: ${response.statusText}`);
        }

        const result = await response.json();
        return result;
    } catch (error) {
        const wrapped = wrapApiError(error);
        console.error('API Call Error:', wrapped);
        throw wrapped;
    }
}

async function apiCallWithParams(endpoint, method = 'GET', params = {}) {
    const query = new URLSearchParams(params).toString();
    const url = query ? `${API_BASE_URL}${endpoint}?${query}` : `${API_BASE_URL}${endpoint}`;
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    try {
        const response = await fetchWithTimeout(url, options);

        if (!response.ok) {
            throw new Error(`API Error: ${response.statusText}`);
        }

        const result = await response.json();
        return result;
    } catch (error) {
        const wrapped = wrapApiError(error);
        console.error('API Call Error:', wrapped);
        throw wrapped;
    }
}

// ============= Events API =============

async function fetchEvents() {
    try {
        const response = await apiCall('/events');
        return response.data || [];
    } catch (error) {
        console.error('Error fetching events:', error);
        return [];
    }
}

async function createEvent(eventData) {
    return apiCall('/events', 'POST', eventData);
}

// ============= Seats API =============

async function reserveSeats(eventId, userId, quantity) {
    try {
        const response = await apiCall('/seats/reserve', 'POST', {
            eventId,
            userId,
            quantity
        });
        return response.data || [];
    } catch (error) {
        console.error('Error reserving seats:', error);
        throw error;
    }
}

async function confirmSeat(eventId, seatId, userId) {
    try {
        const response = await apiCallWithParams('/seats/confirm', 'POST', {
            eventId,
            seatId,
            userId
        });
        return response.data;
    } catch (error) {
        console.error('Error confirming seat:', error);
        throw error;
    }
}

async function releaseSeat(eventId, seatId, userId) {
    try {
        const response = await apiCallWithParams('/seats/release', 'POST', {
            eventId,
            seatId,
            userId
        });
        return response.data;
    } catch (error) {
        console.error('Error releasing seat:', error);
        throw error;
    }
}

async function getAvailableSeats(eventId) {
    try {
        const response = await apiCall(`/seats/available/${eventId}`);
        return response.data || 0;
    } catch (error) {
        console.error('Error fetching available seats:', error);
        return 0;
    }
}

// ============= Payments API =============

async function processPayment(paymentData) {
    try {
        const response = await apiCallWithParams('/payments', 'POST', paymentData);
        return response.data;
    } catch (error) {
        console.error('Error processing payment:', error);
        throw error;
    }
}

// ============= Bookings API =============

async function createTicket(ticketData) {
    try {
        const response = await apiCallWithParams('/tickets', 'POST', ticketData);
        return response.data;
    } catch (error) {
        console.error('Error creating ticket:', error);
        throw error;
    }
}

async function getUserBookings(userId) {
    try {
        const response = await apiCall(`/tickets/user/${userId}`);
        return response.data || [];
    } catch (error) {
        console.error('Error fetching bookings:', error);
        return [];
    }
}

// ============= Reports API =============

async function fetchReports() {
    try {
        const response = await apiCall('/reports/summary');
        return response.data || {};
    } catch (error) {
        console.error('Error fetching reports:', error);
        return {};
    }
}

async function fetchEventReports(eventId) {
    try {
        const response = await apiCall(`/reports/event/${eventId}`);
        return response.data || {};
    } catch (error) {
        console.error('Error fetching event reports:', error);
        return {};
    }
}

// ============= Notifications API =============

async function fetchNotifications(userId) {
    try {
        const response = await apiCall(`/notifications/user/${userId}`);
        return response.data || [];
    } catch (error) {
        console.error('Error fetching notifications:', error);
        return [];
    }
}
