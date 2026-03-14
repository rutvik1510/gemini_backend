package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.ClaimRequest;
import org.hartford.eventguard.dto.ClaimResponse;
import org.hartford.eventguard.entity.*;
import org.hartford.eventguard.exception.InvalidRequestException;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.repo.ClaimsRepository;
import org.hartford.eventguard.repo.PolicySubscriptionRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimsRepository claimsRepository;

    @Mock
    private PolicySubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ClaimService claimService;

    private User user;
    private PolicySubscription subscription;
    private ClaimRequest claimRequest;
    private Event event;
    private Policy policy;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@test.com");
        user.setFullName("Test User");

        event = new Event();
        event.setEventId(1L);
        event.setUser(user);
        event.setEventDate(LocalDate.now());

        policy = new Policy();
        policy.setMaxCoverageAmount(50000.0);
        policy.setPolicyName("Standard Policy");

        subscription = new PolicySubscription();
        subscription.setSubscriptionId(1L);
        subscription.setEvent(event);
        subscription.setPolicy(policy);
        subscription.setStatus(SubscriptionStatus.PAID);

        claimRequest = new ClaimRequest();
        claimRequest.setSubscriptionId(1L);
        claimRequest.setDescription("Test claim description long enough");
        claimRequest.setClaimAmount(1000.0);
        claimRequest.setIncidentDate(LocalDate.now());
    }

    @Test
    void fileClaim_Success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.findByEvent_EventId(1L)).thenReturn(new ArrayList<>());
        when(claimsRepository.existsByPolicySubscription_SubscriptionId(1L)).thenReturn(false);
        when(claimsRepository.save(any(Claim.class))).thenAnswer(invocation -> {
            Claim c = invocation.getArgument(0);
            c.setClaimId(100L);
            return c;
        });

        ClaimResponse response = claimService.fileClaim(claimRequest, "test@test.com");

        assertNotNull(response);
        assertEquals(100L, response.getClaimId());
        assertEquals(ClaimStatus.PENDING.toString(), response.getStatus());
        verify(claimsRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void fileClaim_SubscriptionNotPaid_ThrowsException() {
        subscription.setStatus(SubscriptionStatus.PENDING);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.findByEvent_EventId(1L)).thenReturn(new ArrayList<>());

        assertThrows(InvalidRequestException.class, () -> {
            claimService.fileClaim(claimRequest, "test@test.com");
        });
    }

    @Test
    void approveClaim_Success() {
        User officer = new User();
        officer.setUserId(2L);
        officer.setEmail("officer@test.com");

        Claim claim = new Claim();
        claim.setClaimId(100L);
        claim.setPolicySubscription(subscription);
        claim.setClaimAmount(1000.0);
        claim.setAssignedOfficer(officer);
        claim.setStatus(ClaimStatus.PENDING);

        when(claimsRepository.findById(100L)).thenReturn(Optional.of(claim));
        when(userRepository.findByEmail("officer@test.com")).thenReturn(Optional.of(officer));

        ClaimResponse response = claimService.approveClaim(100L, "officer@test.com", 1000.0);

        assertEquals(ClaimStatus.APPROVED.toString(), response.getStatus());
        assertEquals(1000.0, response.getApprovedAmount());
        verify(claimsRepository, times(1)).save(claim);
    }

    @Test
    void approveClaim_UnauthorizedOfficer_ThrowsException() {
        User officer = new User();
        officer.setUserId(2L);
        
        User otherOfficer = new User();
        otherOfficer.setUserId(3L);

        Claim claim = new Claim();
        claim.setClaimId(100L);
        claim.setAssignedOfficer(otherOfficer);

        when(claimsRepository.findById(100L)).thenReturn(Optional.of(claim));
        when(userRepository.findByEmail("officer@test.com")).thenReturn(Optional.of(officer));

        assertThrows(org.hartford.eventguard.exception.UnauthorizedAccessException.class, () -> {
            claimService.approveClaim(100L, "officer@test.com", 1000.0);
        });
    }
}
