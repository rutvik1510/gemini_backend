package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.AdminClaimResponse;
import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/claims")
@PreAuthorize("hasRole('ADMIN')")
public class AdminClaimsController {

    private final ClaimService claimService;

    public AdminClaimsController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminClaimResponse>>> getAllClaims() {
        List<AdminClaimResponse> claims = claimService.getAllClaimsForAdmin();
        return ResponseEntity.ok(ApiResponse.success("Claims retrieved successfully", claims));
    }
}
