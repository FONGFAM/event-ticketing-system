// ============= Main Application Logic =============

const USER_STORAGE_KEY = 'eventTicketingUser';

function createGuestUser() {
    return {
        id: 'user_' + Math.random().toString(36).slice(2, 11),
        name: '',
        email: '',
        phone: ''
    };
}

function loadCurrentUser() {
    try {
        const stored = localStorage.getItem(USER_STORAGE_KEY);
        if (stored) {
            const parsed = JSON.parse(stored);
            if (parsed && parsed.id) {
                return parsed;
            }
        }
    } catch (error) {
        console.log('Failed to load user from storage:', error);
    }
    const fresh = createGuestUser();
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(fresh));
    return fresh;
}

function saveCurrentUser() {
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(currentUser));
}

let currentUser = loadCurrentUser();

let events = [];
let bookings = [];

// Initialize app when page loads
document.addEventListener('DOMContentLoaded', () => {
    loadData();
    showPage('home');
    setInterval(() => {
        loadEvents();
    }, 5 * 60 * 1000);
});

// ============= Page Navigation =============

function showPage(pageName) {
    // Hide all pages
    const pages = document.querySelectorAll('.page');
    pages.forEach(page => page.style.display = 'none');

    // Show selected page
    if (pageName === 'bookings') {
        // Show both booking form and history
        document.getElementById('bookings').style.display = 'block';
        document.getElementById('bookings-list').style.display = 'block';
    } else {
        const page = document.getElementById(pageName);
        if (page) page.style.display = 'block';
    }

    // Load page-specific data
    if (pageName === 'events') {
        loadEvents();
    } else if (pageName === 'bookings') {
        loadEventOptions();
        loadBookings();
    } else if (pageName === 'reports') {
        loadReports();
    }
}

// ============= Data Loading =============

function loadData() {
    loadEvents().then(() => {
        loadBookings();
    });
}

async function loadEvents() {
    try {
        const apiEvents = await fetchEvents();
        events = apiEvents;
    } catch (error) {
        console.log('Failed to load events:', error);
        events = [];
    }

    const eventsList = document.getElementById('eventsList');
    eventsList.innerHTML = '';

    if (events.length === 0) {
        eventsList.innerHTML = '<p>Không tải được dữ liệu sự kiện từ hệ thống.</p>';
        updateStats();
        loadEventOptions();
        return;
    }

    events.forEach(event => {
        const venueName = event.venueName || event.venue || 'N/A';
        const price = typeof event.price === 'number' ? event.price : 0;
        const availableSeats = typeof event.availableSeats === 'number' ? event.availableSeats : 0;
        const card = document.createElement('div');
        card.className = 'event-card';
        card.innerHTML = `
            <h3>${event.name}</h3>
            <p><strong>📍 Địa điểm:</strong> ${venueName}</p>
            <p><strong>📅 Ngày:</strong> ${new Date(event.startTime).toLocaleDateString('vi-VN')}</p>
            <p><strong>⏰ Giờ:</strong> ${new Date(event.startTime).toLocaleTimeString('vi-VN', {hour: '2-digit', minute:'2-digit'})}</p>
            <div class="event-info">
                <span>💰 ${price.toLocaleString('vi-VN')} VNĐ</span>
                <span>🎫 ${availableSeats} vé trống</span>
            </div>
        `;
        eventsList.appendChild(card);
    });
    updateStats();
    loadEventOptions();
    renderBookings();
}

function loadEventOptions() {
    const select = document.getElementById('eventSelect');
    select.innerHTML = '<option value="">-- Chọn sự kiện --</option>';

    if (events.length === 0) {
        const option = document.createElement('option');
        option.value = '';
        option.text = 'Không có dữ liệu sự kiện';
        select.appendChild(option);
        return;
    }

    events.forEach(event => {
        const price = typeof event.price === 'number' ? event.price : 0;
        const option = document.createElement('option');
        option.value = event.id;
        option.text = `${event.name} - ${price.toLocaleString('vi-VN')} VNĐ`;
        select.appendChild(option);
    });
}

async function loadReports() {
    const reportsList = document.getElementById('reportsList');
    
    try {
        const reports = await fetchReports();
        const data = reports && typeof reports === 'object' ? reports : null;

        if (!data) {
            reportsList.innerHTML = '<p>Không tải được dữ liệu báo cáo.</p>';
            return;
        }
        
        reportsList.innerHTML = `
            <div class="report-item">
                <strong>📊 Tổng sự kiện:</strong>
                <div class="value">${data.totalEvents || 0}</div>
            </div>
            <div class="report-item">
                <strong>🎫 Vé bán được:</strong>
                <div class="value">${(data.totalTicketsSold || 0).toLocaleString('vi-VN')}</div>
            </div>
            <div class="report-item">
                <strong>💰 Tổng doanh thu:</strong>
                <div class="value">${(data.totalRevenue || 0).toLocaleString('vi-VN')} VNĐ</div>
            </div>
            <div class="report-item">
                <strong>💵 Giá vé trung bình:</strong>
                <div class="value">${(data.averageTicketPrice || 0).toLocaleString('vi-VN')} VNĐ</div>
            </div>
            <div class="report-item">
                <strong>⭐ Sự kiện hot:</strong>
                <div class="value">${data.topEvent || 'N/A'}</div>
            </div>
            <div class="report-item">
                <strong>📈 Doanh thu tuần:</strong>
                <div class="value">${(data.weeklyRevenue || 0).toLocaleString('vi-VN')} VNĐ</div>
            </div>
        `;
    } catch (error) {
        reportsList.innerHTML = '<p>Không tải được dữ liệu báo cáo.</p>';
    }
}

async function loadBookings() {
    const bookingsList = document.getElementById('bookingsList');
    if (bookingsList) {
        bookingsList.innerHTML = '<p>Đang tải dữ liệu đặt vé...</p>';
    }

    try {
        const userBookings = await getUserBookings(currentUser.id);
        bookings = Array.isArray(userBookings) ? userBookings : [];
    } catch (error) {
        bookings = [];
    }

    renderBookings();
    updateStats();
}

function renderBookings() {
    const bookingsList = document.getElementById('bookingsList');
    if (!bookingsList) {
        return;
    }

    if (!bookings.length) {
        bookingsList.innerHTML = '<p>Chưa có vé nào được lưu trong hệ thống.</p>';
        return;
    }

    const rows = bookings.map((ticket, index) => {
        const eventInfo = events.find(item => item.id === ticket.eventId);
        const eventName = eventInfo ? eventInfo.name : ticket.eventId;
        const price = eventInfo && typeof eventInfo.price === 'number' ? eventInfo.price : 0;
        const createdAt = ticket.createdAt ? formatTimestamp(ticket.createdAt) : 'N/A';
        const status = ticket.status || 'N/A';

        return `
            <tr>
                <td>${index + 1}</td>
                <td>${eventName}</td>
                <td>${ticket.seatId || 'N/A'}</td>
                <td>${price.toLocaleString('vi-VN')} VNĐ</td>
                <td>${status}</td>
                <td>${createdAt}</td>
            </tr>
        `;
    }).join('');

    bookingsList.innerHTML = `
        <table class="table">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Sự kiện</th>
                    <th>Ghế</th>
                    <th>Giá vé</th>
                    <th>Trạng thái</th>
                    <th>Thời gian</th>
                </tr>
            </thead>
            <tbody>
                ${rows}
            </tbody>
        </table>
    `;
}

// ============= Booking Function =============

async function bookTicket() {
    const eventId = document.getElementById('eventSelect').value;
    const seatCount = parseInt(document.getElementById('seatCount').value);
    const userName = document.getElementById('userName').value;
    const userEmail = document.getElementById('userEmail').value;
    const userPhone = document.getElementById('userPhone').value;

    const messageDiv = document.getElementById('bookingMessage');
    let reservedSeats = [];

    if (!eventId || !userName || !userEmail || !userPhone) {
        messageDiv.className = 'message error';
        messageDiv.textContent = '⚠️ Vui lòng điền đầy đủ thông tin';
        messageDiv.style.display = 'block';
        return;
    }

    if (seatCount < 1) {
        messageDiv.className = 'message error';
        messageDiv.textContent = '⚠️ Số lượng vé phải >= 1';
        messageDiv.style.display = 'block';
        return;
    }

    const selectedEvent = events.find(e => e.id === eventId);
    
    if (selectedEvent.availableSeats < seatCount) {
        messageDiv.className = 'message error';
        messageDiv.textContent = `⚠️ Chỉ còn ${selectedEvent.availableSeats} vé trống`;
        messageDiv.style.display = 'block';
        return;
    }

    try {
        messageDiv.className = 'message';
        messageDiv.textContent = '⏳ Đang giữ chỗ và xử lý thanh toán...';
        messageDiv.style.display = 'block';

        currentUser.name = userName;
        currentUser.email = userEmail;
        currentUser.phone = userPhone;
        saveCurrentUser();

        reservedSeats = await reserveSeats(eventId, currentUser.id, seatCount);

        if (reservedSeats.length !== seatCount) {
            throw new Error('Không đủ chỗ trống để giữ');
        }

        const ticketPrice = typeof selectedEvent.price === 'number' ? selectedEvent.price : 0;
        const totalPrice = ticketPrice * seatCount;
        const paymentId = await processPayment({
            userId: currentUser.id,
            eventId: eventId,
            amount: totalPrice,
            paymentMethod: 'CARD'
        });

        for (const seat of reservedSeats) {
            await createTicket({
                eventId: eventId,
                seatId: seat.id,
                userId: currentUser.id,
                paymentId: paymentId
            });
            await confirmSeat(eventId, seat.id, currentUser.id);
        }

        // Update available seats for UI
        if (typeof selectedEvent.availableSeats === 'number') {
            selectedEvent.availableSeats = Math.max(0, selectedEvent.availableSeats - seatCount);
        }

        messageDiv.className = 'message success';
        messageDiv.innerHTML = `
            ✅ <strong>Đặt vé thành công!</strong><br>
            Sự kiện: ${selectedEvent.name}<br>
            Số vé: ${seatCount}<br>
            Tổng giá: ${totalPrice.toLocaleString('vi-VN')} VNĐ<br>
            <em>Hóa đơn đã gửi đến email: ${userEmail}</em>
        `;
        messageDiv.style.display = 'block';

        // Clear form
        document.getElementById('bookingForm').reset();

        await loadBookings();

    } catch (error) {
        for (const seat of reservedSeats) {
            try {
                await releaseSeat(eventId, seat.id, currentUser.id);
            } catch (releaseError) {
                console.log('Release failed:', releaseError);
            }
        }
        messageDiv.className = 'message error';
        messageDiv.textContent = '❌ Lỗi: ' + (error.message || 'Không thể đặt vé');
        messageDiv.style.display = 'block';
    }
}

// ============= Statistics =============

async function updateStats() {
    let totalEvents = events.length;
    let totalTicketsSold = bookings.length;
    let totalRevenue = bookings.reduce((sum, ticket) => {
        const eventInfo = events.find(item => item.id === ticket.eventId);
        const price = eventInfo && typeof eventInfo.price === 'number' ? eventInfo.price : 0;
        return sum + price;
    }, 0);

    try {
        const reports = await fetchReports();
        if (reports && typeof reports === 'object') {
            if (typeof reports.totalEvents === 'number') {
                totalEvents = reports.totalEvents;
            }
            if (typeof reports.totalTicketsSold === 'number') {
                totalTicketsSold = reports.totalTicketsSold;
            }
            if (typeof reports.totalRevenue === 'number') {
                totalRevenue = reports.totalRevenue;
            }
        }
    } catch (error) {
        console.log('Failed to refresh live dashboard:', error);
    }

    document.getElementById('eventCount').textContent = totalEvents;
    document.getElementById('ticketCount').textContent = totalTicketsSold.toLocaleString('vi-VN');
    document.getElementById('revenueCount').textContent = totalRevenue.toLocaleString('vi-VN') + ' VNĐ';
}

// ============= Utilities =============

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('vi-VN');
}

function formatTime(dateString) {
    return new Date(dateString).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}

function formatCurrency(amount) {
    return amount.toLocaleString('vi-VN') + ' VNĐ';
}

function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    if (Number.isNaN(date.getTime())) {
        return 'N/A';
    }
    return date.toLocaleString('vi-VN');
}
