package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.AdminSubscriptionResponse;
import org.hartford.eventguard.service.PolicySubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/subscriptions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubscriptionController {

    private final PolicySubscriptionService subscriptionService;

    public AdminSubscriptionController(PolicySubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminSubscriptionResponse>>> getAllSubscriptions() {
        List<AdminSubscriptionResponse> subscriptions = subscriptionService.getAllSubscriptionsForAdmin();
        return ResponseEntity.ok(ApiResponse.success("Subscriptions retrieved successfully", subscriptions));
    }
}
