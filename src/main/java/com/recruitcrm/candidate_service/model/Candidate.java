package com.recruitcrm.candidate_service.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "candidates", indexes = {
        @Index(name = "idx_candidate_email", columnList = "email", unique = true),
        @Index(name = "idx_candidate_experience", columnList = "yearsOfExperience")
})
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 10)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer yearsOfExperience;

    @Column(length = 100)
    private String currentTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CandidateStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "candidate_skills",
            joinColumns = @JoinColumn(name = "candidate_id", referencedColumnName = "id")
    )
    @Column(name = "skill", length = 50)
    private Set<String> skills = new HashSet<>();
    @Column(precision = 12, scale = 2)

    private BigDecimal expectedSalary;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.status == null) {
            this.status = CandidateStatus.APPLIED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Default Constructors, Parameterized Constructor, Getters, and Setters remain identical
    public Candidate() {}

    public Candidate(String fullName, String email, String phoneNumber, Integer yearsOfExperience,
                     String currentTitle, CandidateStatus status, Set<String> skills, BigDecimal expectedSalary) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.currentTitle = currentTitle;
        this.status = status;
        this.skills = skills != null ? skills : new HashSet<>();
        this.expectedSalary = expectedSalary;
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public String getCurrentTitle() { return currentTitle; }
    public void setCurrentTitle(String currentTitle) { this.currentTitle = currentTitle; }
    public CandidateStatus getStatus() { return status; }
    public void setStatus(CandidateStatus status) { this.status = status; }
    public Set<String> getSkills() { return skills; }
    public void setSkills(Set<String> skills) { this.skills = skills; }
    public BigDecimal getExpectedSalary() { return expectedSalary; }
    public void setExpectedSalary(BigDecimal expectedSalary) { this.expectedSalary = expectedSalary; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}