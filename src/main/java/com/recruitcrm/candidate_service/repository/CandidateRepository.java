package com.recruitcrm.candidate_service.repository;

import com.recruitcrm.candidate_service.model.Candidate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID>, JpaSpecificationExecutor<Candidate> {
    boolean existsByEmail(String email);
    Optional<Candidate> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);}
