package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.AdminClaimResponse;
import org.hartford.eventguard.dto.ClaimRequest;
import org.hartford.eventguard.dto.ClaimResponse;
import org.hartford.eventguard.dto.ClaimResponseDTO;
import org.hartford.eventguard.entity.Claim;
import org.hartford.eventguard.entity.ClaimStatus;
import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.PolicySubscription;
import org.hartford.eventguard.entity.SubscriptionStatus;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.exception.InvalidRequestException;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.exception.UnauthorizedAccessException;
import org.hartford.eventguard.repo.ClaimsRepository;
import org.hartford.eventguard.repo.PolicySubscriptionRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    private final ClaimsRepository claimsRepository;
    private final PolicySubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @org.springframework.beans.factory.annotation.Value("${claim.validate.event-date:true}")
    private boolean validateEventDate;

    public ClaimService(ClaimsRepository claimsRepository,
                        PolicySubscriptionRepository subscriptionRepository,
                        UserRepository userRepository) {
        this.claimsRepository = claimsRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    public ClaimResponse fileClaim(ClaimRequest request, String email) {
        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch subscription
        PolicySubscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Validate subscription belongs to user
        if (!subscription.getEvent().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("You do not have permission to file claim for this subscription");
        }

        // Validate subscription is approved
        if (subscription.getStatus() != SubscriptionStatus.APPROVED) {
            throw new InvalidRequestException("Claim can only be filed for approved policies");
        }

        // Validation temporarily disabled for development testing.
        // In production, claims should only be allowed after the event date.
        // if (validateEventDate) {
        //     Event event = subscription.getEvent();
        //     if (java.time.LocalDate.now().isBefore(event.getEventDate())) {
        //         throw new InvalidRequestException("Claims can only be filed after the event date");
        //     }
        // }

        // Validate no existing claim for this subscription
        boolean claimExists = claimsRepository
                .existsByPolicySubscription_SubscriptionId(request.getSubscriptionId());

        if (claimExists) {
            throw new InvalidRequestException("A claim already exists for this subscription");
        }

        // Validate claim description
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new InvalidRequestException("Claim description is required");
        }

        // Validate claim amount
        if (request.getClaimAmount() == null || request.getClaimAmount() <= 0) {
            throw new InvalidRequestException("Claim amount must be greater than zero");
        }

        // Validate claim amount doesn't exceed policy coverage
        Double coverage = subscription.getPolicy().getMaxCoverageAmount();
        if (request.getClaimAmount() > coverage) {
            throw new InvalidRequestException("Claim amount cannot exceed the policy coverage amount");
        }

        // Create claim
        Claim claim = new Claim();
        claim.setPolicySubscription(subscription);
        claim.setDescription(request.getDescription());
        claim.setClaimAmount(request.getClaimAmount());
        claim.setStatus(ClaimStatus.PENDING);
        claim.setFiledAt(LocalDateTime.now());

        Claim savedClaim = claimsRepository.save(claim);

        // Convert to DTO and return
        return convertToClaimResponse(savedClaim);
    }

    public List<Claim> getCustomerClaims(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return claimsRepository.findByPolicySubscription_Event_User(user);
    }

    public List<ClaimResponseDTO> getCustomerClaimsDTO(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Claim> claims = claimsRepository.findByPolicySubscription_Event_User(user);

        return claims.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<Claim> getAllClaims() {
        return claimsRepository.findAll();
    }

    public ClaimResponseDTO approveClaim(Long id, String email) {
        // Fetch claim
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update claim
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setResolvedAt(LocalDateTime.now());
        claim.setResolvedBy(user);

        claimsRepository.save(claim);

        // Convert to DTO and return
        return convertToDTO(claim);
    }

    public ClaimResponseDTO rejectClaim(Long id, String email) {
        // Fetch claim
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update claim
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setResolvedAt(LocalDateTime.now());
        claim.setResolvedBy(user);

        claimsRepository.save(claim);

        // Convert to DTO and return
        return convertToDTO(claim);
    }

    private ClaimResponseDTO convertToDTO(Claim claim) {
        ClaimResponseDTO dto = new ClaimResponseDTO();
        dto.setClaimId(claim.getClaimId());
        dto.setSubscriptionId(claim.getPolicySubscription().getSubscriptionId());
        dto.setDescription(claim.getDescription());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setStatus(claim.getStatus().toString());
        dto.setResolvedBy(claim.getResolvedBy() != null ? claim.getResolvedBy().getFullName() : null);
        dto.setFiledAt(claim.getFiledAt());
        dto.setResolvedAt(claim.getResolvedAt());
        return dto;
    }

    public List<ClaimResponse> getCustomerClaimsResponse(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Claim> claims = claimsRepository.findByPolicySubscription_Event_User(user);

        return claims.stream()
                .map(this::convertToClaimResponse)
                .collect(Collectors.toList());
    }

    public List<ClaimResponse> getAllClaimsResponse() {
        List<Claim> claims = claimsRepository.findAll();

        return claims.stream()
                .map(this::convertToClaimResponse)
                .collect(Collectors.toList());
    }

    public ClaimResponse getClaimByIdDTO(Long id) {
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        return convertToClaimResponse(claim);
    }

    private ClaimResponse convertToClaimResponse(Claim claim) {
        ClaimResponse dto = new ClaimResponse();

        // Claim info
        dto.setClaimId(claim.getClaimId());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setDescription(claim.getDescription());
        dto.setStatus(claim.getStatus().toString());
        dto.setFiledAt(claim.getFiledAt());

        // Customer info
        dto.setCustomerName(
            claim.getPolicySubscription().getEvent().getUser().getFullName()
        );

        // Event info
        dto.setEventName(claim.getPolicySubscription().getEvent().getEventName());
        dto.setEventType(claim.getPolicySubscription().getEvent().getEventType());
        dto.setEventDate(claim.getPolicySubscription().getEvent().getEventDate());
        dto.setLocation(claim.getPolicySubscription().getEvent().getLocation());
        dto.setNumberOfAttendees(claim.getPolicySubscription().getEvent().getNumberOfAttendees());
        dto.setBudget(claim.getPolicySubscription().getEvent().getBudget());

        // Policy info
        dto.setPolicyName(claim.getPolicySubscription().getPolicy().getPolicyName());
        dto.setBaseRate(claim.getPolicySubscription().getPolicy().getBaseRate());
        dto.setMaxCoverageAmount(claim.getPolicySubscription().getPolicy().getMaxCoverageAmount());
        dto.setPremiumAmount(claim.getPolicySubscription().getPremiumAmount());

        // Risk info
        dto.setEventRisk(claim.getPolicySubscription().getEventRisk());
        dto.setWeatherRisk(claim.getPolicySubscription().getWeatherRisk());
        dto.setTotalRisk(claim.getPolicySubscription().getTotalRisk());
        dto.setRiskLevel(calculateRiskLevel(claim.getPolicySubscription().getTotalRisk()));

        // Weather info
        dto.setTemperature(claim.getPolicySubscription().getTemperature());
        dto.setHumidity(claim.getPolicySubscription().getHumidity());
        dto.setWindSpeed(claim.getPolicySubscription().getWindSpeed());
        dto.setWeatherCondition(claim.getPolicySubscription().getWeatherCondition());

        return dto;
    }

    private String calculateRiskLevel(Double risk) {
        if (risk == null) return "UNKNOWN";
        if (risk >= 10) return "HIGH";
        if (risk >= 5) return "MEDIUM";
        return "LOW";
    }

    // Admin method to get all claims
    public List<AdminClaimResponse> getAllClaimsForAdmin() {
        List<Claim> claims = claimsRepository.findAll();

        return claims.stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
    }

    private AdminClaimResponse convertToAdminDTO(Claim claim) {
        AdminClaimResponse dto = new AdminClaimResponse();
        dto.setClaimId(claim.getClaimId());
        dto.setEventName(claim.getPolicySubscription().getEvent().getEventName());
        dto.setCustomerName(claim.getPolicySubscription().getEvent().getUser().getFullName());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setStatus(claim.getStatus().toString());
        dto.setFiledAt(claim.getFiledAt());
        return dto;
    }
}
