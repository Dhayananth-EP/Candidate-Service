# Candidate Management Service

A RESTful API built using **Java 21, Spring Boot 3, and MySQL** for managing, searching, and filtering candidate profiles.

The application supports candidate CRUD operations, pagination, sorting, filtering, and centralized exception handling.

## Prerequisites

Before running the application, ensure the following software is installed:

- **Git** – To clone the repository.
- **Docker Desktop** – To build and run the application and MySQL database using Docker Compose.

Download Docker Desktop: https://www.docker.com/products/docker-desktop/

> Note: You do not need to install Java or MySQL separately if you are running the application entirely through Docker Compose.

## Instructions to Run the Application

### Run Using Docker Compose (Recommended)

Docker Compose automatically builds and starts both the Candidate Management Service and the MySQL database.

**Step 1: Clone the Repository**

Open a terminal and navigate to the directory where you want to store the project.

Example:
   **cd Downloads**

Clone the repository:
   **git clone YOUR_REPOSITORY_URL**

Navigate to the project directory:
   **cd candidate-service**

**Step 2: Start Docker Desktop**

Ensure Docker Desktop is installed and running.

**Step 3: Build and Start the Application**

Execute the following command from the project root directory:

   **docker compose up --build**


This command will:

- Build the Spring Boot application.
- Create and start the MySQL database container.
- Wait for the database health check, if configured.
- Start the Candidate Management Service.
- Configure communication between the application and database.

**Step 4: Verify Application Startup**

Wait until the application starts successfully.

You should see a message similar to:

Started CandidateServiceApplication in X seconds

The application will be accessible at:

http://localhost:8080

## API Documentation and Testing

The application provides interactive API documentation through Swagger UI.

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

Open Swagger UI to explore and test the available REST API endpoints.

You can perform candidate creation, retrieval, updating, deletion, searching, and filtering operations directly from Swagger UI.

## Stopping the Application

To stop the application and database containers, execute:

   **docker compose down**

This stops and removes the containers while preserving database data stored in a Docker volume.

To stop the containers and delete the database volume (including persisted database data), execute:

   **docker compose down -v**

**Note:** Use the second command only when you want to reset the database completely.
