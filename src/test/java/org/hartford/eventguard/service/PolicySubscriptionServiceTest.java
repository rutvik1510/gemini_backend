package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.*;
import org.hartford.eventguard.entity.*;
import org.hartford.eventguard.exception.InvalidRequestException;
import org.hartford.eventguard.repo.*;
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
class PolicySubscriptionServiceTest {

    @Mock private PolicySubscriptionRepository subscriptionRepository;
    @Mock private EventRepository eventRepository;
    @Mock private PolicyRepository policyRepository;
    @Mock private UserRepository userRepository;
    @Mock private RiskCalculationService riskCalculationService;
    @Mock private ClaimsRepository claimsRepository;
    @Mock private NotificationService notificationService;
    @Mock private RiskRepository riskRepository;

    @InjectMocks
    private PolicySubscriptionService subscriptionService;

    private User user;
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
        event.setEventName("Test Event");
        event.setBudget(10000.0);
        event.setUser(user);
        event.setEventType(EventDomain.OUTDOOR_MUSIC_CONCERT);

        policy = new Policy();
        policy.setPolicyId(1L);
        policy.setPolicyName("Test Policy");
        policy.setBaseRate(10.0);
    }

    @Test
    void calculateQuoteForCustomer_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        
        DetailedRiskBreakdown breakdown = new DetailedRiskBreakdown(5.0, 2.0, "None", null);
        when(riskCalculationService.calculateRiskWithBreakdown(event)).thenReturn(breakdown);
        when(subscriptionRepository.findByEvent_EventId(1L)).thenReturn(new ArrayList<>());

        CustomerSubscriptionResponse response = subscriptionService.calculateQuoteForCustomer(1L, 1L);

        assertNotNull(response);
        assertEquals(1300.0, response.getPremiumAmount()); // 10000 * 0.1 * 1.3
        assertEquals(7.0, response.getRiskPercentage());
    }

    @Test
    void createSubscription_Success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(subscriptionRepository.findByEvent_EventId(1L)).thenReturn(new ArrayList<>());
        when(subscriptionRepository.existsByEvent_EventIdAndPolicy_PolicyId(1L, 1L)).thenReturn(false);
        
        DetailedRiskBreakdown breakdown = new DetailedRiskBreakdown(5.0, 2.0, "None", null);
        when(riskCalculationService.calculateRiskWithBreakdown(event)).thenReturn(breakdown);
        
        Risk riskEntity = new Risk();
        when(riskRepository.save(any(Risk.class))).thenReturn(riskEntity);
        
        PolicySubscription sub = new PolicySubscription();
        sub.setSubscriptionId(1L);
        sub.setEvent(event);
        sub.setPolicy(policy);
        sub.setStatus(SubscriptionStatus.PENDING);
        when(subscriptionRepository.save(any(PolicySubscription.class))).thenReturn(sub);

        CustomerSubscriptionResponse response = subscriptionService.createSubscription(1L, 1L, "test@test.com");

        assertNotNull(response);
        assertEquals(SubscriptionStatus.PENDING.toString(), response.getStatus());
        verify(subscriptionRepository, times(1)).save(any(PolicySubscription.class));
    }

    @Test
    void assignUnderwriter_Success() {
        PolicySubscription sub = new PolicySubscription();
        sub.setSubscriptionId(1L);
        sub.setEvent(event);
        sub.setPolicy(policy);
        sub.setStatus(SubscriptionStatus.PENDING);
        
        User underwriter = new User();
        underwriter.setUserId(2L);
        underwriter.setFullName("Underwriter Name");

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(sub));
        when(userRepository.findById(2L)).thenReturn(Optional.of(underwriter));

        SubscriptionResponseDTO response = subscriptionService.assignUnderwriter(1L, 2L);

        assertNotNull(response);
        assertEquals(underwriter, sub.getAssignedUnderwriter());
        assertEquals(SubscriptionStatus.PENDING.toString(), response.getStatus());
        verify(notificationService, times(1)).createNotification(eq(underwriter), anyString(), eq("ALERT"));
    }
}
