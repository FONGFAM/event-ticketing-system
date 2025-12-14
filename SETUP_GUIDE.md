# 🚀 Hướng dẫn Setup & Build Dự án

## 📋 Yêu cầu Hệ thống

- **Java JDK 17+** 
- **Maven 3.8.0+**
- **Docker & Docker Compose** (tùy chọn, cho environment local)
- **Git**

## 📥 Clone & Chuẩn bị

```bash
git clone https://github.com/FONGFAM/event-ticketing-system.git
cd event-ticketing-system
```

## 🔨 Build Project

### Option 1: Build Full Project (Recommended)
```bash
mvn clean install -DskipTests
```

### Option 2: Build Specific Module
```bash
# Build only event-booking-service
mvn clean install -pl event-booking-service -DskipTests

# Build excluding grpc-proto
mvn clean install -pl "!grpc-proto" -DskipTests
```

### Option 3: Build with Tests
```bash
mvn clean install
```

## 📦 Maven Cache & Dependencies

Nếu gặp lỗi download dependencies:
```bash
# Clean Maven local cache
mvn clean

# Redownload all dependencies
mvn -U clean install
```

## ⚙️ Cấu hình IDE (VS Code)

### Recommended Extensions
- Extension Pack for Java (Microsoft)
- Lombok Annotations Support for VS Code
- Maven for Java

### IDE Setup
1. Mở project trong VS Code
2. Chọn JDK 17+ cho Java Language Support
3. Maven sẽ tự động detect pom.xml

## 🚀 Chạy Services

### Option 1: Docker Compose (Full Stack)
```bash
cd infra
docker-compose up -d
```

Này sẽ start:
- PostgreSQL databases (ports 5432)
- Redis (port 6379)
- Kafka (port 9092)
- pgAdmin (port 5050)

### Option 2: Run Services Locally
```bash
# Terminal 1 - Event Booking Service
mvn spring-boot:run -pl event-booking-service

# Terminal 2 - Payment Service  
mvn spring-boot:run -pl payment-service

# Terminal 3 - Ticketing Service
mvn spring-boot:run -pl ticketing-service

# Terminal 4 - Notification Service
mvn spring-boot:run -pl notification-analytics-service

# Terminal 5 - API Gateway
mvn spring-boot:run -pl api-gateway
```

## 🧪 Test

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TestClassName

# Skip tests during build
mvn install -DskipTests
```

## 📂 Cấu trúc Thư mục

```
event-ticketing-system/
├── api-gateway/           # API Gateway (Spring Cloud)
├── common-library/        # Shared libraries & DTOs
├── event-booking-service/ # Event & Seat Management
├── payment-service/       # Payment Processing
├── ticketing-service/     # Ticket Generation (Merged)
├── notification-analytics-service/ # Email & Reports (Merged)
├── grpc-proto/           # gRPC Protocol Buffers
├── infra/                # Docker Compose configurations
├── docs/                 # Documentation
└── pom.xml              # Parent POM

target/                   # ❌ IGNORED - Build outputs
node_modules/            # ❌ IGNORED - Dependencies
.idea/, .vscode/         # ❌ IGNORED - IDE files
```

## 🔧 Troubleshooting

### Lỗi: "Cannot find symbol: log"
- File đã có `@Slf4j` annotation từ Lombok
- Kiểm tra Lombok version trong pom.xml (phải >= 1.18.30)
- Clean IDE cache: `mvn clean`

### Lỗi: "Error reading file" trong gRPC
- Xóa folder `grpc-proto/target`
- Chạy lại: `mvn clean compile -pl grpc-proto`

### Lỗi: Maven command not found
- Thêm Maven vào PATH hoặc dùng: `./mvnw` (Maven Wrapper)
- Hoặc dùng đường dẫn đầy đủ

### Lỗi: Port already in use
- Xóa container Docker đã chạy: `docker-compose down`
- Hoặc change port trong application.yml

## 📝 Git Workflow

```bash
# Cập nhật từ remote
git pull origin main

# Tạo feature branch
git checkout -b feature/your-feature

# Push code
git add .
git commit -m "feat: description"
git push origin feature/your-feature
```

## 🌐 API Endpoints

```
API Gateway (http://localhost:8000)
├── /events          - Event Management
├── /seats           - Seat Management  
├── /payments        - Payment Processing
├── /tickets         - Ticket Operations
└── /reports         - Analytics & Reports
```

## 💡 Các Files Cần Avoid Push

`.gitignore` đã cấu hình để tự động bỏ qua:
- ✅ `target/` - Maven build output
- ✅ `node_modules/` - NPM dependencies
- ✅ `.idea/`, `.vscode/` - IDE cache
- ✅ `*.log` - Log files
- ✅ `.env` - Environment variables
- ✅ `.m2/` - Maven local cache

**DO NOT manually commit**:
- Database files
- API keys/secrets
- IDE configuration
- OS-specific files (Thumbs.db, .DS_Store)

## 📧 Support

For issues or questions:
1. Check existing GitHub issues
2. Create detailed issue with error logs
3. Include Maven/Java version info

## 📄 License

Project License: [Your License]
