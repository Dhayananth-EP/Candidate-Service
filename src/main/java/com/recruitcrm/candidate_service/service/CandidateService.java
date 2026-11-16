package com.recruitcrm.candidate_service.service;

import com.recruitcrm.candidate_service.dto.CandidateRequest;
import com.recruitcrm.candidate_service.dto.CandidateResponse;
import com.recruitcrm.candidate_service.dto.CandidateSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CandidateService {
    CandidateResponse createCandidate(CandidateRequest request);
    CandidateResponse getCandidateById(UUID id);
    CandidateResponse updateCandidate(UUID id, CandidateRequest request);
    void deleteCandidate(UUID id);
    Page<CandidateResponse> searchCandidates(CandidateSearchCriteria criteria, Pageable pageable);
}
