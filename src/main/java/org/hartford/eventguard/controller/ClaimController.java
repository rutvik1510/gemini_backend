package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.ClaimRequest;
import org.hartford.eventguard.dto.ClaimResponse;
import org.hartford.eventguard.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClaimResponse>> fileClaim(
            @RequestBody ClaimRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        ClaimResponse claimResponse = claimService.fileClaim(request, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Claim filed successfully", claimResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getCustomerClaims(Authentication authentication) {
        String email = authentication.getName();
        List<ClaimResponse> claims = claimService.getCustomerClaimsResponse(email);
        return ResponseEntity.ok(ApiResponse.success("Claims retrieved successfully", claims));
    }

    @PutMapping("/{id}/collect")
    public ResponseEntity<ApiResponse<ClaimResponse>> collectClaim(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        ClaimResponse response = claimService.collectClaim(id, email);
        return ResponseEntity.ok(ApiResponse.success("Claim payment collected successfully", response));
    }
}
