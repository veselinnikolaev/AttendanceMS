# Attendance Management System

## Overview
The Attendance Management System is a microservices-based application designed to efficiently manage and track attendance at the university level. It leverages modern technologies and architectural patterns to ensure scalability, security, and reliability.

## Table of Contents
- [Technologies Used](#technologies-used)
- [Microservices Architecture](#microservices-architecture)
- [Project Structure](#project-structure)
- [Installation and Setup](#installation-and-setup)
- [Usage](#usage)
- [Testing](#testing)
- [Design Patterns Implemented](#design-patterns-implemented)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

## Technologies Used
- **Service Discovery & Configuration Management:** Netflix Eureka  
- **Security & Secrets Management:** HashiCorp Vault  
- **Inter-Service Communication:** RestTemplate for synchronous API calls, RabbitMQ for asynchronous messaging  
- **Asynchronous Processing:** CompletableFuture  
- **Data Mapping:** MapStruct  
- **Entity Representation:** Java Records  
- **Caching & Session Management:** Redis  
- **Load Balancing & API Gateway:** Spring Cloud Gateway with JWT authentication filter  
- **Containerization:** Docker for RabbitMQ and Redis  
- **Email Notifications:** JavaMailSender with Mailtrap SMTP server  

## Microservices Architecture
The system consists of multiple microservices, each responsible for a specific functionality:

### 1. Service Registry
- **Technology:** Netflix Eureka
- **Function:** Acts as a service registry for dynamic discovery of microservices.

### 2. API Gateway
- **Technology:** Spring Cloud Gateway
- **Function:** Serves as the entry point for all client requests, handling routing, load balancing, and JWT-based authentication.

### 3. Auth Service
- **Database:** Redis
- **Function:** Manages user authentication and authorization, issuing JWT tokens stored in Redis for session management.

### 4. User Service
- **Database:** PostgreSQL
- **Function:** Handles user-related operations and data management.

### 5. Attendance Service
- **Database:** MySQL
- **Function:** Manages attendance records, tracking, and validation.

### 6. Category Service
- **Database:** MongoDB
- **Function:** Manages course categories and related metadata.

### 7. Notification Service
- **Function:** Sends email notifications using JavaMailSender configured with Mailtrap SMTP server.

## Project Structure
The repository is organized as follows:

```
AttendanceMS/
├── ApiGateway/
├── AttendanceService/
├── AuthService/
├── CategoryService/
├── NotificationService/
├── ServiceRegistry/
├── UserService/
├── docker-compose.yml
└── README.md
```

- Each directory corresponds to a specific microservice.
- The `docker-compose.yml` file is used for orchestrating Docker containers for RabbitMQ, Redis, and other services.

## Installation and Setup

### Prerequisites
- Docker and Docker Compose installed on your machine.
- Java 17 or higher.
- Maven or Gradle build tool.

### Steps
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/veselinnikolaev/AttendanceMS.git
   cd AttendanceMS
   ```
2. **Start Infrastructure Services:** Use Docker Compose to start RabbitMQ and Redis:
   ```bash
   docker-compose up -d
   ```
3. **Start the Service Registry:**
   ```bash
   cd ServiceRegistry
   ./mvnw spring-boot:run
   ```
4. **Start Each Microservice:** For each service (AuthService, UserService, AttendanceService, CategoryService, NotificationService, ApiGateway), navigate to its directory and execute:
   ```bash
   ./mvnw spring-boot:run
   ```
5. **Access the Application:** The API Gateway will be accessible at [http://localhost:8080/](http://localhost:8080/).

## Usage
Once all services are running:

- **User Registration and Authentication:** Users can register and authenticate through the Auth Service endpoints.
- **Attendance Management:** Authorized users can record and view attendance via the Attendance Service.
- **Category Management:** Manage course categories through the Category Service.
- **Notifications:** The Notification Service will handle sending emails for relevant events.

## Testing
To run tests for each microservice:

1. Navigate to the service's directory.
2. Execute:
   ```bash
   ./mvnw test
   ```
Ensure that all dependent services are running before executing tests.

## Design Patterns Implemented
- **Saga Pattern (Planned):** To manage distributed transactions ensuring data consistency across services.
- **Circuit Breaker Pattern (Planned):** To handle fault tolerance and prevent cascading failures.

## Future Enhancements
- Implement Saga and Circuit Breaker Patterns to enhance reliability and consistency in distributed environments.
- Improve logging and monitoring using ELK Stack (Elasticsearch, Logstash, Kibana) or Prometheus & Grafana.
- Introduce a Web UI for easier management.
- Implement role-based access control (RBAC).

## Contributing
Contributions are welcome! To contribute:
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature-name`).
3. Commit your changes (`git commit -m 'Add new feature'`).
4. Push to the branch (`git push origin feature-name`).
5. Open a Pull Request.

## License
This project is licensed under the MIT License.
