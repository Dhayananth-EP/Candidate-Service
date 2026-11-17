# AI-Assisted Development Process Document

## 1. Where AI Was Used
AI assistance was utilized as a technical pair programmer across the following areas:
* **Boilerplate Acceleration:** Scaffolding Java 17 immutable record DTOs with Jakarta Bean Validation annotations (`@NotBlank`, `@Email`, `@Min`, `@Max`).
* **JPA Criteria Specification Setup:** Prototyping the initial predicate construction syntax for the `CandidateSpecification` class.
* **Test Case Scaffolding:** Generating Mockito and MockMvc test templates to verify HTTP responses and controller mappings quickly.
* **Docker Configuration:** Generating the initial multi-stage Dockerfile and Docker Compose template for Spring Boot and MySQL container orchestration.

---

## 2. How AI Output Was Validated
Every piece of AI-generated output was reviewed, tested, and validated through multiple checkpoints:
1. **Compilation & Static Typing Checks:** Verified that code complied with Java 21 and Spring Boot 4 standards without deprecated classes.
2. **Automated Unit & Web Tests:** Ran `./mvnw clean test` to confirm all mock interactions, status codes, and JSON assertions passed without errors.
3. **Manual Functional Testing via Postman & Swagger UI:** Tested live boundary inputs (negative experience, duplicate emails, invalid UUIDs, whitespace inputs).
4. **Container Health Verification:** Ran `docker compose up --build` to confirm the application waited for MySQL health checks before initializing Hibernate.

---

## 3. What Changes Were Made to AI-Generated Output
* **MySQL UUID Representation:** AI initially suggested standard JPA `@GeneratedValue` without dialect constraints, which caused binary serialization quirks in MySQL. Adjusted the mapping to `@JdbcTypeCode(SqlTypes.VARCHAR)` with a 36-character length constraint for clean string storage in MySQL.
* **Swagger Pageable Fix:** AI initially generated raw Spring Data `Pageable` in the controller, which caused Swagger UI to render a broken JSON input containing `"sort": ["string"]` that caused 500 runtime errors. Refactored the controller argument with `@ParameterObject` to expose clean URL query parameters (`page`, `size`, `sort`).
* **Distinct Querying for Skill Joins:** Added `query.distinct(true)` to the JPA Criteria query to eliminate duplicate candidate records caused by inner joins on the `candidate_skills` collection table.

---

## 4. Where AI Recommendations Were Rejected and Why
* **Native SQL Concatenation for Dynamic Search:**
    * *AI Recommendation:* The AI suggested building dynamic filtering via a `StringBuilder` using raw Native SQL and appending conditions with string concatenation.
    * *Reason for Rejection:* Rejected due to severe SQL injection vulnerabilities, lack of compile-time type safety, and tight coupling to database dialect syntax. Chose **Spring Data JPA Specifications with CriteriaBuilder** instead.
* **Duplicate Email Validation on Update (`PUT`):**
    * *AI Recommendation:* The AI initially generated an update check using `candidateRepository.existsByEmail(request.email())`.
    * *Reason for Rejection:* This produced a false `409 Conflict` whenever a candidate was updated without altering their email, because their own record matched the check. Rejected the logic and implemented `existsByEmailAndIdNot(email, id)` to verify uniqueness only against other candidate records.
* **Unbounded In-Memory Listing:**
    * *AI Recommendation:* An early recommendation suggested returning `List<CandidateResponse>` for all candidate searches.
    * *Reason for Rejection:* Returning unbounded collections causes memory exhaustion when tables scale. Enforced **Spring Data `Pageable`** with page size constraints.
