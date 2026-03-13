package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.ApproveSubscriptionRequest;
import org.hartford.eventguard.dto.SubscriptionResponseDTO;
import org.hartford.eventguard.dto.UnderwriterSubscriptionDetailsResponse;
import org.hartford.eventguard.dto.UnderwriterSubscriptionResponse;
import org.hartford.eventguard.service.PolicySubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/underwriter/subscriptions")
@PreAuthorize("hasAnyRole('UNDERWRITER', 'ADMIN')")
public class UnderwriterSubscriptionController {

    private final PolicySubscriptionService subscriptionService;

    public UnderwriterSubscriptionController(PolicySubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UnderwriterSubscriptionResponse>>> getAllSubscriptions() {
        List<UnderwriterSubscriptionResponse> subscriptions = subscriptionService.getAllSubscriptionsForUnderwriter();
        return ResponseEntity.ok(ApiResponse.success("Subscriptions retrieved successfully", subscriptions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnderwriterSubscriptionDetailsResponse>> getSubscriptionDetails(
            @PathVariable Long id) {
        UnderwriterSubscriptionDetailsResponse details = subscriptionService.getSubscriptionDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription details retrieved successfully", details));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<SubscriptionResponseDTO>> approveSubscription(
            @PathVariable Long id,
            @RequestBody(required = false) ApproveSubscriptionRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        Double override = (request != null) ? request.getPremiumOverrideAmount() : null;
        String reason = (request != null) ? request.getOverrideReason() : null;

        SubscriptionResponseDTO response = subscriptionService.approveSubscription(id, email, override, reason);
        return ResponseEntity.ok(ApiResponse.success("Subscription approved successfully", response));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<String>> rejectSubscription(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Authentication authentication) {

        String email = authentication.getName();
        subscriptionService.rejectSubscription(id, email, reason);
        return ResponseEntity.ok(ApiResponse.success("Subscription rejected"));
    }
}
