Project Overview: 
Spring Boot backend application implementing a secure, role-based authentication and authorization system with JWT-based token management. 
Designed as a modular REST API backend supporting a full-stack Angular frontend.

The application includes:
- Custom JWT authentication server logic.
- Role-based access control. (RBAC)
- User management with multiple permission levels.
- Database schema management.
- Exception handling layer.
- Full CRUD operations.
- Integration-ready API structure.

Tech Stack:
- Java
- Spring Boot
- Spring Security
- JWT (custom authentication implementation)
- JPA / Hibernate
- MySQL (configurable)
- Flyway (database migrations & seed data)
- Maven

Architecture Overview:
- The application follows a layered architecture:
    Controller → Service → Repository → Database
  
Key architectural elements:
- Custom JWT authentication flow.
- Role hierarchy: USER, MANAGER, ADMIN, SUPER_ADMIN.
- Permission-based endpoint access.
- Centralized exception handling.
- DTO-based API communication.
- Database schema definitions within the application.
- Flyway migrations for schema setup and default admin data.
- Security is enforced using Spring Security with custom filters for token validation and role extraction.
- Authorization decisions are made based on role and permission checks at endpoint level.
- Authentication & Authorization Design.
- Login & registration endpoints issue JWT tokens.
- Tokens contain role and permission claims.
- Role-based endpoint protection.
- 2-step email confirmation. (requires valid email configuration)
- Custom exception responses for unauthorized / forbidden access.

This project demonstrates practical implementation of authentication flows beyond basic Spring defaults.

Database & Data Handling:
- Relational schema managed through JPA entities.
- Flyway used for schema migration and default data insertion.
- Default administrative users seeded automatically.
- CRUD operations for user and domain entities.

Error Handling & Validation:
- Centralized exception handling using @ControllerAdvice.
- Custom exception classes.
- Structured error response model.
- Input validation at DTO level.

How to Run Locally:
  The application can be started locally using:
  mvn spring-boot:run      or by running the main Spring Boot application class.

Important:
- Email-based 2FA and account confirmation require valid SMTP configuration.

Without proper email configuration: Registration confirmation emails will not be sent, 2-step verification may not complete.
- For testing purposes, email verification logic can be disabled or mocked in the configuration layer.
- Database configuration must be adjusted in application.yml or application.properties.

Design Decisions:
- JWT chosen for stateless authentication.
- Layered architecture for separation of concerns.
- Flyway for reproducible database state.
- Custom security configuration to deeply understand authentication mechanisms rather than relying purely on defaults.
- Clear separation between backend logic and frontend integration.




Thanks for reading, alexberbo :)
