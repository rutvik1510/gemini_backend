package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.PolicyResponse;
import org.hartford.eventguard.entity.EventDomain;
import org.hartford.eventguard.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PolicyResponse>>> getAllPolicies() {
        List<PolicyResponse> policies = policyService.getAllPoliciesDTO();
        return ResponseEntity.ok(ApiResponse.success("Policies retrieved successfully", policies));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PolicyResponse>>> getActivePolicies() {
        List<PolicyResponse> policies = policyService.getActivePoliciesDTO();
        return ResponseEntity.ok(ApiResponse.success("Active policies retrieved successfully", policies));
    }

    @GetMapping("/domain/{domain}")
    public ResponseEntity<ApiResponse<List<PolicyResponse>>> getPoliciesByDomain(
            @PathVariable EventDomain domain) {
        List<PolicyResponse> policies = policyService.getPoliciesByDomain(domain);
        return ResponseEntity.ok(ApiResponse.success("Policies retrieved successfully", policies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PolicyResponse>> getPolicyById(@PathVariable Long id) {
        PolicyResponse policy = policyService.getPolicyByIdDTO(id);
        return ResponseEntity.ok(ApiResponse.success("Policy retrieved successfully", policy));
    }
}
