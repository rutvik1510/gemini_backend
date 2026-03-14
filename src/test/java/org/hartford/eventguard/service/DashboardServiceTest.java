package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.DashboardStatsResponse;
import org.hartford.eventguard.entity.Claim;
import org.hartford.eventguard.entity.ClaimStatus;
import org.hartford.eventguard.entity.SubscriptionStatus;
import org.hartford.eventguard.repo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private PolicyRepository policyRepository;
    @Mock private EventRepository eventRepository;
    @Mock private PolicySubscriptionRepository policySubscriptionRepository;
    @Mock private ClaimsRepository claimsRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboardStats_Success() {
        when(policyRepository.count()).thenReturn(10L);
        when(policyRepository.countByIsActiveTrue()).thenReturn(8L);
        when(eventRepository.count()).thenReturn(50L);
        when(userRepository.count()).thenReturn(100L);
        when(policySubscriptionRepository.countByStatus(SubscriptionStatus.PENDING)).thenReturn(5L);
        
        Claim c1 = new Claim();
        c1.setStatus(ClaimStatus.PENDING);
        
        Claim c2 = new Claim();
        c2.setStatus(ClaimStatus.APPROVED);
        c2.setApprovedAmount(5000.0);
        
        Claim c3 = new Claim();
        c3.setStatus(ClaimStatus.COLLECTED);
        c3.setClaimAmount(3000.0);
        
        List<Claim> claims = Arrays.asList(c1, c2, c3);
        when(claimsRepository.findAll()).thenReturn(claims);
        when(policySubscriptionRepository.sumPaidPremiums()).thenReturn(20000.0);

        DashboardStatsResponse stats = dashboardService.getDashboardStats();

        assertNotNull(stats);
        assertEquals(10L, stats.getTotalPolicies());
        assertEquals(8L, stats.getActivePolicies());
        assertEquals(5L, stats.getPendingSubscriptions());
        assertEquals(1L, stats.getPendingClaims());
        assertEquals(2L, stats.getSuccessfulClaims());
        assertEquals(1L, stats.getSettledClaims());
        assertEquals(20000.0, stats.getTotalRevenue());
        assertEquals(8000.0, stats.getTotalPayouts()); // 5000 + 3000
    }
}
