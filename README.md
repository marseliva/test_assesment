# Example Assignment

Welcome to this **Example** repository. This is a **Spring Boot** project built for an **assignment**. The code, while functional, **is not production-ready** and **may contain questionable or non-ideal implementations**. Part of the challenge is to **discover**, **review**, and **improve** these elements.

---

## What to Expect

- A **simple** REST API for managing `Patients` and their `Appointments`.
- Multiple classes (controllers, services, entities, and repositories).
- **Incomplete** or **inefficient** approaches to certain tasks.

---

## Glossary

Below are the primary entities you’ll find in this codebase:

1. **Patient**
- Represents an individual in the hospital system.
- Fields may include:
   - `id`: auto-generated primary key
   - `name`: name of the patient
   - `ssn`: Social Security Number (used here as a unique identifier)
   - `appointments`: a list of `Appointment` objects linked to this patient

2. **Appointment**
- Represents a scheduled appointment or event for a patient.
- Fields may include:
   - `id`: auto-generated primary key
   - `reason`: a textual reason for the appointment (e.g., “Checkup”)
   - `date`: the date of the appointment
   - `patient`: a reference to the `Patient` who owns this appointment

---

## Goals

1. **Explore the codebase**: Familiarize yourself with the structure and logic.
2. **Identify potential issues**: Think about security, performance, maintainability, design patterns, etc.
3. **Propose and/or implement improvements**: Refactor, rewrite, or reorganize parts of the code to showcase your approach.

---

## Project Structure

```plaintext
assignment/
├── src/
    ├── main/
        ├── java/      # Java source code
        └── resources/ # Resource files
    └── test/
        ├── java/      # Test source code
        └── resources/
├── build.gradle.kts      # Gradle build configuration
├── Dockerfile        # Docker build file
├── docker-compose.yml # Docker Compose file
├── settings.gradle.kts   # Gradle settings file
├── README.md         # Project overview

```

## Installation & Setup

### Using Gradle

```bash

# Clean previous builds and install dependencies
./gradlew clean build

# Alternatively, if you prefer
./gradlew clean install

# Start services without logs
docker-compose up -d

# Stop services
docker-compose down
```

## Components Used

- **Java 21** — Base runtime environment (OpenJDK 21-slim)
- **PostgreSQL 15** — Relational database for persistent storage
- **Spring Boot (assumed)** — Framework for building Java applications

# Appointment Service API

A Spring Boot REST API for managing medical appointments. All endpoints are secured and only accessible by users with the **DOCTOR** role.

## Core Features

- **Bulk Create Appointments**  
  Create multiple appointment records in a single request.
- **Search by Reason**  
  Retrieve all appointments that match a given “reason” string.
- **Delete by SSN**  
  Remove all appointments for a patient identified by their SSN, and return the count of deleted records.
- **Get Latest Appointment**  
  Quickly fetch the most recent appointment for a patient by SSN.
- **Role-Based Access Control**  
  All operations are restricted to users with the `DOCTOR` role using Spring Security’s `@PreAuthorize`.
- **Input Validation**  
  Request payloads and query parameters are validated using `@Valid`, `@NotBlank` and `@Validated`.

## API Endpoints (check http://localhost:8080/swagger-ui/index.html#/)

| HTTP Method | Path                       | Parameters / Body                        | Description                                                                   |
|-------------|----------------------------|------------------------------------------|-------------------------------------------------------------------------------|
| **POST**    | `/api/appointments/bulk`   | **Body**: `CreateAppointmentRequestBody` | Create multiple appointments in a single batch.                               |
| **GET**     | `/api/appointments`        | **Query**: `reason=string`               | Find all appointments whose reason contains the given (non-blank) value.      |
| **DELETE**  | `/api/appointments`        | **Query**: `ssn=string`                  | Delete all appointments for the patient with this SSN; return deletion count. |
| **GET**     | `/api/appointments/latest` | **Query**: `ssn=string`                  | Retrieve the most recent appointment for the patient with this SSN.           |


## Future Improvements

- **Increase Test Coverage**  
  Due to time constraints, the current code quality and coverage are not optimal. We should add more unit and integration tests with additional validation checks to ensure stability and prevent regressions.

- **Enhance Exception Handling**  
  Refine our error-handling strategy by introducing custom exception classes where appropriate, centralizing exception mapping, and providing clearer, more actionable error responses.

- **Refactor Analytics Logging**  
  The `HospitalUtils.recordUsage` method (and its static field) has been removed because it introduced a memory leak and unclear responsibilities. If we need hospital‑level usage statistics, this should be handled by a dedicated analytics service.

- **Strengthen Security Configuration**  
  While basic security settings are in place, they aren’t thoroughly tested in local environments. Since this service operates as one of many microservices, it should fully embrace JWT‑based authentication and include comprehensive integration tests for end‑to‑end security flows.
