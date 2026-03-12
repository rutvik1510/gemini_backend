package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.CustomerSubscriptionResponse;
import org.hartford.eventguard.dto.QuoteRequest;
import org.hartford.eventguard.dto.QuoteResponse;
import org.hartford.eventguard.dto.SubscriptionRequest;
import org.hartford.eventguard.service.PolicySubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class PolicySubscriptionController {

    private final PolicySubscriptionService subscriptionService;

    public PolicySubscriptionController(PolicySubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerSubscriptionResponse>> createSubscription(
            @RequestBody SubscriptionRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        CustomerSubscriptionResponse response = subscriptionService.createSubscription(
                request.getEventId(),
                request.getPolicyId(),
                email
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription created successfully", response));
    }

    @PostMapping("/quote")
    public ResponseEntity<ApiResponse<CustomerSubscriptionResponse>> calculateQuote(
            @RequestBody QuoteRequest request) {
        CustomerSubscriptionResponse response = subscriptionService.calculateQuoteForCustomer(
                request.getEventId(),
                request.getPolicyId()
        );
        return ResponseEntity.ok(ApiResponse.success("Quote generated", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerSubscriptionResponse>>> getCustomerSubscriptions(
            Authentication authentication) {
        String email = authentication.getName();
        List<CustomerSubscriptionResponse> subscriptions = subscriptionService.getCustomerSubscriptionsDTO(email);
        return ResponseEntity.ok(ApiResponse.success("Subscriptions retrieved successfully", subscriptions));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<CustomerSubscriptionResponse>>> getMySubscriptions(
            Authentication authentication) {
        String email = authentication.getName();
        List<CustomerSubscriptionResponse> subscriptions = subscriptionService.getCustomerSubscriptionsDTO(email);
        return ResponseEntity.ok(ApiResponse.success("Subscriptions retrieved successfully", subscriptions));
    }

    @PostMapping("/{subscriptionId}/pay-premium")
    public ResponseEntity<ApiResponse<String>> payPremium(
            @PathVariable Long subscriptionId,
            Authentication authentication) {
        String email = authentication.getName();
        String message = subscriptionService.payPremium(subscriptionId, email);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }
}
