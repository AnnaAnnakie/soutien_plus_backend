# SoutienPlus API (Back-End)

This repository contains the back-end RESTful API for the **SoutienPlus** project, a peer-tutoring application developed using **Spring Boot**. It handles business logic, database management, and authentication for the client application.

## 🔗 Related Repositories

* **Front-End Client:** [SoutienPlus Angular Client](https://github.com/AnnaAnnakie/soutien_plus_client.git)

## 📋 Prerequisites

Ensure you have the following installed before setting up the project:

* **Java JDK**: `17` or higher
* **Build Tool**: `Maven` (or use the included `./mvnw` wrapper)
* **Database**: `PostgreSQL` instance running locally or hosted

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/AnnaAnnakie/soutien_plus_backend.git
cd soutien_plus_backend
```

### 2. Database Configuration
Create a PostgreSQL database named `soutien_plus` (or update your database name accordingly).

Configure your credentials in `src/main/resources/application.properties` (or `application.yml`):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/soutien_plus
spring.datasource.username=YOUR_POSTGRES_USER
spring.datasource.password=YOUR_POSTGRES_PASSWORD
```

### 3. Build & Run
Run the application using the Maven wrapper:

* **Linux/macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```
* **Windows:**
  ```cmd
  mvnw spring-boot:run
  ```

By default, the server runs on [http://localhost:8080](http://localhost:8080).

---

## 🏗️ Project Architecture

The application follows a standard layered architecture:

* **Controllers (`/controller`)**: Expose RESTful endpoints to communicate with the front-end.
* **Services (`/service`)**: Encapsulate the core business logic.
* **Repositories (`/repository`)**: Manage data persistence using Spring Data JPA.
* **Entities (`/model` or `/entity`)**: Object-relational mapping (ORM) for PostgreSQL tables.
* **Security (`/security`)**: Configures Spring Security and handles stateless JWT authentication.

---

## 🛠️ Tech Stack & Key Dependencies

* **Spring Boot**: Core framework for backend development
* **Spring Web**: Building REST APIs
* **Spring Data JPA**: Database abstraction layer
* **Spring Security & JWT**: Authentication and role-based access control
* **PostgreSQL**: Relational database engine
* **Lombok**: Reduces boilerplate code (Getters, Setters, Builders)