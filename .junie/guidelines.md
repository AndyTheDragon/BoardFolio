# BoardFolio Project Overview

## Introduction


## Project Structure
The project follows a standard MVC architecture with the following components:

### Main Packages
- **config**: Configuration classes for the application, including Hibernate and application configuration
- **controllers**: Controllers for handling HTTP requests
- **dao**: Data Access Objects for database operations
- **dto**: Data Transfer Objects for transferring data between processes
- **entities**: Entity classes representing database tables
- **enums**: Enumeration types
- **exceptions**: Custom exception classes
- **routes**: Route definitions for the API
- **utils**: Utility classes

### Domain Model
The application manages the following entities:


## Technologies Used
- **Java 17**: Programming language
- **Maven**: Build tool
- **Hibernate**: ORM for database operations
- **Javalin**: Web framework for building RESTful APIs
- **PostgreSQL**: Database
- **JBCrypt & TokenSecurity**: Security libraries for authentication and authorization
- **JUnit, RestAssured, TestContainers**: Testing frameworks
- **Lombok**: Reduces boilerplate code
- **Jackson**: JSON handling
- **SLF4J & Logback**: Logging

## Getting Started
1. Clone the repository
2. Create a `config.properties` file in the resources directory with the following properties:
   ```
   DB_NAME=your_db_name
   DB_USERNAME=postgres
   DB_PASSWORD=your_password
   SECRET_KEY=minimum32characterslong
   ISSUER=your_issuer
   TOKEN_EXPIRE_TIME=3600000
   ```
   The DB_NAME, DB_USERNAME, DB_PASSWORD, ISSUER, and TOKEN_EXPIRE_TIME properties should be filled in with the appropriate values. The SECRET_KEY property should be a minimum of 32 characters long.
3. Build the project with Maven: `mvn clean package`
4. Run the application: `java -jar target/app.jar`

## API Endpoints
The application provides the following main endpoints:



- **Security**:
    - POST `/api/auth/login`: Authenticate a user
    - POST `/api/auth/register`: Register a new user

## Testing
The project includes unit tests and integration tests using JUnit, RestAssured, and TestContainers. Tests can be run with Maven: `mvn test`

## Deployment
The application can be deployed as a Docker container using the provided Dockerfile.
