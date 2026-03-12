package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.DashboardStatsResponse;
import org.hartford.eventguard.entity.ClaimStatus;
import org.hartford.eventguard.entity.SubscriptionStatus;
import org.hartford.eventguard.repo.ClaimsRepository;
import org.hartford.eventguard.repo.EventRepository;
import org.hartford.eventguard.repo.PolicyRepository;
import org.hartford.eventguard.repo.PolicySubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final PolicyRepository policyRepository;
    private final EventRepository eventRepository;
    private final PolicySubscriptionRepository policySubscriptionRepository;
    private final ClaimsRepository claimsRepository;

    public DashboardService(PolicyRepository policyRepository,
                          EventRepository eventRepository,
                          PolicySubscriptionRepository policySubscriptionRepository,
                          ClaimsRepository claimsRepository) {
        this.policyRepository = policyRepository;
        this.eventRepository = eventRepository;
        this.policySubscriptionRepository = policySubscriptionRepository;
        this.claimsRepository = claimsRepository;
    }

    public DashboardStatsResponse getDashboardStats() {
        // Get total policies
        long totalPolicies = policyRepository.count();

        // Get active policies
        long activePolicies = policyRepository.countByIsActiveTrue();

        // Get total events
        long totalEvents = eventRepository.count();

        // Get pending subscriptions
        long pendingSubscriptions = policySubscriptionRepository.countByStatus(SubscriptionStatus.PENDING);

        // Get pending claims
        long pendingClaims = claimsRepository.countByStatus(ClaimStatus.PENDING);

        // Create and return response
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalPolicies(totalPolicies);
        stats.setActivePolicies(activePolicies);
        stats.setTotalEvents(totalEvents);
        stats.setPendingSubscriptions(pendingSubscriptions);
        stats.setPendingClaims(pendingClaims);

        return stats;
    }
}
