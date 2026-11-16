
# Technical Design & Architecture Document

## 1. Architecture & Layering Strategy
The application adheres strictly to standard enterprise separation of concerns, decoupling transport, business domain, and data persistence layers:

1. **Controller Layer (`CandidateController`):** 
   * Exposes uniform RESTful HTTP endpoints versioned under `/api/v1/candidates`.
   * Enforces declarative Bean Validation on request payloads before business execution.
   * Leverages `@ParameterObject` for clean SpringDoc OpenAPI integration with `Pageable`.

2. **Service Layer (`CandidateService` / `CandidateServiceImpl`):**
   * Encapsulates all transactional boundaries (`@Transactional`).
   * Manages domain invariants, such as duplicate email isolation checks and field trimming.
   * Emits structured SLF4J log events for auditable state transitions.

3. **Repository & Dynamic Specification Layer:**
   * Extends `JpaRepository` for primary CRUD and `JpaSpecificationExecutor` for dynamic Criteria query construction.
   * Translates multi-criteria search queries into a single database-level SQL query, eliminating in-memory filtration.

4. **DTO Layer (Java 17 Records):**
   * Uses immutable records (`CandidateRequest`, `CandidateResponse`, `CandidateSearchCriteria`, `ErrorResponse`).
   * Decouples the external API contract from internal database schemas, preventing over-posting/mass-assignment risks.

5. **Centralized Error Handling (`GlobalExceptionHandler`):**
   * Annotated with `@RestControllerAdvice` to intercept exceptions across all controllers.
   * Maps domain exceptions to specific HTTP status codes (`400 Bad Request`, `404 Not Found`, `409 Conflict`, `500 Server Error`).
   * Prevents internal SQL details and stack traces from leaking to clients.

---

## 2. Dynamic Search & Multi-Criteria Filtering Design
Instead of brittle combinatorial finder methods (e.g., `findByStatusAndMinExp...`), the service uses **Spring Data JPA Specifications**:
* Evaluates non-null query parameters dynamically (`search`, `status`, `minExperience`, `maxExperience`, `skills`).
* Performs case-insensitive wildcard searches across `fullName` and `email` using `cb.lower(...)` and `cb.like(...)`.
* Joins the `@ElementCollection` table `candidate_skills` and applies `query.distinct(true)` to eliminate Cartesian product duplicates.
* Enforces server-side pagination (`Pageable`) with configurable page sizing to protect database and JVM memory.

---

## 3. Data Modeling & Database Design
* **Primary Table (`candidates`):**
  * `id`: 36-character UUID string generated via Hibernate (`@JdbcTypeCode(SqlTypes.VARCHAR)`). UUIDs prevent sequential enumeration attacks and make distributed sharding trivial.
  * `email`: Defined with a unique constraint index (`idx_candidate_email`) to guarantee data integrity at the storage engine level.
  * `years_of_experience`: Indexed (`idx_candidate_experience`) to accelerate numerical range queries.
  * `status`: Stored as a descriptive string enum (`CandidateStatus`).
  * `created_at` / `updated_at`: Populated via JPA lifecycle hooks (`@PrePersist`, `@PreUpdate`).
* **Join Table (`candidate_skills`):**
  * Normalized child table mapping candidate IDs to individual skill strings with foreign key constraints and cascade deletion.

