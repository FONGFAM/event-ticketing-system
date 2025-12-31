# 🎟️ Event Ticketing System

## 📖 Tổng Quan Dự Án

Hệ thống đặt vé sự kiện (Event Ticketing System) là một ứng dụng phân tán được xây dựng theo kiến trúc Microservices, cho phép người dùng đặt vé cho các sự kiện, xử lý thanh toán, tạo vé điện tử và nhận thông báo. Hệ thống được thiết kế để xử lý đồng thời, đảm bảo tính nhất quán dữ liệu và khả năng mở rộng cao.

### 🎯 Mục Tiêu Chính

- **Đặt vé trực tuyến**: Cho phép người dùng tìm kiếm sự kiện và đặt chỗ ngồi
- **Xử lý thanh toán**: Tích hợp thanh toán an toàn với nhiều phương thức
- **Quản lý ghế ngồi**: Giữ chỗ tạm thời (5 phút) với Redis distributed lock
- **Tạo vé điện tử**: Sinh mã QR code cho từng vé
- **Thông báo & Báo cáo**: Email xác nhận và báo cáo thống kê

---

## 🏗️ Kiến Trúc Hệ Thống

### 1️⃣ Kiến Trúc Tổng Quan

Hệ thống được xây dựng theo mô hình **Microservices** với các thành phần chính:

```
┌──────────────────────────────────────────────────────────────────┐
│                         FRONTEND (HTML/JS/CSS)                    │
└────────────────┬─────────────────────────────────────────────────┘
                 │ HTTP/REST
                 ▼
┌──────────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Port 8000)                        │
│  - Spring Cloud Gateway                                           │
│  - JWT Authentication                                             │
│  - Rate Limiting                                                  │
│  - Request Routing                                                │
└──────┬──────────┬──────────┬──────────┬─────────────────────────┘
       │          │          │          │
   ┌───▼──┐   ┌───▼──┐   ┌───▼──┐   ┌───▼────────┐
   │Event │   │Payment│   │Ticket│   │Notification│
   │Booking│  │Service│   │Service│  │& Analytics │
   │ 8001 │   │ 8003 │   │ 8004 │   │   8005     │
   └───┬──┘   └───┬──┘   └───┬──┘   └───┬────────┘
       │          │          │          │
       ├──────────┴──────────┴──────────┤
       │      Kafka Message Queue        │
       │  - payment-confirmed            │
       │  - ticket-created               │
       │  - payment-failed               │
       └─────────────────────────────────┘
                     │
       ┌─────────────┴──────────────┐
       │  Data Layer                │
       ├────────────────────────────┤
       │ PostgreSQL x 4 (5432-5435) │
       │ Redis Cache (6379)         │
       │ Kafka + Zookeeper          │
       └────────────────────────────┘
```

### 2️⃣ Các Microservices

#### 🔷 API Gateway (Port 8000)
- **Công nghệ**: Spring Cloud Gateway
- **Chức năng**:
  - Định tuyến request đến các microservices
  - Xác thực JWT token
  - Rate limiting và load balancing
  - Centralized logging

#### 🔷 Event Booking Service (Port 8001)
- **Database**: PostgreSQL (Port 5432)
- **Cache**: Redis (Port 6379)
- **Chức năng**:
  - Quản lý sự kiện (CRUD operations)
  - Quản lý ghế ngồi và sơ đồ chỗ ngồi
  - Giữ chỗ tạm thời với Redis lock (TTL: 5 phút)
  - Xác nhận đặt chỗ sau khi thanh toán thành công
  - gRPC endpoints cho inter-service communication

#### 🔷 Payment Service (Port 8003)
- **Database**: PostgreSQL (Port 5433)
- **Chức năng**:
  - Xử lý thanh toán (VNPay, MoMo, Credit Card)
  - Quản lý transaction history
  - Publish payment events qua Kafka
  - Xử lý refund và rollback

#### 🔷 Ticketing Service (Port 8004)
- **Database**: PostgreSQL (Port 5434)
- **Chức năng**:
  - Tạo vé điện tử sau khi thanh toán
  - Sinh mã QR code unique cho mỗi vé
  - Quản lý trạng thái vé (ACTIVE, USED, CANCELLED)
  - Check-in tại cổng vào sự kiện
  - gRPC service cho việc validate tickets

#### 🔷 Notification & Analytics Service (Port 8005)
- **Database**: PostgreSQL (Port 5435)
- **Chức năng**:
  - Gửi email xác nhận đặt vé
  - Thông báo trạng thái thanh toán
  - Báo cáo thống kê doanh thu
  - Phân tích hành vi người dùng
  - Kafka consumer để xử lý events

### 3️⃣ Công Nghệ Sử Dụng

| Thành phần | Công nghệ | Version |
|------------|-----------|---------|
| **Backend Framework** | Spring Boot | 3.4.0 |
| **Cloud Gateway** | Spring Cloud Gateway | 2024.0.0 |
| **Database** | PostgreSQL | 15 Alpine |
| **Cache** | Redis | 7 Alpine |
| **Message Queue** | Apache Kafka | 7.5.0 |
| **RPC** | gRPC | 1.59.0 |
| **Build Tool** | Maven | 3.8+ |
| **Java** | OpenJDK | 17+ |
| **Containerization** | Docker & Docker Compose | - |
| **Frontend** | HTML5, CSS3, Vanilla JS | - |

### 4️⃣ Communication Patterns

1. **REST API**: Client ↔ API Gateway ↔ Services
2. **gRPC**: Inter-service synchronous calls
3. **Kafka Events**: Asynchronous event streaming
4. **Redis**: Distributed locking & caching

---

## 📁 Cấu Trúc Thư Mục

```
event-ticketing-system/
│
├── 📂 api-gateway/                    # API Gateway service
│   ├── src/main/java/...
│   ├── src/main/resources/
│   │   └── application.yml            # Gateway configuration
│   └── pom.xml
│
├── 📂 common-library/                 # Shared DTOs, Utils, Exceptions
│   ├── src/main/java/com/eventticket/common/
│   │   ├── dto/                       # Data Transfer Objects
│   │   ├── exception/                 # Custom Exceptions
│   │   └── util/                      # Utility classes
│   └── pom.xml
│
├── 📂 event-booking-service/          # Event & Seat Management
│   ├── src/main/java/com/eventticket/booking/
│   │   ├── controller/                # REST Controllers
│   │   ├── service/                   # Business Logic
│   │   ├── repository/                # JPA Repositories
│   │   ├── entity/                    # Database Entities
│   │   └── grpc/                      # gRPC Server Implementation
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-dev.yml
│   └── pom.xml
│
├── 📂 payment-service/                # Payment Processing
│   ├── src/main/java/com/eventticket/payment/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── kafka/                     # Kafka Producers
│   └── pom.xml
│
├── 📂 ticketing-service/              # Ticket Generation
│   ├── src/main/java/com/eventticket/ticketing/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── grpc/                      # gRPC Client/Server
│   └── pom.xml
│
├── 📂 notification-analytics-service/ # Notification & Reports
│   ├── src/main/java/com/eventticket/notification/
│   │   ├── controller/
│   │   ├── service/
│   │   │   ├── EmailService.java
│   │   │   └── AnalyticsService.java
│   │   ├── repository/
│   │   └── kafka/                     # Kafka Consumers
│   └── pom.xml
│
├── 📂 grpc-proto/                     # Protocol Buffer Definitions
│   ├── src/main/proto/
│   │   ├── event.proto
│   │   ├── ticket.proto
│   │   └── seat.proto
│   └── pom.xml
│
├── 📂 infra/                          # Infrastructure as Code
│   ├── docker-compose.yml             # Full stack setup
│   └── postgres-init/                 # DB initialization scripts
│
├── 📂 frontend/                       # Web UI
│   ├── index.html
│   ├── app.js
│   ├── api.js                         # API client
│   └── style.css
│
├── 📂 docs/                           # Documentation
│   ├── ARCHITECTURE.md                # Architecture details
│   ├── API_EXAMPLES.md                # API usage examples
│   └── README.md
│
├── 📂 scripts/                        # Automation scripts
│   ├── setup.sh
│   ├── start_all_services.sh
│   └── cleanup.sh
│
├── pom.xml                            # Parent POM (Maven Multi-module)
├── SETUP_GUIDE.md                     # Setup instructions
└── README.md                          # This file
```

### 📦 Module Dependencies

```
┌─────────────────────────────────────┐
│       Parent POM (Root)             │
└──────────────┬──────────────────────┘
               │
       ┌───────┴───────┬───────────────┬──────────────┐
       │               │               │              │
┌──────▼──────┐  ┌─────▼─────┐  ┌──────▼─────┐  ┌────▼─────┐
│common-library│  │grpc-proto │  │api-gateway │  │ services │
└──────────────┘  └───────────┘  └────────────┘  └──────────┘
       ▲               ▲                              ▲
       │               │                              │
       └───────────────┴──────────────────────────────┘
              (All services depend on these)
```

---

## 🚀 Hướng Dẫn Cài Đặt và Triển Khai

### 📋 Yêu Cầu Hệ Thống

| Phần mềm | Phiên bản | Mô tả |
|----------|-----------|-------|
| **Java JDK** | 17 hoặc cao hơn | Oracle JDK hoặc OpenJDK |
| **Maven** | 3.8.0+ | Build tool |
| **Docker** | Latest | Containerization |
| **Docker Compose** | Latest | Multi-container orchestration |
| **Git** | Latest | Version control |

### 🔧 Bước 1: Clone Repository

```bash
git clone https://github.com/FONGFAM/event-ticketing-system.git
cd event-ticketing-system
```

### 🔨 Bước 2: Build Project

#### Option 1: Build toàn bộ project (Khuyến nghị)
```bash
mvn clean install -DskipTests
```

#### Option 2: Build từng module riêng lẻ
```bash
# Build common library trước
mvn clean install -pl common-library -DskipTests

# Build grpc-proto
mvn clean install -pl grpc-proto -DskipTests

# Build các services
mvn clean install -pl event-booking-service -DskipTests
mvn clean install -pl payment-service -DskipTests
mvn clean install -pl ticketing-service -DskipTests
mvn clean install -pl notification-analytics-service -DskipTests
mvn clean install -pl api-gateway -DskipTests
```

#### Option 3: Build và chạy tests
```bash
mvn clean install
```

### 🐳 Bước 3: Khởi động Infrastructure với Docker Compose

```bash
# Di chuyển vào thư mục infra
cd infra

# Khởi động tất cả services
docker-compose up -d

# Kiểm tra trạng thái
docker-compose ps
```

**Services được khởi động:**
- ✅ PostgreSQL Event DB (Port 5432)
- ✅ PostgreSQL Payment DB (Port 5433)
- ✅ PostgreSQL Ticketing DB (Port 5434)
- ✅ PostgreSQL Notification DB (Port 5435)
- ✅ Redis Cache (Port 6379)
- ✅ Kafka (Port 9092, 29092)
- ✅ Zookeeper (Port 2181)
- ✅ pgAdmin (Port 5050)

### 🏃 Bước 4: Chạy Microservices

#### Option 1: Chạy từng service trong terminal riêng

```bash
# Terminal 1 - API Gateway
mvn spring-boot:run -pl api-gateway

# Terminal 2 - Event Booking Service
mvn spring-boot:run -pl event-booking-service

# Terminal 3 - Payment Service
mvn spring-boot:run -pl payment-service

# Terminal 4 - Ticketing Service
mvn spring-boot:run -pl ticketing-service

# Terminal 5 - Notification & Analytics Service
mvn spring-boot:run -pl notification-analytics-service
```

#### Option 2: Sử dụng script tự động

```bash
# Chạy tất cả services
./scripts/start_all_services.sh
```

### 🌐 Bước 5: Mở Frontend

```bash
# Mở file frontend/index.html trong trình duyệt
# Hoặc sử dụng Live Server trong VS Code
```

### ✅ Bước 6: Kiểm tra Services

Kiểm tra các services đã khởi động thành công:

```bash
# Check Event Booking Service
curl http://localhost:8001/actuator/health

# Check Payment Service
curl http://localhost:8003/actuator/health

# Check Ticketing Service
curl http://localhost:8004/actuator/health

# Check Notification Service
curl http://localhost:8005/actuator/health

# Check API Gateway
curl http://localhost:8000/actuator/health
```

---

## 🔐 Flow Hoạt Động Chính

### 📝 Flow 1: Đặt vé hoàn chỉnh

```
┌─────────┐                                                    
│  User   │                                                    
└────┬────┘                                                    
     │                                                         
     │ 1. Browse Events                                       
     ├─────────────────────► API Gateway ──► Event Booking Service
     │                                              │          
     │                                              ▼          
     │                                        Get Events from DB
     │                                              │          
     │ ◄────────────────────────────────────────────┘          
     │                                                         
     │ 2. Hold Seat A1 (5 minutes)                           
     ├─────────────────────► API Gateway ──► Event Booking Service
     │                                              │          
     │                                              ├─► Redis: SET seat_lock NX PX 300000
     │                                              │          
     │                                              ├─► Save Reservation (status=HELD)
     │                                              │          
     │ ◄────────────────────────────────────────────┘          
     │ (reservation_id, expires_at)                           
     │                                                         
     │ 3. Process Payment                                     
     ├─────────────────────► API Gateway ──► Payment Service 
     │                                              │          
     │                                              ├─► Create Transaction
     │                                              │          
     │                                              ├─► Process Payment
     │                                              │          
     │                                              ├─► Kafka: Publish "payment-confirmed"
     │                                              │          
     │ ◄────────────────────────────────────────────┘          
     │                                                         
     │ 4. Event Listeners (Async)                            
     │                                                         
     │   ┌──────────────────────────────────────────┐         
     │   │ Event Booking Service (Kafka Consumer)   │         
     │   ├─► Update Reservation status = CONFIRMED  │         
     │   ├─► Redis: DEL seat_lock                   │         
     │   └──────────────────────────────────────────┘         
     │                                                         
     │   ┌──────────────────────────────────────────┐         
     │   │ Ticketing Service (Kafka Consumer)       │         
     │   ├─► Create Ticket with QR Code             │         
     │   ├─► Kafka: Publish "ticket-created"        │         
     │   └──────────────────────────────────────────┘         
     │                                                         
     │   ┌──────────────────────────────────────────┐         
     │   │ Notification Service (Kafka Consumer)    │         
     │   ├─► Send Email with Ticket PDF             │         
     │   ├─► Update Analytics                       │         
     │   └──────────────────────────────────────────┘         
     │                                                         
     │ 5. Get Ticket                                          
     ├─────────────────────► API Gateway ──► Ticketing Service
     │                                              │          
     │                                              ├─► Fetch Ticket with QR
     │                                              │          
     │ ◄────────────────────────────────────────────┘          
     │ (Ticket PDF + QR Code)                                 
     │                                                         
```

### ⏰ Flow 2: Hết hạn giữ chỗ (Timeout)

```
1. User giữ chỗ Seat A1
2. Redis lock set với TTL = 5 phút
3. Sau 5 phút:
   ├─► Redis TTL hết hạn → Key tự động xóa
   ├─► Background Job cleanup expired reservations
   │   └─► Update reservation status = RELEASED
   └─► Seat A1 available trở lại cho user khác
```

### 🔄 Flow 3: Rollback khi thanh toán thất bại

```
1. User thanh toán thất bại
2. Payment Service:
   ├─► Update transaction status = FAILED
   └─► Kafka: Publish "payment-failed" event
3. Event Booking Service (Consumer):
   ├─► Update reservation status = RELEASED
   └─► Redis: DEL seat_lock (release seat)
4. Notification Service (Consumer):
   └─► Send email: "Payment failed, please try again"
```

---

## 🔑 API Endpoints Chính

### 📍 API Gateway Base URL: `http://localhost:8000`

#### 1️⃣ Event Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/events` | Lấy danh sách sự kiện |
| GET | `/api/events/{id}` | Lấy chi tiết sự kiện |
| POST | `/api/events` | Tạo sự kiện mới (Admin) |
| PUT | `/api/events/{id}` | Cập nhật sự kiện |
| DELETE | `/api/events/{id}` | Xóa sự kiện |

#### 2️⃣ Seat Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/seats/{eventId}` | Lấy sơ đồ chỗ ngồi |
| POST | `/api/seats/hold` | Giữ chỗ tạm thời |
| POST | `/api/seats/release` | Hủy giữ chỗ |

#### 3️⃣ Payment

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/process` | Xử lý thanh toán |
| GET | `/api/payments/{id}` | Lấy thông tin transaction |
| GET | `/api/payments/user/{userId}` | Lịch sử thanh toán |

#### 4️⃣ Ticketing

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tickets/user/{userId}` | Lấy vé của user |
| GET | `/api/tickets/{id}` | Chi tiết vé |
| POST | `/api/tickets/validate` | Validate QR code |

#### 5️⃣ Analytics & Reports

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports/revenue` | Báo cáo doanh thu |
| GET | `/api/reports/events/{eventId}` | Thống kê sự kiện |

---

## 🧪 Testing

### Unit Tests

```bash
# Chạy tất cả unit tests
mvn test

# Chạy test cho một service cụ thể
mvn test -pl event-booking-service

# Chạy một test class cụ thể
mvn test -Dtest=EventServiceTest
```

### Integration Tests

```bash
# Chạy integration tests
mvn verify

# Chạy với coverage report
mvn clean test jacoco:report
```

### Manual API Testing

Sử dụng file `docs/API_EXAMPLES.md` để test các API endpoints với curl hoặc Postman.

---

## 🛠️ Troubleshooting

### ❌ Lỗi: "Cannot find symbol: log"

**Nguyên nhân**: Lombok chưa được cài đặt hoặc cấu hình

**Giải pháp**:
```bash
# Clean và rebuild
mvn clean install -DskipTests

# Kiểm tra Lombok version trong pom.xml (>= 1.18.30)
```

### ❌ Lỗi: "Port already in use"

**Giải pháp**:
```bash
# Stop các Docker containers
docker-compose down

# Hoặc kill process đang sử dụng port
# Windows
netstat -ano | findstr :8000
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8000 | xargs kill -9
```

### ❌ Lỗi: "Database connection refused"

**Giải pháp**:
```bash
# Kiểm tra Docker containers đang chạy
docker-compose ps

# Restart PostgreSQL containers
docker-compose restart postgres-event-db
```

### ❌ Lỗi: "Redis connection timeout"

**Giải pháp**:
```bash
# Kiểm tra Redis container
docker logs redis-cache

# Restart Redis
docker-compose restart redis-cache
```

---

## 📊 Monitoring & Logging

### Health Checks

Mỗi service đều có Spring Boot Actuator endpoints:

```bash
# Health check
curl http://localhost:8001/actuator/health

# Metrics
curl http://localhost:8001/actuator/metrics

# Info
curl http://localhost:8001/actuator/info
```

### Database Management

**pgAdmin**: http://localhost:5050
- Email: `admin@example.com`
- Password: `admin`

### Kafka Monitoring

```bash
# List topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Consume messages
docker exec -it kafka kafka-console-consumer \
  --topic payment-confirmed \
  --from-beginning \
  --bootstrap-server localhost:9092
```

---

## 🚦 Deployment

### Development Environment

```bash
# Sử dụng Docker Compose
cd infra
docker-compose up -d
```

### Production Deployment

#### Option 1: Docker Containers

```bash
# Build Docker images cho từng service
docker build -t event-booking-service:1.0 ./event-booking-service
docker build -t payment-service:1.0 ./payment-service
docker build -t ticketing-service:1.0 ./ticketing-service
docker build -t notification-service:1.0 ./notification-analytics-service
docker build -t api-gateway:1.0 ./api-gateway
```

#### Option 2: Kubernetes (K8s)

```bash
# Apply Kubernetes manifests (cần tạo trước)
kubectl apply -f k8s/
```

#### Option 3: Cloud Platforms

- **AWS**: ECS, EKS, RDS, ElastiCache
- **Azure**: AKS, Azure Database for PostgreSQL, Azure Cache for Redis
- **GCP**: GKE, Cloud SQL, Memorystore

---

## 🤝 Contributing

### Git Workflow

```bash
# Tạo feature branch
git checkout -b feature/your-feature-name

# Commit changes
git add .
git commit -m "feat: add new feature description"

# Push to remote
git push origin feature/your-feature-name

# Tạo Pull Request trên GitHub
```

### Commit Message Convention

- `feat`: Tính năng mới
- `fix`: Sửa lỗi
- `docs`: Cập nhật documentation
- `refactor`: Refactor code
- `test`: Thêm tests
- `chore`: Maintenance tasks

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 📧 Contact & Support

- **GitHub Issues**: [Create an issue](https://github.com/FONGFAM/event-ticketing-system/issues)
- **Documentation**: Xem thêm trong thư mục `docs/`
- **Email**: support@eventticket.com

---

## 🙏 Acknowledgments

- Spring Boot Team
- Apache Kafka Community
- Redis Labs
- PostgreSQL Community

---

**Last Updated**: December 29, 2025
**Version**: 1.0.0
