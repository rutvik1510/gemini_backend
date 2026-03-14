package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.DashboardStatsResponse;
import org.hartford.eventguard.entity.Claim;
import org.hartford.eventguard.entity.ClaimStatus;
import org.hartford.eventguard.entity.SubscriptionStatus;
import org.hartford.eventguard.repo.ClaimsRepository;
import org.hartford.eventguard.repo.EventRepository;
import org.hartford.eventguard.repo.PolicyRepository;
import org.hartford.eventguard.repo.PolicySubscriptionRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final PolicyRepository policyRepository;
    private final EventRepository eventRepository;
    private final PolicySubscriptionRepository policySubscriptionRepository;
    private final ClaimsRepository claimsRepository;
    private final UserRepository userRepository;

    public DashboardService(PolicyRepository policyRepository,
                          EventRepository eventRepository,
                          PolicySubscriptionRepository policySubscriptionRepository,
                          ClaimsRepository claimsRepository,
                          UserRepository userRepository) {
        this.policyRepository = policyRepository;
        this.eventRepository = eventRepository;
        this.policySubscriptionRepository = policySubscriptionRepository;
        this.claimsRepository = claimsRepository;
        this.userRepository = userRepository;
    }

    public DashboardStatsResponse getDashboardStats() {
        // 1. Basic counts using JPA methods
        long totalPolicies = policyRepository.count();
        long activePolicies = policyRepository.countByIsActiveTrue();
        long totalEvents = eventRepository.count();
        long totalUsers = userRepository.count();
        long pendingSubscriptions = policySubscriptionRepository.countByStatus(SubscriptionStatus.PENDING);
        
        // 2. Fetch all claims and calculate stats manually for maximum reliability
        List<Claim> allClaims = claimsRepository.findAll();
        long pendingClaims = 0;
        long successfulClaims = 0;
        long settledClaims = 0;
        double calculatedTotalPayouts = 0.0;

        for (Claim claim : allClaims) {
            ClaimStatus status = claim.getStatus();
            
            if (status == ClaimStatus.PENDING) {
                pendingClaims++;
            } 
            else if (status == ClaimStatus.APPROVED || status == ClaimStatus.COLLECTED) {
                successfulClaims++;
                
                // Add to payouts sum
                // Use approvedAmount if set, else fallback to requested claimAmount
                Double amount = claim.getApprovedAmount();
                if (amount == null || amount <= 0) {
                    amount = claim.getClaimAmount();
                }
                
                if (amount != null) {
                    calculatedTotalPayouts += amount;
                }

                if (status == ClaimStatus.COLLECTED) {
                    settledClaims++;
                }
            }
        }

        // 3. Calculate Total Revenue (Paid Premiums)
        Double totalRevenue = policySubscriptionRepository.sumPaidPremiums();
        if (totalRevenue == null) totalRevenue = 0.0;

        // 4. Log calculation for developer visibility
        System.out.println("========== DASHBOARD REFRESH ==========");
        System.out.println("Users: " + totalUsers + " | Events: " + totalEvents);
        System.out.println("Claims [Total: " + allClaims.size() + ", Successful: " + successfulClaims + "]");
        System.out.println("Financials [Revenue: ₹" + totalRevenue + ", Payouts: ₹" + calculatedTotalPayouts + "]");
        System.out.println("=======================================");

        // 5. Build and return response
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalPolicies(totalPolicies);
        stats.setActivePolicies(activePolicies);
        stats.setTotalEvents(totalEvents);
        stats.setTotalUsers(totalUsers);
        stats.setPendingSubscriptions(pendingSubscriptions);
        stats.setPendingClaims(pendingClaims);
        stats.setSuccessfulClaims(successfulClaims);
        stats.setSettledClaims(settledClaims);
        stats.setTotalRevenue(totalRevenue);
        stats.setTotalPayouts(calculatedTotalPayouts);

        return stats;
    }
}
