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
    private final NotificationService notificationService;

    public ClaimService(ClaimsRepository claimsRepository, 
                        PolicySubscriptionRepository subscriptionRepository, 
                        UserRepository userRepository,
                        NotificationService notificationService) {
        this.claimsRepository = claimsRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public ClaimResponse fileClaim(ClaimRequest request, String email) {
        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch subscription
        PolicySubscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // --- LOCKDOWN CHECK ---
        List<Claim> eventClaims = claimsRepository.findByPolicySubscription_Event_User(user);
        for (Claim c : eventClaims) {
            if (c.getPolicySubscription().getEvent().getEventId().equals(subscription.getEvent().getEventId())) {
                if (c.getStatus() == ClaimStatus.COLLECTED) {
                    throw new InvalidRequestException("This event is locked. Claim collected.");
                }
            }
        }

        // Validate subscription belongs to user
        if (!subscription.getEvent().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("You do not have permission to file claim for this subscription");
        }

        // Validate subscription is paid
        if (subscription.getStatus() != SubscriptionStatus.PAID) {
            throw new InvalidRequestException("Claim can only be filed for paid policies");
        }

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

        // Notify Admins
        userRepository.findByRoles_RoleName("ADMIN").forEach(admin -> {
            notificationService.createNotification(admin, 
                "Alert: New claim filed (#" + savedClaim.getClaimId() + ") for event " + subscription.getEvent().getEventName(), 
                "ALERT");
        });

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

    public ClaimResponseDTO assignClaimsOfficer(Long id, Long officerId) {
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        // Finality Check: Cannot re-assign if already assigned
        if (claim.getAssignedOfficer() != null) {
            throw new InvalidRequestException("This claim is already assigned to " + claim.getAssignedOfficer().getFullName() + " and cannot be re-assigned.");
        }

        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Claims Officer not found"));

        claim.setAssignedOfficer(officer);
        claimsRepository.save(claim);

        // Notify Officer
        notificationService.createNotification(officer, 
            "Task Assigned: You have been assigned to process claim #" + claim.getClaimId() + " for event " + claim.getPolicySubscription().getEvent().getEventName(), 
            "ALERT");

        return convertToDTO(claim);
    }

    public ClaimResponseDTO approveClaim(Long id, String email, Double approvedAmount) {
        // Fetch claim
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Security Check: Must be assigned
        if (claim.getAssignedOfficer() == null || 
            !claim.getAssignedOfficer().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("You cannot approve this claim as it is not assigned to you.");
        }

        // Update claim
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setResolvedAt(LocalDateTime.now());
        claim.setResolvedBy(user);
        
        if (approvedAmount != null) {
            claim.setApprovedAmount(approvedAmount);
        } else {
            claim.setApprovedAmount(claim.getClaimAmount());
        }

        claimsRepository.save(claim);

        // Notify Customer
        notificationService.createNotification(claim.getPolicySubscription().getEvent().getUser(), 
            "Claim Approved: Your claim for " + claim.getPolicySubscription().getEvent().getEventName() + " has been approved for ₹" + claim.getApprovedAmount() + ". You can now collect your payout.", 
            "SUCCESS");

        return convertToDTO(claim);
    }

    public ClaimResponseDTO rejectClaim(Long id, String email) {
        // Fetch claim
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Security Check: Must be assigned
        if (claim.getAssignedOfficer() == null || 
            !claim.getAssignedOfficer().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("You cannot reject this claim as it is not assigned to you.");
        }

        // Update claim
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setResolvedAt(LocalDateTime.now());
        claim.setResolvedBy(user);

        claimsRepository.save(claim);

        // Notify Customer
        notificationService.createNotification(claim.getPolicySubscription().getEvent().getUser(), 
            "Claim Rejected: Your claim for " + claim.getPolicySubscription().getEvent().getEventName() + " was not approved after detailed review.", 
            "ALERT");

        // Convert to DTO and return
        return convertToDTO(claim);
    }

    public ClaimResponse collectClaim(Long claimId, String email) {
        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch claim
        Claim claim = claimsRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        // Validate claim belongs to user
        if (!claim.getPolicySubscription().getEvent().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("Unauthorized to collect this claim");
        }

        // Validate claim is approved
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new InvalidRequestException("Only approved claims can be collected");
        }

        // Update status
        claim.setStatus(ClaimStatus.COLLECTED);
        claimsRepository.save(claim);

        // Notify resolved officer
        if (claim.getResolvedBy() != null) {
            notificationService.createNotification(claim.getResolvedBy(), 
                "Payout Collected: Customer has collected ₹" + claim.getApprovedAmount() + " for claim #" + claim.getClaimId(), 
                "INFO");
        }

        return convertToClaimResponse(claim);
    }

    private ClaimResponseDTO convertToDTO(Claim claim) {
        ClaimResponseDTO dto = new ClaimResponseDTO();
        dto.setClaimId(claim.getClaimId());
        dto.setSubscriptionId(claim.getPolicySubscription().getSubscriptionId());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setApprovedAmount(claim.getApprovedAmount());
        dto.setEvidenceDocPath(claim.getEvidenceDocPath());
        dto.setDescription(claim.getDescription());
        dto.setStatus(claim.getStatus().toString());
        dto.setResolvedBy(claim.getResolvedBy() != null ? claim.getResolvedBy().getFullName() : null);
        dto.setAssignedOfficerName(claim.getAssignedOfficer() != null ? claim.getAssignedOfficer().getFullName() : null);
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

    public List<ClaimResponse> getAssignedClaimsResponse(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Claim> claims = claimsRepository.findByAssignedOfficer(user);

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
        dto.setSubscriptionId(claim.getPolicySubscription().getSubscriptionId());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setApprovedAmount(claim.getApprovedAmount());
        dto.setEvidenceDocPath(claim.getEvidenceDocPath());
        dto.setDescription(claim.getDescription());
        dto.setStatus(claim.getStatus().toString());
        dto.setFiledAt(claim.getFiledAt());
        dto.setAssignedOfficerName(claim.getAssignedOfficer() != null ? claim.getAssignedOfficer().getFullName() : null);

        // Customer info
        dto.setCustomerName(
            claim.getPolicySubscription().getEvent().getUser().getFullName()
        );
        dto.setCustomerPhone(
            claim.getPolicySubscription().getEvent().getUser().getPhone()
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
        dto.setAssignedOfficerName(claim.getAssignedOfficer() != null ? 
            claim.getAssignedOfficer().getFullName() : "NOT ASSIGNED");
        return dto;
    }
}
