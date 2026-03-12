package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.ClaimResponse;
import org.hartford.eventguard.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/claims-officer/claims")
@PreAuthorize("hasAnyRole('CLAIMS_OFFICER', 'ADMIN')")
public class ClaimsOfficerController {

    private final ClaimService claimService;

    public ClaimsOfficerController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getAllClaims() {
        List<ClaimResponse> claims = claimService.getAllClaimsResponse();
        return ResponseEntity.ok(ApiResponse.success("Claims retrieved successfully", claims));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClaimResponse>> getClaimDetails(@PathVariable Long id) {
        ClaimResponse claim = claimService.getClaimByIdDTO(id);
        return ResponseEntity.ok(ApiResponse.success("Claim details retrieved successfully", claim));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<String>> approveClaim(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        claimService.approveClaim(id, email);
        return ResponseEntity.ok(ApiResponse.success("Claim approved"));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<String>> rejectClaim(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        claimService.rejectClaim(id, email);
        return ResponseEntity.ok(ApiResponse.success("Claim rejected"));
    }
}
