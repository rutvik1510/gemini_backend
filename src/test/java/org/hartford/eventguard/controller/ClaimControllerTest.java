package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.ClaimRequest;
import org.hartford.eventguard.dto.ClaimResponse;
import org.hartford.eventguard.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimControllerTest {

    @Mock
    private ClaimService claimService;

    @Mock
    private Authentication authentication;

    private ClaimController claimController;

    @BeforeEach
    void setUp() {
        claimController = new ClaimController(claimService);
    }

    @Test
    void fileClaim_Success() {
        ClaimRequest request = new ClaimRequest();
        request.setSubscriptionId(1L);

        ClaimResponse response = new ClaimResponse();
        response.setClaimId(1L);

        when(authentication.getName()).thenReturn("test@test.com");
        when(claimService.fileClaim(any(ClaimRequest.class), eq("test@test.com"))).thenReturn(response);

        ResponseEntity<ApiResponse<ClaimResponse>> result = claimController.fileClaim(request, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1L, result.getBody().getData().getClaimId());
    }

    @Test
    void getCustomerClaims_Success() {
        ClaimResponse response = new ClaimResponse();
        response.setClaimId(1L);

        when(authentication.getName()).thenReturn("test@test.com");
        when(claimService.getCustomerClaimsResponse("test@test.com")).thenReturn(Collections.singletonList(response));

        ResponseEntity<ApiResponse<List<ClaimResponse>>> result = claimController.getCustomerClaims(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getData().size());
    }

    @Test
    void collectClaim_Success() {
        ClaimResponse response = new ClaimResponse();
        response.setClaimId(1L);

        when(authentication.getName()).thenReturn("test@test.com");
        when(claimService.collectClaim(1L, "test@test.com")).thenReturn(response);

        ResponseEntity<ApiResponse<ClaimResponse>> result = claimController.collectClaim(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().getData().getClaimId());
    }
}
