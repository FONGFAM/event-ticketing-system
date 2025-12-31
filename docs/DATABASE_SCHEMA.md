# 🗄️ Database Schema Documentation

## 📊 Tổng Quan Database

Hệ thống Event Ticketing System sử dụng **PostgreSQL** với kiến trúc **Database-per-Service**. Mỗi microservice có database riêng để đảm bảo tính độc lập và khả năng scale.

### 🏛️ Database Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    MICROSERVICES                             │
├──────────────┬──────────────┬──────────────┬────────────────┤
│Event Booking │   Payment    │  Ticketing   │ Notification   │
│  Service     │   Service    │   Service    │  & Analytics   │
└──────┬───────┴──────┬───────┴──────┬───────┴────────┬───────┘
       │              │              │                │
┌──────▼──────┐ ┌─────▼─────┐ ┌──────▼──────┐ ┌──────▼──────┐
│  event_db   │ │payment_db │ │ticketing_db │ │notification │
│ (Port 5432) │ │(Port 5433)│ │(Port 5434)  │ │_db (5435)   │
└─────────────┘ └───────────┘ └─────────────┘ └─────────────┘
```

### 📋 Danh Sách Databases

| Database | Service | Port | Tables | Mô tả |
|----------|---------|------|--------|-------|
| `event_db` | Event Booking Service | 5432 | 3 | Quản lý sự kiện, ghế ngồi, đặt chỗ |
| `payment_db` | Payment Service | 5433 | 1 | Quản lý giao dịch thanh toán |
| `ticketing_db` | Ticketing Service | 5434 | 1 | Quản lý vé điện tử |
| `notification_db` | Notification & Analytics | 5435 | 1 | Log thông báo và analytics |

---

## 📚 Database 1: event_db (Event Booking Service)

**Connection**: `localhost:5432`  
**Username**: `eventuser`  
**Password**: `eventpass`

### Bảng 1.1: `events`

**Mô tả**: Lưu trữ thông tin về các sự kiện

| Column | Type | Constraint | Mô tả |
|--------|------|------------|-------|
| `id` | VARCHAR(36) | PRIMARY KEY | UUID của sự kiện |
| `name` | VARCHAR(255) | NOT NULL | Tên sự kiện |
| `venue_name` | VARCHAR(255) | NOT NULL | Tên địa điểm tổ chức |
| `start_time` | TIMESTAMP | NOT NULL | Thời gian bắt đầu |
| `end_time` | TIMESTAMP | NOT NULL | Thời gian kết thúc |
| `description` | TEXT | NULL | Mô tả chi tiết sự kiện |
| `price` | DECIMAL(10,2) | NULL | Giá vé |
| `total_seats` | INTEGER | NOT NULL | Tổng số ghế |
| `available_seats` | INTEGER | NOT NULL | Số ghế còn trống |
| `sold_seats` | INTEGER | NOT NULL | Số ghế đã bán |
| `created_at` | TIMESTAMP | NOT NULL | Thời gian tạo |
| `updated_at` | TIMESTAMP | NOT NULL | Thời gian cập nhật |

**Indexes**:
```sql
CREATE INDEX idx_events_start_time ON events(start_time);
CREATE INDEX idx_events_venue_name ON events(venue_name);
```

**SQL Schema**:
```sql
CREATE TABLE events (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    venue_name VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    total_seats INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    sold_seats INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_events_start_time ON events(start_time);
CREATE INDEX idx_events_venue_name ON events(venue_name);
```

---

### Bảng 1.2: `seats`

**Mô tả**: Lưu trữ thông tin về các ghế ngồi trong từng sự kiện

| Column | Type | Constraint | Mô tả |
|--------|------|------------|-------|
| `id` | VARCHAR(36) | PRIMARY KEY | UUID của ghế |
| `event_id` | VARCHAR(36) | NOT NULL, FK → events(id) | ID sự kiện |
| `row` | VARCHAR(10) | NOT NULL | Hàng ghế (A, B, C, ...) |
| `col` | INTEGER | NOT NULL | Số ghế trong hàng (1, 2, 3, ...) |
| `status` | VARCHAR(20) | NOT NULL | AVAILABLE, BLOCKED, SOLD |
| `held_by` | TEXT | NULL | User ID đang giữ chỗ |
| `held_until` | BIGINT | NULL | Timestamp hết hạn giữ chỗ |
| `created_at` | TIMESTAMP | NOT NULL | Thời gian tạo |
| `updated_at` | TIMESTAMP | NOT NULL | Thời gian cập nhật |

**Constraints**:
- UNIQUE constraint on (event_id, row, col)

**Indexes**:
```sql
CREATE INDEX idx_seats_event_id ON seats(event_id);
CREATE INDEX idx_seats_status ON seats(status);
CREATE UNIQUE INDEX idx_seats_event_row_col ON seats(event_id, row, col);
```

**SQL Schema**:
```sql
CREATE TABLE seats (
    id VARCHAR(36) PRIMARY KEY,
    event_id VARCHAR(36) NOT NULL,
    row VARCHAR(10) NOT NULL,
    col INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    held_by TEXT,
    held_until BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seats_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT unique_seat_position UNIQUE (event_id, row, col)
);

CREATE INDEX idx_seats_event_id ON seats(event_id);
CREATE INDEX idx_seats_status ON seats(status);
```

**Enum Values**:
- `status`: 
  - `AVAILABLE`: Ghế có sẵn để đặt
  - `BLOCKED`: Ghế đang được giữ tạm thời (Redis lock)
  - `SOLD`: Ghế đã được bán

---

### Bảng 1.3: `seat_reservations`

**Mô tả**: Lưu trữ thông tin đặt chỗ (reservation) của người dùng

| Column | Type | Constraint | Mô tả |
|--------|------|------------|-------|
| `id` | VARCHAR(36) | PRIMARY KEY | UUID của reservation |
| `event_id` | VARCHAR(36) | NOT NULL | ID sự kiện |
| `seat_id` | VARCHAR(36) | NOT NULL | ID ghế ngồi |
| `user_id` | VARCHAR(255) | NOT NULL | ID người dùng |
| `status` | VARCHAR(20) | NOT NULL | HELD, CONFIRMED, RELEASED |
| `held_at` | TIMESTAMP | NOT NULL | Thời gian giữ chỗ |
| `expires_at` | TIMESTAMP | NOT NULL | Thời gian hết hạn (held_at + 5 phút) |
| `confirmed_at` | TIMESTAMP | NULL | Thời gian xác nhận (sau thanh toán) |

**Indexes**:
```sql
CREATE INDEX idx_reservation_event_seat ON seat_reservations(event_id, seat_id);
CREATE INDEX idx_reservation_status ON seat_reservations(status);
CREATE INDEX idx_reservation_expires_at ON seat_reservations(expires_at);
CREATE INDEX idx_reservation_user_id ON seat_reservations(user_id);
```

**SQL Schema**:
```sql
CREATE TABLE seat_reservations (
    id VARCHAR(36) PRIMARY KEY,
    event_id VARCHAR(36) NOT NULL,
    seat_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'HELD',
    held_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    CONSTRAINT fk_reservation_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_seat FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE
);

CREATE INDEX idx_reservation_event_seat ON seat_reservations(event_id, seat_id);
CREATE INDEX idx_reservation_status ON seat_reservations(status);
CREATE INDEX idx_reservation_expires_at ON seat_reservations(expires_at);
CREATE INDEX idx_reservation_user_id ON seat_reservations(user_id);
```

**Enum Values**:
- `status`:
  - `HELD`: Đang giữ chỗ (chưa thanh toán)
  - `CONFIRMED`: Đã xác nhận (đã thanh toán thành công)
  - `RELEASED`: Đã hủy hoặc hết hạn

---

## 💳 Database 2: payment_db (Payment Service)

**Connection**: `localhost:5433`  
**Username**: `paymentuser`  
**Password**: `paymentpass`

### Bảng 2.1: `transactions`

**Mô tả**: Lưu trữ tất cả giao dịch thanh toán

| Column | Type | Constraint | Mô tả |
|--------|------|------------|-------|
| `id` | VARCHAR(36) | PRIMARY KEY | UUID của transaction |
| `user_id` | VARCHAR(255) | NOT NULL | ID người dùng |
| `event_id` | VARCHAR(36) | NOT NULL | ID sự kiện |
| `amount` | DECIMAL(10,2) | NOT NULL | Số tiền thanh toán |
| `status` | VARCHAR(20) | NOT NULL | PENDING, CONFIRMED, FAILED, CANCELLED |
| `payment_method` | VARCHAR(50) | NOT NULL | Phương thức thanh toán |
| `transaction_id` | VARCHAR(255) | NULL | ID từ payment gateway |
| `created_at` | TIMESTAMP | NOT NULL | Thời gian tạo giao dịch |
| `updated_at` | TIMESTAMP | NOT NULL | Thời gian cập nhật |

**Indexes**:
```sql
CREATE INDEX idx_transaction_user_id ON transactions(user_id);
CREATE INDEX idx_transaction_status ON transactions(status);
CREATE INDEX idx_transaction_event_id ON transactions(event_id);
CREATE INDEX idx_transaction_created_at ON transactions(created_at);
```

**SQL Schema**:
```sql
CREATE TABLE transactions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    event_id VARCHAR(36) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transaction_user_id ON transactions(user_id);
CREATE INDEX idx_transaction_status ON transactions(status);
CREATE INDEX idx_transaction_event_id ON transactions(event_id);
CREATE INDEX idx_transaction_created_at ON transactions(created_at);
```

**Enum Values**:
- `status`:
  - `PENDING`: Đang chờ xử lý
  - `CONFIRMED`: Thanh toán thành công
  - `FAILED`: Thanh toán thất bại
  - `CANCELLED`: Đã hủy
- `payment_method`:
  - `CREDIT_CARD`: Thẻ tín dụng
  - `DEBIT_CARD`: Thẻ ghi nợ
  - `VNPAY`: VNPay wallet
  - `MOMO`: MoMo wallet
  - `PAYPAL`: PayPal

---

## 🎟️ Database 3: ticketing_db (Ticketing Service)

**Connection**: `localhost:5434`  
**Username**: `ticketinguser`  
**Password**: `ticketingpass`

### Bảng 3.1: `tickets`

**Mô tả**: Lưu trữ thông tin vé điện tử

| Column | Type | Constraint | Mô tả |
|--------|------|------------|-------|
| `id` | VARCHAR(36) | PRIMARY KEY | UUID của vé |
| `event_id` | VARCHAR(36) | NOT NULL | ID sự kiện |
| `seat_id` | VARCHAR(36) | NOT NULL | ID ghế ngồi |
| `user_id` | VARCHAR(255) | NOT NULL | ID người dùng |
| `payment_id` | VARCHAR(36) | NOT NULL | ID giao dịch thanh toán |
| `qr_code` | VARCHAR(255) | NOT NULL, UNIQUE | Mã QR code unique |
| `qr_code_image` | BYTEA | NULL | Ảnh QR code (binary) |
| `status` | VARCHAR(20) | NOT NULL | ACTIVE, USED, CANCELLED |
| `created_at` | TIMESTAMP | NOT NULL | Thời gian tạo vé |
| `updated_at` | TIMESTAMP | NOT NULL | Thời gian cập nhật |
| `checked_in_at` | TIMESTAMP | NULL | Thời gian check-in |

**Indexes**:
```sql
CREATE INDEX idx_ticket_user_id ON tickets(user_id);
CREATE INDEX idx_ticket_event_id ON tickets(event_id);
CREATE INDEX idx_ticket_qr_code ON tickets(qr_code);
CREATE INDEX idx_ticket_status ON tickets(status);
CREATE UNIQUE INDEX idx_ticket_qr_code_unique ON tickets(qr_code);
```

**SQL Schema**:
```sql
CREATE TABLE tickets (
    id VARCHAR(36) PRIMARY KEY,
    event_id VARCHAR(36) NOT NULL,
    seat_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    payment_id VARCHAR(36) NOT NULL,
    qr_code VARCHAR(255) NOT NULL UNIQUE,
    qr_code_image BYTEA,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checked_in_at TIMESTAMP
);

CREATE INDEX idx_ticket_user_id ON tickets(user_id);
CREATE INDEX idx_ticket_event_id ON tickets(event_id);
CREATE INDEX idx_ticket_qr_code ON tickets(qr_code);
CREATE INDEX idx_ticket_status ON tickets(status);
```

**Enum Values**:
- `status`:
  - `ACTIVE`: Vé còn hiệu lực
  - `USED`: Đã sử dụng (đã check-in)
  - `CANCELLED`: Đã hủy

---

## 📧 Database 4: notification_db (Notification & Analytics Service)

**Connection**: `localhost:5435`  
**Username**: `notificationuser`  
**Password**: `notificationpass`

### Bảng 4.1: `notification_logs`

**Mô tả**: Lưu log của tất cả thông báo và events

| Column | Type | Constraint | Mô tả |
|--------|------|------------|-------|
| `id` | VARCHAR(36) | PRIMARY KEY | UUID của log |
| `event_type` | VARCHAR(100) | NOT NULL | Loại event (payment-confirmed, ticket-created, ...) |
| `payload` | TEXT | NOT NULL | Dữ liệu JSON của event |
| `status` | VARCHAR(20) | NOT NULL | SENT, FAILED, PENDING |
| `created_at` | TIMESTAMP | NOT NULL | Thời gian tạo log |

**Indexes**:
```sql
CREATE INDEX idx_notification_event_type ON notification_logs(event_type);
CREATE INDEX idx_notification_status ON notification_logs(status);
CREATE INDEX idx_notification_created_at ON notification_logs(created_at);
```

**SQL Schema**:
```sql
CREATE TABLE notification_logs (
    id VARCHAR(36) PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_event_type ON notification_logs(event_type);
CREATE INDEX idx_notification_status ON notification_logs(status);
CREATE INDEX idx_notification_created_at ON notification_logs(created_at);
```

**Enum Values**:
- `event_type`:
  - `payment-confirmed`: Thanh toán thành công
  - `payment-failed`: Thanh toán thất bại
  - `ticket-created`: Vé được tạo
  - `reservation-expired`: Hết hạn giữ chỗ
- `status`:
  - `PENDING`: Đang chờ gửi
  - `SENT`: Đã gửi thành công
  - `FAILED`: Gửi thất bại

---

## 🔗 Relationships & Foreign Keys

### Entity Relationship Diagram (ERD)

```
┌─────────────┐
│   events    │
└──────┬──────┘
       │
       │ 1:N
       │
┌──────▼──────┐         ┌─────────────────┐
│    seats    │────────▶│seat_reservations│
└──────┬──────┘   1:N   └─────────────────┘
       │
       │ 1:1
       │
┌──────▼──────┐         ┌──────────────┐
│   tickets   │◀────────│ transactions │
└─────────────┘         └──────────────┘
                              │
                              │ N:1
                              │
                        ┌─────▼──────────┐
                        │notification_logs│
                        └────────────────┘
```

### Cross-Database Relationships

⚠️ **Lưu ý**: Do mỗi service có database riêng, **không thể tạo Foreign Key constraints giữa các database khác nhau**. Thay vào đó, sử dụng:
- **Application-level validation**: Kiểm tra tính toàn vẹn trong code
- **Event-driven consistency**: Kafka events để đồng bộ dữ liệu
- **Saga pattern**: Đảm bảo distributed transactions

#### Logical Relationships:

```
events(id) ←──── seats(event_id)
seats(id) ←──── seat_reservations(seat_id)
events(id) ←──── seat_reservations(event_id)
events(id) ←──── transactions(event_id)
transactions(id) ←──── tickets(payment_id)
seats(id) ←──── tickets(seat_id)
events(id) ←──── tickets(event_id)
```

---

## 🔐 Redis Cache Schema

**Connection**: `localhost:6379`

Redis được sử dụng cho **distributed locking** trong quá trình giữ chỗ:

### Key Pattern: Seat Locks

```
Key Pattern: seat_lock:{event_id}:{seat_id}
Value: {user_id}
TTL: 300000 milliseconds (5 phút)
Type: String
```

**Ví dụ**:
```redis
SET seat_lock:event-123:seat-A1 "user-456" NX PX 300000
```

**Commands**:
```bash
# Set lock (chỉ set nếu key chưa tồn tại)
SET seat_lock:event-123:seat-A1 "user-456" NX PX 300000

# Get lock owner
GET seat_lock:event-123:seat-A1

# Delete lock (release seat)
DEL seat_lock:event-123:seat-A1

# Check TTL
TTL seat_lock:event-123:seat-A1
```

---

## 📊 Data Volume Estimates

### Expected Data Growth

| Table | Initial | 1 Year | Growth Rate |
|-------|---------|--------|-------------|
| `events` | 100 | 5,000 | ~400/month |
| `seats` | 10,000 | 500,000 | ~40,000/month |
| `seat_reservations` | 5,000 | 1,000,000 | High (many expired) |
| `transactions` | 3,000 | 600,000 | ~50,000/month |
| `tickets` | 3,000 | 600,000 | ~50,000/month |
| `notification_logs` | 10,000 | 5,000,000 | Very high |

### Storage Estimates

```
events: ~500 bytes/row × 5,000 = 2.5 MB
seats: ~200 bytes/row × 500,000 = 100 MB
seat_reservations: ~300 bytes/row × 1,000,000 = 300 MB
transactions: ~350 bytes/row × 600,000 = 210 MB
tickets: ~500 bytes/row × 600,000 = 300 MB (excluding QR images)
notification_logs: ~1 KB/row × 5,000,000 = 5 GB

Total (estimated): ~6 GB/year
```

---

## 🔧 Maintenance Tasks

### Daily Tasks

```sql
-- Cleanup expired reservations (Event DB)
DELETE FROM seat_reservations 
WHERE status = 'HELD' AND expires_at < NOW();

-- Update seat status for expired holds
UPDATE seats 
SET status = 'AVAILABLE', held_by = NULL, held_until = NULL
WHERE status = 'BLOCKED' AND held_until < EXTRACT(EPOCH FROM NOW()) * 1000;
```

### Weekly Tasks

```sql
-- Archive old notification logs (older than 30 days)
DELETE FROM notification_logs 
WHERE created_at < NOW() - INTERVAL '30 days' AND status = 'SENT';

-- Vacuum and analyze
VACUUM ANALYZE events;
VACUUM ANALYZE seats;
VACUUM ANALYZE seat_reservations;
VACUUM ANALYZE transactions;
VACUUM ANALYZE tickets;
VACUUM ANALYZE notification_logs;
```

### Monthly Tasks

```sql
-- Reindex for performance
REINDEX TABLE events;
REINDEX TABLE seats;
REINDEX TABLE seat_reservations;
REINDEX TABLE transactions;
REINDEX TABLE tickets;

-- Update statistics
ANALYZE events;
ANALYZE seats;
ANALYZE transactions;
```

---

## 📈 Performance Optimization

### Recommended Indexes

Tất cả indexes quan trọng đã được tạo trong schema. Kiểm tra query performance:

```sql
-- Check slow queries
SELECT query, calls, total_time, mean_time 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;

-- Check index usage
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes
WHERE idx_scan = 0;
```

### Connection Pooling

**Recommended Settings** (application.yml):
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

## 🔒 Security Considerations

### Database Users & Permissions

```sql
-- Create read-only user for analytics
CREATE USER analytics_reader WITH PASSWORD 'secure_password';
GRANT CONNECT ON DATABASE event_db TO analytics_reader;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO analytics_reader;

-- Revoke unnecessary permissions
REVOKE ALL ON DATABASE event_db FROM PUBLIC;
```

### Encryption

- ✅ Connections use SSL/TLS in production
- ✅ Passwords stored as hashed values
- ✅ QR code images encrypted at rest
- ✅ Sensitive data masked in logs

---

## 📝 Migration Scripts

### Initial Setup Script

```bash
# Run from project root
cd infra
docker-compose up -d

# Wait for databases to be ready
sleep 10

# Run migrations (if using Flyway/Liquibase)
mvn flyway:migrate -pl event-booking-service
mvn flyway:migrate -pl payment-service
mvn flyway:migrate -pl ticketing-service
mvn flyway:migrate -pl notification-analytics-service
```

---

## 🧪 Sample Data

### Insert Sample Event

```sql
-- event_db
INSERT INTO events (id, name, venue_name, start_time, end_time, description, price, total_seats, available_seats, sold_seats, created_at, updated_at)
VALUES (
    'event-001',
    'Hòa nhạc Mùa Xuân 2025',
    'Nhà hát Lớn Hà Nội',
    '2025-03-15 19:00:00',
    '2025-03-15 22:00:00',
    'Đêm nhạc giao hưởng chào xuân',
    500000,
    200,
    200,
    0,
    NOW(),
    NOW()
);

-- Create seats (A1-A10)
INSERT INTO seats (id, event_id, row, col, status, created_at, updated_at)
SELECT 
    'seat-' || generate_series || '-' || col,
    'event-001',
    chr(64 + generate_series),
    col,
    'AVAILABLE',
    NOW(),
    NOW()
FROM generate_series(1, 10) AS row_num,
     generate_series(1, 20) AS col;
```

---

**Last Updated**: December 29, 2025  
**Database Version**: PostgreSQL 15  
**Document Version**: 1.0.0
