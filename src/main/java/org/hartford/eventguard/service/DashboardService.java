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
    private final org.hartford.eventguard.repo.UserRepository userRepository;

    public DashboardService(PolicyRepository policyRepository,
                          EventRepository eventRepository,
                          PolicySubscriptionRepository policySubscriptionRepository,
                          ClaimsRepository claimsRepository,
                          org.hartford.eventguard.repo.UserRepository userRepository) {
        this.policyRepository = policyRepository;
        this.eventRepository = eventRepository;
        this.policySubscriptionRepository = policySubscriptionRepository;
        this.claimsRepository = claimsRepository;
        this.userRepository = userRepository;
    }

    public DashboardStatsResponse getDashboardStats() {
        // Get counts
        long totalPolicies = policyRepository.count();
        long activePolicies = policyRepository.countByIsActiveTrue();
        long totalEvents = eventRepository.count();
        long totalUsers = userRepository.count();
        long pendingSubscriptions = policySubscriptionRepository.countByStatus(SubscriptionStatus.PENDING);
        long pendingClaims = claimsRepository.countByStatus(ClaimStatus.PENDING);
        
        // Count settled claims (APPROVED or COLLECTED)
        long approved = claimsRepository.countByStatus(ClaimStatus.APPROVED);
        long collected = claimsRepository.countByStatus(ClaimStatus.COLLECTED);
        long settledClaims = approved + collected;

        // Calculate Financials
        Double totalRevenue = policySubscriptionRepository.sumPaidPremiums();
        if (totalRevenue == null) totalRevenue = 0.0;

        Double totalPayouts = claimsRepository.sumApprovedPayouts();
        if (totalPayouts == null) totalPayouts = 0.0;

        // Create and return response
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalPolicies(totalPolicies);
        stats.setActivePolicies(activePolicies);
        stats.setTotalEvents(totalEvents);
        stats.setTotalUsers(totalUsers);
        stats.setPendingSubscriptions(pendingSubscriptions);
        stats.setPendingClaims(pendingClaims);
        stats.setSettledClaims(settledClaims);
        stats.setTotalRevenue(totalRevenue);
        stats.setTotalPayouts(totalPayouts);

        return stats;
    }
}
