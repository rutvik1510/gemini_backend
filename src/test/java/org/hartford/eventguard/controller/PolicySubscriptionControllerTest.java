package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.CustomerSubscriptionResponse;
import org.hartford.eventguard.dto.SubscriptionRequest;
import org.hartford.eventguard.service.PolicySubscriptionService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicySubscriptionControllerTest {

    @Mock
    private PolicySubscriptionService subscriptionService;

    @Mock
    private Authentication authentication;

    private PolicySubscriptionController controller;

    @BeforeEach
    void setUp() {
        controller = new PolicySubscriptionController(subscriptionService);
    }

    @Test
    void createSubscription_Success() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setEventId(1L);
        request.setPolicyId(1L);

        CustomerSubscriptionResponse response = new CustomerSubscriptionResponse();
        response.setSubscriptionId(100L);

        when(authentication.getName()).thenReturn("test@test.com");
        when(subscriptionService.createSubscription(1L, 1L, "test@test.com")).thenReturn(response);

        ResponseEntity<ApiResponse<CustomerSubscriptionResponse>> result = controller.createSubscription(request, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(100L, result.getBody().getData().getSubscriptionId());
    }

    @Test
    void getCustomerSubscriptions_Success() {
        CustomerSubscriptionResponse response = new CustomerSubscriptionResponse();
        response.setSubscriptionId(100L);

        when(authentication.getName()).thenReturn("test@test.com");
        when(subscriptionService.getCustomerSubscriptionsDTO("test@test.com")).thenReturn(Collections.singletonList(response));

        ResponseEntity<ApiResponse<List<CustomerSubscriptionResponse>>> result = controller.getCustomerSubscriptions(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getData().size());
    }

    @Test
    void payPremium_Success() {
        when(authentication.getName()).thenReturn("test@test.com");
        when(subscriptionService.payPremium(100L, "test@test.com")).thenReturn("Paid");

        ResponseEntity<ApiResponse<String>> result = controller.payPremium(100L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Paid", result.getBody().getData());
    }
}
