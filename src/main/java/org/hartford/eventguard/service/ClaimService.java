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

    // --- Alias methods for Controller compatibility ---
    public List<ClaimResponse> getCustomerClaimsResponse(String email) {
        return getClaimsForCustomer(email);
    }

    public List<ClaimResponse> getAllClaimsResponse() {
        List<Claim> claims = claimsRepository.findAll();
        return claims.stream().map(this::convertToClaimResponse).collect(Collectors.toList());
    }

    public List<ClaimResponse> getAssignedClaimsResponse(String email) {
        return getClaimsForOfficer(email);
    }

    public ClaimResponse getClaimByIdDTO(Long id) {
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        return convertToClaimResponse(claim);
    }
    // --------------------------------------------------

    public ClaimResponse fileClaim(ClaimRequest request, String email) {
        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch subscription
        PolicySubscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // --- LOCKDOWN CHECK ---
        List<PolicySubscription> eventSubs = subscriptionRepository.findByEvent_EventId(subscription.getEvent().getEventId());
        for (PolicySubscription s : eventSubs) {
            java.util.Optional<Claim> existingClaim = claimsRepository.findByPolicySubscription_SubscriptionId(s.getSubscriptionId());
            if (existingClaim.isPresent() && existingClaim.get().getStatus() == ClaimStatus.COLLECTED) {
                throw new InvalidRequestException("This event is locked. Claim collected.");
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
            throw new InvalidRequestException("Claim amount cannot exceed the policy coverage amount of ₹" + coverage);
        }

        // --- DATE RESTRICTIONS ---
        if (request.getIncidentDate() == null) {
            throw new InvalidRequestException("Incident date is required");
        }

        java.time.LocalDate incidentDate = request.getIncidentDate();
        java.time.LocalDateTime filingDateTime = request.getFiledAt() != null ? request.getFiledAt() : LocalDateTime.now();
        java.time.LocalDate filingDate = filingDateTime.toLocalDate();
        java.time.LocalDate eventDate = subscription.getEvent().getEventDate();

        // 1. Cannot be after the Filing Date
        if (incidentDate.isAfter(filingDate)) {
            throw new InvalidRequestException("Incident date cannot be after the filing date");
        }

        // 2. Must be within +/- 3 days of the event date
        java.time.LocalDate minDate = eventDate.minusDays(3);
        java.time.LocalDate maxDate = eventDate.plusDays(3);

        if (incidentDate.isBefore(minDate) || incidentDate.isAfter(maxDate)) {
            throw new InvalidRequestException("Incident date must be within 3 days of the event date (" + eventDate + ")");
        }

        // Create claim
        Claim claim = new Claim();
        claim.setPolicySubscription(subscription);
        claim.setDescription(request.getDescription());
        claim.setClaimAmount(request.getClaimAmount());
        claim.setIncidentDate(incidentDate);
        claim.setEvidenceDocPath(request.getEvidenceDocPath());
        claim.setStatus(ClaimStatus.PENDING);
        
        // Use the same filing time used for validation
        claim.setFiledAt(filingDateTime);

        Claim savedClaim = claimsRepository.save(claim);

        // Notify Admins
        userRepository.findByRoles_RoleName("ADMIN").forEach(admin -> {
            notificationService.createNotification(admin, 
                "New Claim Filed: ₹" + claim.getClaimAmount() + " for event " + subscription.getEvent().getEventName(), 
                "ALERT");
        });

        return convertToClaimResponse(savedClaim);
    }

    public List<ClaimResponse> getClaimsForCustomer(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Claim> claims = claimsRepository.findByPolicySubscription_Event_User(user);
        return claims.stream()
                .map(this::convertToClaimResponse)
                .collect(Collectors.toList());
    }

    public List<ClaimResponse> getClaimsForOfficer(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Claim> claims = claimsRepository.findByAssignedOfficer(user);
        return claims.stream()
                .map(this::convertToClaimResponse)
                .collect(Collectors.toList());
    }

    public ClaimResponse approveClaim(Long id, String email, Double amount) {
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        User officer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        if (claim.getAssignedOfficer() == null || !claim.getAssignedOfficer().getUserId().equals(officer.getUserId())) {
            throw new UnauthorizedAccessException("Claim not assigned to you");
        }

        claim.setStatus(ClaimStatus.APPROVED);
        
        // Default to full requested amount if no specific amount provided
        if (amount == null || amount <= 0) {
            claim.setApprovedAmount(claim.getClaimAmount());
        } else {
            claim.setApprovedAmount(amount);
        }
        
        claim.setResolvedAt(LocalDateTime.now());
        claim.setResolvedBy(officer);
        
        claimsRepository.save(claim);

        // Notify Customer
        notificationService.createNotification(claim.getPolicySubscription().getEvent().getUser(), 
            "Your claim for " + claim.getPolicySubscription().getEvent().getEventName() + " has been APPROVED for ₹" + amount, 
            "SUCCESS");

        return convertToClaimResponse(claim);
    }

    public ClaimResponse rejectClaim(Long id, String email, String reason) {
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        User officer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        if (claim.getAssignedOfficer() == null || !claim.getAssignedOfficer().getUserId().equals(officer.getUserId())) {
            throw new UnauthorizedAccessException("Claim not assigned to you");
        }

        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRejectionReason(reason);
        claim.setResolvedAt(LocalDateTime.now());
        claim.setResolvedBy(officer);
        
        claimsRepository.save(claim);

        // Notify Customer
        notificationService.createNotification(claim.getPolicySubscription().getEvent().getUser(), 
            "Your claim for " + claim.getPolicySubscription().getEvent().getEventName() + " has been REJECTED. Reason: " + reason, 
            "ALERT");

        return convertToClaimResponse(claim);
    }

    public ClaimResponse collectClaim(Long id, String email) {
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!claim.getPolicySubscription().getEvent().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("Not your claim");
        }

        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new InvalidRequestException("Claim not approved yet");
        }

        claim.setStatus(ClaimStatus.COLLECTED);
        claimsRepository.save(claim);

        return convertToClaimResponse(claim);
    }

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
        dto.setIncidentDate(claim.getIncidentDate());
        dto.setStatus(claim.getStatus().toString());
        dto.setFiledAt(claim.getFiledAt());
        dto.setAssignedOfficerName(claim.getAssignedOfficer() != null ? 
            claim.getAssignedOfficer().getFullName() : "NOT ASSIGNED");
        return dto;
    }

    public ClaimResponseDTO assignClaimsOfficer(Long id, Long officerId) {
        Claim claim = claimsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        claim.setAssignedOfficer(officer);
        claimsRepository.save(claim);

        // Notify Officer
        notificationService.createNotification(officer, 
            "New Claim Assigned: Please review the claim for " + claim.getPolicySubscription().getEvent().getEventName(), 
            "ALERT");

        return convertToDTO(claim);
    }

    private ClaimResponseDTO convertToDTO(Claim claim) {
        ClaimResponseDTO dto = new ClaimResponseDTO();
        dto.setClaimId(claim.getClaimId());
        dto.setStatus(claim.getStatus().toString());
        return dto;
    }

    private ClaimResponse convertToClaimResponse(Claim claim) {
        ClaimResponse dto = new ClaimResponse();

        // Claim info
        dto.setClaimId(claim.getClaimId());
        dto.setSubscriptionId(claim.getPolicySubscription().getSubscriptionId());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setIncidentDate(claim.getIncidentDate());
        dto.setApprovedAmount(claim.getApprovedAmount());
        dto.setEvidenceDocPath(claim.getEvidenceDocPath());
        dto.setDescription(claim.getDescription());
        dto.setStatus(claim.getStatus().toString());
        dto.setRejectionReason(claim.getRejectionReason());
        dto.setFiledAt(claim.getFiledAt());
        dto.setAssignedOfficerName(claim.getAssignedOfficer() != null ? claim.getAssignedOfficer().getFullName() : "NOT ASSIGNED");

        // Customer info
        dto.setCustomerName(claim.getPolicySubscription().getEvent().getUser().getFullName());
        dto.setCustomerPhone(claim.getPolicySubscription().getEvent().getUser().getPhone());

        // Event info
        Event event = claim.getPolicySubscription().getEvent();
        dto.setEventName(event.getEventName());
        dto.setEventType(event.getEventType());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setNumberOfAttendees(event.getNumberOfAttendees());
        dto.setBudget(event.getBudget());

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
}
