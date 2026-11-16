package com.recruitcrm.candidate_service.service;

import com.recruitcrm.candidate_service.dto.CandidateRequest;
import com.recruitcrm.candidate_service.dto.CandidateResponse;
import com.recruitcrm.candidate_service.exception.DuplicateResourceException;
import com.recruitcrm.candidate_service.exception.ResourceNotFoundException;
import com.recruitcrm.candidate_service.model.Candidate;
import com.recruitcrm.candidate_service.model.CandidateStatus;
import com.recruitcrm.candidate_service.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    private CandidateRequest sampleRequest;
    private Candidate sampleCandidate;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleRequest = new CandidateRequest(
                "Jane Doe",
                "jane.doe@example.com",
                "+1234567890",
                4,
                "Backend Engineer",
                CandidateStatus.APPLIED,
                Set.of("Java", "MySQL"),
                BigDecimal.valueOf(85000)
        );

        sampleCandidate = new Candidate(
                sampleRequest.fullName(),
                sampleRequest.email(),
                sampleRequest.phoneNumber(),
                sampleRequest.yearsOfExperience(),
                sampleRequest.currentTitle(),
                sampleRequest.status(),
                sampleRequest.skills(),
                sampleRequest.expectedSalary()
        );
    }

    @Test
    @DisplayName("Create Candidate - Success")
    void testCreateCandidate_Success() {
        when(candidateRepository.existsByEmail(sampleRequest.email())).thenReturn(false);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(sampleCandidate);

        CandidateResponse response = candidateService.createCandidate(sampleRequest);

        assertNotNull(response);
        assertEquals("Jane Doe", response.fullName());
        assertEquals("jane.doe@example.com", response.email());
        verify(candidateRepository, times(1)).save(any(Candidate.class));
    }

    @Test
    @DisplayName("Create Candidate - Duplicate Email Throws DuplicateResourceException")
    void testCreateCandidate_DuplicateEmail() {
        when(candidateRepository.existsByEmail(sampleRequest.email())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> candidateService.createCandidate(sampleRequest));
        verify(candidateRepository, never()).save(any(Candidate.class));
    }

    @Test
    @DisplayName("Get Candidate By ID - Not Found Throws ResourceNotFoundException")
    void testGetCandidateById_NotFound() {
        when(candidateRepository.findById(sampleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> candidateService.getCandidateById(sampleId));
    }
    @Test
    @DisplayName("Get Candidate By ID - Success Returns CandidateResponse")
    void testGetCandidateById_Success() {
        when(candidateRepository.findById(sampleId)).thenReturn(Optional.of(sampleCandidate));

        CandidateResponse response = candidateService.getCandidateById(sampleId);

        assertNotNull(response);
        assertEquals(sampleCandidate.getEmail(), response.email());
        verify(candidateRepository, times(1)).findById(sampleId);
    }


    @Test
    @DisplayName("Delete Candidate - Success")
    void testDeleteCandidate_Success() {
        when(candidateRepository.existsById(sampleId)).thenReturn(true);
        doNothing().when(candidateRepository).deleteById(sampleId);

        assertDoesNotThrow(() -> candidateService.deleteCandidate(sampleId));

        verify(candidateRepository, times(1)).deleteById(sampleId);
    }

    @Test
    @DisplayName("Delete Candidate - Non-Existent ID Throws ResourceNotFoundException")
    void testDeleteCandidate_NotFound() {
        when(candidateRepository.existsById(sampleId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> candidateService.deleteCandidate(sampleId));
        verify(candidateRepository, never()).deleteById(sampleId);
    }

}