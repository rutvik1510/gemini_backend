package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.PolicyRequest;
import org.hartford.eventguard.dto.PolicyResponse;
import org.hartford.eventguard.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/policies")
public class AdminPolicyController {

    @Autowired
    private PolicyService policyService;

    @PostMapping
    public ResponseEntity<ApiResponse<PolicyResponse>> createPolicy(@RequestBody PolicyRequest request) {
        PolicyResponse policy = policyService.createPolicyDTO(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Policy created successfully", policy));
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PolicyResponse>> getPolicyById(@PathVariable Long id) {
        PolicyResponse policy = policyService.getPolicyByIdDTO(id);
        return ResponseEntity.ok(ApiResponse.success("Policy retrieved successfully", policy));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updatePolicy(@PathVariable Long id, @RequestBody PolicyRequest request) {
        policyService.updatePolicy(id, request);
        return ResponseEntity.ok(ApiResponse.success("Policy updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deactivatePolicy(@PathVariable Long id) {
        policyService.deactivatePolicy(id);
        return ResponseEntity.ok(ApiResponse.success("Policy deactivated successfully"));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<String>> activatePolicy(@PathVariable Long id) {
        policyService.activatePolicy(id);
        return ResponseEntity.ok(ApiResponse.success("Policy activated successfully"));
    }
}

