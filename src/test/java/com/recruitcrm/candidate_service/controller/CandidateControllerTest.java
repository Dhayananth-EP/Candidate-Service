package com.recruitcrm.candidate_service.controller;

import com.recruitcrm.candidate_service.controller.CandidateController;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.recruitcrm.candidate_service.dto.CandidateRequest;
import com.recruitcrm.candidate_service.dto.CandidateResponse;
import com.recruitcrm.candidate_service.model.CandidateStatus;
import com.recruitcrm.candidate_service.service.CandidateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CandidateController.class)
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CandidateService candidateService;

    @Autowired
    private JsonMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/candidates - Valid Request Returns 201 Created")
    void testCreateCandidate_Valid() throws Exception {
        CandidateRequest request = new CandidateRequest(
                "Alex Hunter",
                "alex@example.com",
                "+1987654321",
                3,
                "Java Developer",
                CandidateStatus.APPLIED,
                Set.of("Java", "Spring Boot"),
                BigDecimal.valueOf(90000)
        );

        CandidateResponse response = new CandidateResponse(
                UUID.randomUUID(),
                request.fullName(),
                request.email(),
                request.phoneNumber(),
                request.yearsOfExperience(),
                request.currentTitle(),
                request.status(),
                request.skills(),
                request.expectedSalary(),
                Instant.now(),
                Instant.now()
        );

        when(candidateService.createCandidate(any(CandidateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Alex Hunter"))
                .andExpect(jsonPath("$.email").value("alex@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/candidates - Invalid Email Returns 400 Bad Request")
    void testCreateCandidate_InvalidEmail() throws Exception {
        CandidateRequest invalidRequest = new CandidateRequest(
                "Alex Hunter",
                "invalid-email-address",
                "+1987654321",
                3,
                "Java Developer",
                CandidateStatus.APPLIED,
                Set.of("Java"),
                BigDecimal.valueOf(90000)
        );

        mockMvc.perform(post("/api/v1/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }
    @Test
    @DisplayName("GET /api/v1/candidates/{id} - Existing ID Returns 200 OK")
    void testGetCandidateById_Success() throws Exception {
        UUID id = UUID.randomUUID();
        CandidateResponse response = new CandidateResponse(
                id, "John Smith", "john@example.com", "+1234567890", 5,
                "Engineer", CandidateStatus.APPLIED, Set.of("Java"),
                BigDecimal.valueOf(90000), Instant.now(), Instant.now()
        );

        when(candidateService.getCandidateById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/candidates/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/candidates/{id} - Non-Existent ID Returns 404 Not Found")
    void testGetCandidateById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(candidateService.getCandidateById(id))
                .thenThrow(new com.recruitcrm.candidate_service.exception.ResourceNotFoundException("Candidate not found"));

        mockMvc.perform(get("/api/v1/candidates/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Candidate not found"));
    }

    @Test
    @DisplayName("GET /api/v1/candidates - Search with Filters and Pagination Returns 200 OK")
    void testSearchCandidates_WithFilters() throws Exception {
        org.springframework.data.domain.Page<CandidateResponse> emptyPage =
                new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());

        when(candidateService.searchCandidates(any(), any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/candidates")
                        .param("search", "john")
                        .param("status", "APPLIED")
                        .param("minExperience", "3")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/candidates - Negative Experience Returns 400 Bad Request")
    void testCreateCandidate_NegativeExperience() throws Exception {
        CandidateRequest invalidRequest = new CandidateRequest(
                "Negative Exp User",
                "valid.email@example.com",
                "+1234567890",
                -3, // Invalid negative value
                "Developer",
                CandidateStatus.APPLIED,
                Set.of("Java"),
                BigDecimal.valueOf(50000)
        );

        mockMvc.perform(post("/api/v1/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.yearsOfExperience").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/candidates/{id} - Existing ID Returns 204 No Content")
    void testDeleteCandidate_Success() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(candidateService).deleteCandidate(id);

        mockMvc.perform(delete("/api/v1/candidates/{id}", id))
                .andExpect(status().isNoContent());
    }
}