package org.hartford.eventguard.repo;

import org.hartford.eventguard.entity.Claim;
import org.hartford.eventguard.entity.ClaimStatus;
import org.hartford.eventguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimsRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByPolicySubscription_Event_User(User user);

    // Count claims by status for dashboard stats
    long countByStatus(ClaimStatus status);

    // Check if claim already exists for subscription
    boolean existsByPolicySubscription_SubscriptionId(Long subscriptionId);

    java.util.Optional<Claim> findByPolicySubscription_SubscriptionId(Long subscriptionId);
}
