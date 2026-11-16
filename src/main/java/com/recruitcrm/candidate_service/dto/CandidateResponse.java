package com.recruitcrm.candidate_service.dto;

import com.recruitcrm.candidate_service.model.Candidate;
import com.recruitcrm.candidate_service.model.CandidateStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record CandidateResponse (
        UUID id,
        String fullName,
        String email,
        String phoneNumber,
        Integer yearsOfExperience,
        String currentTitle,
        CandidateStatus status,
        Set<String> skills,
        BigDecimal expectedSalary,
        Instant createdAt,
        Instant updatedAt
){
    public static CandidateResponse fromEntity(Candidate candidate) {
        return new CandidateResponse(
                candidate.getId(),
                candidate.getFullName(),
                candidate.getEmail(),
                candidate.getPhoneNumber(),
                candidate.getYearsOfExperience(),
                candidate.getCurrentTitle(),
                candidate.getStatus(),
                candidate.getSkills(),
                candidate.getExpectedSalary(),
                candidate.getCreatedAt(),
                candidate.getUpdatedAt()
        );
}}

