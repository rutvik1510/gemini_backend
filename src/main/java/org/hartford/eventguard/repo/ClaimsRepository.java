package org.hartford.eventguard.repo;

import org.hartford.eventguard.entity.Claim;
import org.hartford.eventguard.entity.ClaimStatus;
import org.hartford.eventguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClaimsRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByPolicySubscription_Event_User(User user);

    List<Claim> findByAssignedOfficer(User user);

    long countByStatus(ClaimStatus status);

    boolean existsByPolicySubscription_SubscriptionId(Long subscriptionId);

    @Query("SELECT COALESCE(SUM(c.approvedAmount), 0.0) FROM Claim c WHERE c.status IN (org.hartford.eventguard.entity.ClaimStatus.APPROVED, org.hartford.eventguard.entity.ClaimStatus.COLLECTED)")
    Double sumApprovedPayouts();

    Optional<Claim> findByPolicySubscription_SubscriptionId(Long subscriptionId);
}
