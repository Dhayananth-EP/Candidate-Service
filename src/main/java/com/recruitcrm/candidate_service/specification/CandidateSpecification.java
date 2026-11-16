package com.recruitcrm.candidate_service.specification;

import com.recruitcrm.candidate_service.model.Candidate;
import com.recruitcrm.candidate_service.model.CandidateStatus;
import org.springframework.data.jpa.domain.Specification;
import com.recruitcrm.candidate_service.dto.CandidateSearchCriteria;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;

public class CandidateSpecification {

    public static Specification<Candidate> withCriteria(CandidateSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria == null) {
                return cb.conjunction();
            }

            // 1. Keyword search across fullName OR email (case-insensitive)
            if (criteria.search() != null && !criteria.search().trim().isEmpty()) {
                String searchPattern = "%" + criteria.search().trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("fullName")), searchPattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), searchPattern);
                predicates.add(cb.or(nameMatch, emailMatch));
            }

            // 2. Filter by Status
            if (criteria.status() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.status()));
            }

            // 3. Minimum Experience filter
            if (criteria.minExperience() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("yearsOfExperience"), criteria.minExperience()));
            }

            // 4. Maximum Experience filter
            if (criteria.maxExperience() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("yearsOfExperience"), criteria.maxExperience()));
            }

            // 5. Skills filter (matches if candidate has any of the selected skills)
            if (criteria.skills() != null && !criteria.skills().isEmpty()) {
                Join<Candidate, String> skillsJoin = root.join("skills");
                predicates.add(skillsJoin.in(criteria.skills()));
                // Ensure distinct results because joins can produce duplicate candidate rows
                if (query != null) {
                    query.distinct(true);
                }
            }

            // Combine all active predicates with logical AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}