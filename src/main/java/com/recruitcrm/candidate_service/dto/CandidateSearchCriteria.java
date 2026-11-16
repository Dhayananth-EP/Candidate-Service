package com.recruitcrm.candidate_service.dto;

import com.recruitcrm.candidate_service.model.CandidateStatus;

import java.util.Set;

public record CandidateSearchCriteria(
        String search,             // Matches fullName or email
        CandidateStatus status,    // e.g. APPLIED, INTERVIEWING
        Integer minExperience,     // e.g. >= 3
        Integer maxExperience,     // e.g. <= 10
        Set<String> skills
) {
}
