package org.hartford.eventguard.repo;

import org.hartford.eventguard.entity.EventDomain;
import org.hartford.eventguard.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    // Get only active policies (for customers later)
    List<Policy> findByIsActiveTrue();

    // Find policy by name (useful to avoid duplicates)
    Optional<Policy> findByPolicyName(String policyName);

    // Count active policies for dashboard stats
    long countByIsActiveTrue();

    // Find active policies by domain
    List<Policy> findByDomainAndIsActiveTrue(EventDomain domain);
}

