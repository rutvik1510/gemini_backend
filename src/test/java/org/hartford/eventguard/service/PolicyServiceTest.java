package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.PolicyRequest;
import org.hartford.eventguard.dto.PolicyResponse;
import org.hartford.eventguard.entity.EventDomain;
import org.hartford.eventguard.entity.Policy;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.repo.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    private PolicyRequest request;
    private Policy policy;

    @BeforeEach
    void setUp() {
        request = new PolicyRequest();
        request.setPolicyName("Standard Policy");
        request.setDescription("Covers basic risks");
        request.setDomain(EventDomain.OUTDOOR_MUSIC_CONCERT);
        request.setBaseRate(5.0);
        request.setMaxCoverageAmount(100000.0);
        request.setCoversTheft(true);

        policy = new Policy();
        policy.setPolicyId(1L);
        policy.setPolicyName("Standard Policy");
        policy.setIsActive(true);
    }

    @Test
    void createPolicy_Success() {
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);
        
        Policy result = policyService.createPolicy(request);
        
        assertNotNull(result);
        assertEquals("Standard Policy", result.getPolicyName());
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void getPolicyById_Success() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        
        Policy result = policyService.getPolicyById(1L);
        
        assertEquals(1L, result.getPolicyId());
    }

    @Test
    void getPolicyById_NotFound_ThrowsException() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            policyService.getPolicyById(99L);
        });
    }

    @Test
    void deactivatePolicy_Success() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        
        policyService.deactivatePolicy(1L);
        
        assertFalse(policy.getIsActive());
        verify(policyRepository, times(1)).save(policy);
    }

    @Test
    void getAllActivePolicies_Success() {
        when(policyRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(policy));
        
        List<Policy> results = policyService.getActivePolicies();
        
        assertEquals(1, results.size());
    }
}
