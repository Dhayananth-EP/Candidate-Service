package com.recruitcrm.candidate_service.service;


import com.recruitcrm.candidate_service.dto.CandidateRequest;
import com.recruitcrm.candidate_service.dto.CandidateResponse;
import com.recruitcrm.candidate_service.dto.CandidateSearchCriteria;

import com.recruitcrm.candidate_service.exception.DuplicateResourceException;
import com.recruitcrm.candidate_service.exception.ResourceNotFoundException;
import com.recruitcrm.candidate_service.model.Candidate;
import com.recruitcrm.candidate_service.repository.CandidateRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.recruitcrm.candidate_service.specification.CandidateSpecification;
import org.springframework.data.jpa.domain.Specification;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// ... other imports

@Service
@Transactional
public class CandidateServiceImpl implements CandidateService {

    private static final Logger log = LoggerFactory.getLogger(CandidateServiceImpl.class);
    private final CandidateRepository candidateRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Override
    public CandidateResponse createCandidate(CandidateRequest request) {
        log.info("Received request to create candidate with email: {}", request.email());

        if (candidateRepository.existsByEmail(request.email())) {
            log.warn("Candidate creation rejected: email {} already exists", request.email());
            throw new DuplicateResourceException("Candidate with email " + request.email() + " already exists");
        }

        Candidate candidate = new Candidate(
                request.fullName(),
                request.email(),
                request.phoneNumber(),
                request.yearsOfExperience(),
                request.currentTitle(),
                request.status(),
                request.skills(),
                request.expectedSalary()
        );

        Candidate saved = candidateRepository.save(candidate);
        log.info("Successfully created candidate with ID: {}", saved.getId());
        return CandidateResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateResponse getCandidateById(UUID id) {
        log.debug("Fetching candidate details for ID: {}", id);
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Candidate lookup failed: ID {} not found", id);
                    return new ResourceNotFoundException("Candidate not found with id: " + id);
                });
        return CandidateResponse.fromEntity(candidate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CandidateResponse> searchCandidates(CandidateSearchCriteria criteria, Pageable pageable) {
        log.info("Searching candidates with criteria: search='{}', status={}, minExp={}, maxExp={}, skills={}",
                criteria.search(), criteria.status(), criteria.minExperience(), criteria.maxExperience(), criteria.skills());

        Specification<Candidate> spec = CandidateSpecification.withCriteria(criteria);
        Page<CandidateResponse> page = candidateRepository.findAll(spec, pageable)
                .map(CandidateResponse::fromEntity);

        log.debug("Search query returned {} candidates on page {}", page.getNumberOfElements(), page.getNumber());
        return page;
    }

    @Override
    public CandidateResponse updateCandidate(UUID id, CandidateRequest request) {
        log.info("Updating candidate ID: {}", id);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Candidate update failed: ID {} does not exist", id);
                    return new ResourceNotFoundException("Candidate not found with id: " + id);
                });

        String trimmedEmail = request.email().trim();
        if (candidateRepository.existsByEmailAndIdNot(trimmedEmail, id)) {
            log.warn("Candidate update failed: Email {} is used by another record", trimmedEmail);
            throw new DuplicateResourceException("Email " + trimmedEmail + " is already in use by another candidate");
        }

        candidate.setFullName(request.fullName().trim());
        candidate.setEmail(trimmedEmail);
        candidate.setPhoneNumber(request.phoneNumber());
        candidate.setYearsOfExperience(request.yearsOfExperience());
        candidate.setCurrentTitle(request.currentTitle());
        if (request.status() != null) {
            candidate.setStatus(request.status());
        }
        candidate.setSkills(request.skills());
        candidate.setExpectedSalary(request.expectedSalary());

        Candidate updated = candidateRepository.save(candidate);
        log.info("Successfully updated candidate ID: {}", id);
        return CandidateResponse.fromEntity(updated);
    }

    @Override
    public void deleteCandidate(UUID id) {
        log.info("Request to delete candidate ID: {}", id);
        if (!candidateRepository.existsById(id)) {
            log.warn("Candidate deletion failed: ID {} does not exist", id);
            throw new ResourceNotFoundException("Candidate not found with id: " + id);
        }
        candidateRepository.deleteById(id);
        log.info("Successfully deleted candidate ID: {}", id);
    }

}