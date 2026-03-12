package org.hartford.eventguard.repo;

import org.hartford.eventguard.entity.PolicySubscription;
import org.hartford.eventguard.entity.SubscriptionStatus;
import org.hartford.eventguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PolicySubscriptionRepository extends JpaRepository<PolicySubscription, Long> {

    List<PolicySubscription> findByEvent_User(User user);

    // Count subscriptions by status for dashboard stats
    long countByStatus(SubscriptionStatus status);

    // Check for existing subscription to prevent duplicates
    Optional<PolicySubscription> findByEvent_EventIdAndPolicy_PolicyId(Long eventId, Long policyId);

    boolean existsByEvent_EventIdAndPolicy_PolicyId(Long eventId, Long policyId);

    List<PolicySubscription> findByEvent_EventId(Long eventId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(ps.premiumAmount) FROM PolicySubscription ps WHERE ps.status = 'PAID'")
    Double sumPaidPremiums();

    // Fetch subscription with all related entities
    @Query("""
        SELECT ps FROM PolicySubscription ps
        JOIN FETCH ps.event e
        JOIN FETCH ps.policy p
        JOIN FETCH e.user u
        WHERE ps.subscriptionId = :id
        """)
    Optional<PolicySubscription> findSubscriptionWithDetails(@Param("id") Long id);
}
