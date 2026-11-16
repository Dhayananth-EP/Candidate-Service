package com.recruitcrm.candidate_service.dto;

import com.recruitcrm.candidate_service.model.CandidateStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record CandidateRequest (
        @NotBlank(message = "Full name cannot be blank")
@Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
String fullName,

@NotBlank(message = "Email cannot be blank")
@Email(message = "Invalid email format")
String email,

@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
String phoneNumber,

@NotNull(message = "Years of experience is required")
@Min(value = 0, message = "Years of experience must be 0 or greater")
@Max(value = 60, message = "Years of experience cannot exceed 60")
Integer yearsOfExperience,

String currentTitle,
CandidateStatus status,
Set<String> skills,

@PositiveOrZero(message = "Expected salary must be greater than or equal to 0")
        BigDecimal expectedSalary
)
{}
