package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.*;
import org.hartford.eventguard.entity.*;
import org.hartford.eventguard.exception.InvalidRequestException;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.exception.UnauthorizedAccessException;
import org.hartford.eventguard.repo.ClaimsRepository;
import org.hartford.eventguard.repo.EventRepository;
import org.hartford.eventguard.repo.PolicyRepository;
import org.hartford.eventguard.repo.PolicySubscriptionRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicySubscriptionService {

    private final PolicySubscriptionRepository subscriptionRepository;
    private final EventRepository eventRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final RiskCalculationService riskCalculationService;
    private final ClaimsRepository claimsRepository;
    private final NotificationService notificationService;
    private final org.hartford.eventguard.repo.RiskRepository riskRepository;

    public PolicySubscriptionService(PolicySubscriptionRepository subscriptionRepository,
                                     EventRepository eventRepository,
                                     PolicyRepository policyRepository,
                                     UserRepository userRepository,
                                     RiskCalculationService riskCalculationService,
                                     ClaimsRepository claimsRepository,
                                     NotificationService notificationService,
                                     org.hartford.eventguard.repo.RiskRepository riskRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.eventRepository = eventRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.riskCalculationService = riskCalculationService;
        this.claimsRepository = claimsRepository;
        this.notificationService = notificationService;
        this.riskRepository = riskRepository;
    }

    public CustomerSubscriptionResponse calculateQuoteForCustomer(Long eventId, Long policyId) {
        // Fetch Event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Fetch Policy
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        // Calculate detailed risk breakdown
        DetailedRiskBreakdown riskBreakdown = riskCalculationService.calculateRiskWithBreakdown(event);
        double totalRisk = riskBreakdown.getEventRisk() + riskBreakdown.getWeatherRisk();

        // --- Risk Multiplier Logic ---
        double basePremium = event.getBudget() * (policy.getBaseRate() / 100.0);
        double multiplier = 1.0;

        if (totalRisk > 15) {
            multiplier = 2.0; // Critical
        } else if (totalRisk > 10) {
            multiplier = 1.6; // High
        } else if (totalRisk > 5) {
            multiplier = 1.3; // Medium
        }

        double premiumAmount = basePremium * multiplier;

        // Convert to response DTO (without saving subscription)
        CustomerSubscriptionResponse dto = new CustomerSubscriptionResponse();
        dto.setEventId(eventId);
        dto.setPolicyId(policyId);
        dto.setEventName(event.getEventName());
        dto.setEventDate(event.getEventDate());
        dto.setPolicyName(policy.getPolicyName());
        dto.setBaseRate(policy.getBaseRate());
        dto.setMaxCoverageAmount(policy.getMaxCoverageAmount());
        dto.setPremiumAmount(premiumAmount);
        dto.setStatus("QUOTE");
        dto.setPaid(false);
        dto.setRiskPercentage(totalRisk);
        dto.setEventRisk(riskBreakdown.getEventRisk());
        dto.setWeatherRisk(riskBreakdown.getWeatherRisk());

        // Check if locked
        boolean isLocked = false;
        List<PolicySubscription> eventSubs = subscriptionRepository.findByEvent_EventId(eventId);
        for (PolicySubscription s : eventSubs) {
            java.util.Optional<Claim> existingClaim = claimsRepository.findByPolicySubscription_SubscriptionId(s.getSubscriptionId());
            if (existingClaim.isPresent() && existingClaim.get().getStatus() == ClaimStatus.COLLECTED) {
                isLocked = true;
                break;
            }
        }
        dto.setIsLocked(isLocked);
        
        String riskLevel = "LOW";
        if (totalRisk > 10) riskLevel = "HIGH";
        else if (totalRisk > 5) riskLevel = "MEDIUM";
        dto.setRiskLevel(riskLevel);
        
        return dto;
    }

    public CustomerSubscriptionResponse createSubscription(Long eventId, Long policyId, String email) {
        // Fetch User using email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch Event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // --- LOCKDOWN CHECK: CLAIM COLLECTED ---
        List<PolicySubscription> eventSubs = subscriptionRepository.findByEvent_EventId(eventId);
        for (PolicySubscription s : eventSubs) {
            java.util.Optional<Claim> existingClaim = claimsRepository.findByPolicySubscription_SubscriptionId(s.getSubscriptionId());
            if (existingClaim.isPresent() && existingClaim.get().getStatus() == ClaimStatus.COLLECTED) {
                throw new InvalidRequestException("This event is locked. A claim has already been collected and no further operations are allowed.");
            }
        }

        // --- DUPLICATE/PAID CHECK ---
        for (PolicySubscription s : eventSubs) {
            if (s.getStatus() == SubscriptionStatus.PAID) {
                throw new InvalidRequestException("An active (paid) policy already exists for this event. You cannot subscribe to multiple policies for the same event.");
            }
        }

        // Fetch Policy
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        // Validate event belongs to logged in user
        if (!event.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("You do not have permission to create subscription for this event");
        }

        // Check for existing subscription to prevent duplicates
        boolean exists = subscriptionRepository
                .existsByEvent_EventIdAndPolicy_PolicyId(eventId, policyId);

        if (exists) {
            throw new InvalidRequestException("This policy is already subscribed for the event");
        }

        // Calculate detailed risk breakdown
        DetailedRiskBreakdown riskBreakdown = riskCalculationService.calculateRiskWithBreakdown(event);
        double totalRisk = riskBreakdown.getEventRisk() + riskBreakdown.getWeatherRisk();

        // Save detailed Risk entity
        Risk riskEntity = new Risk();
        riskEntity.setEventRiskScore(riskBreakdown.getEventRisk());
        riskEntity.setWeatherRiskScore(riskBreakdown.getWeatherRisk());
        riskEntity.setTotalRiskScore(totalRisk);
        riskEntity.setRiskPercentage(totalRisk);
        riskEntity.setRiskFactors(riskBreakdown.getRiskFactors());
        
        String riskLevel = "LOW";
        if (totalRisk > 10) riskLevel = "HIGH";
        else if (totalRisk > 5) riskLevel = "MEDIUM";
        riskEntity.setRiskLevel(riskLevel);
        
        Risk savedRisk = riskRepository.save(riskEntity);

        // --- NEW SIMPLE LOGIC: Risk Multiplier ---
        double basePremium = event.getBudget() * (policy.getBaseRate() / 100.0);
        double multiplier = 1.0;

        if (totalRisk > 15) {
            multiplier = 2.0; // Critical
        } else if (totalRisk > 10) {
            multiplier = 1.6; // High
        } else if (totalRisk > 5) {
            multiplier = 1.3; // Medium
        }

        double premiumAmount = basePremium * multiplier;

        // Create new PolicySubscription object
        PolicySubscription subscription = new PolicySubscription();
        subscription.setEvent(event);
        subscription.setPolicy(policy);
        subscription.setRiskDetails(savedRisk);

        // Store risk breakdown (legacy fields for backward compatibility if needed)
        subscription.setEventRisk(riskBreakdown.getEventRisk());
        subscription.setWeatherRisk(riskBreakdown.getWeatherRisk());
        subscription.setTotalRisk(totalRisk);
        subscription.setRiskPercentage(totalRisk);

        // Store weather data
        WeatherRiskResponse weatherData = riskBreakdown.getWeatherData();
        if (weatherData != null) {
            subscription.setTemperature(weatherData.getTemperature());
            subscription.setWindSpeed(weatherData.getWindSpeed());
            subscription.setHumidity(weatherData.getRainProbability() != null ? weatherData.getRainProbability() * 0.6 : 0.0);
            subscription.setWeatherCondition(getWeatherCondition(weatherData));
        }

        subscription.setPremiumAmount(premiumAmount);
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setRequestedAt(LocalDateTime.now());

        // Save subscription
        PolicySubscription savedSubscription = subscriptionRepository.save(subscription);

        return convertToCustomerDTO(savedSubscription);
    }

    public List<CustomerSubscriptionResponse> getCustomerSubscriptionsDTO(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<PolicySubscription> subscriptions = subscriptionRepository.findByEvent_User(user);

        return subscriptions.stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

    public List<PolicySubscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public SubscriptionResponseDTO assignUnderwriter(Long id, Long underwriterId) {
        // Fetch subscription
        PolicySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Finality Check: Cannot re-assign if already assigned
        if (subscription.getAssignedUnderwriter() != null) {
            throw new InvalidRequestException("This policy is already assigned to underwriter " + subscription.getAssignedUnderwriter().getFullName() + " and cannot be re-assigned.");
        }

        // Fetch underwriter user
        User underwriter = userRepository.findById(underwriterId)
                .orElseThrow(() -> new ResourceNotFoundException("Underwriter not found"));

        // Update assignment
        subscription.setAssignedUnderwriter(underwriter);
        subscriptionRepository.save(subscription);

        // Notify Underwriter
        notificationService.createNotification(underwriter, 
            "Action Required: A new policy subscription for " + subscription.getEvent().getEventName() + " has been assigned to you for review.", 
            "ALERT");

        // Convert to DTO and return
        return convertToDTO(subscription);
    }

    public SubscriptionResponseDTO approveSubscription(Long id, String email, Double overrideAmount, String reason) {
        // Fetch subscription
        PolicySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Security Check: Must be assigned
        if (subscription.getAssignedUnderwriter() == null || 
            !subscription.getAssignedUnderwriter().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("You cannot approve this subscription as it is not assigned to you.");
        }

        // Update subscription
        subscription.setStatus(SubscriptionStatus.APPROVED);
        subscription.setApprovedAt(LocalDateTime.now());
        subscription.setApprovedBy(user);
        
        if (overrideAmount != null) {
            subscription.setPremiumOverrideAmount(overrideAmount);
            subscription.setOverrideReason(reason);
            subscription.setPremiumAmount(overrideAmount);
        }

        subscriptionRepository.save(subscription);

        // Notify Customer
        notificationService.createNotification(subscription.getEvent().getUser(), 
            "Great news! Your insurance subscription for " + subscription.getEvent().getEventName() + " has been APPROVED. You can now pay your premium.", 
            "SUCCESS");

        return convertToDTO(subscription);
    }

    public SubscriptionResponseDTO rejectSubscription(Long id, String email, String reason) {
        // Fetch subscription
        PolicySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Security Check: Must be assigned
        if (subscription.getAssignedUnderwriter() == null || 
            !subscription.getAssignedUnderwriter().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("You cannot reject this subscription as it is not assigned to you.");
        }

        // Update subscription
        subscription.setStatus(SubscriptionStatus.REJECTED);
        subscription.setApprovedAt(LocalDateTime.now());
        subscription.setApprovedBy(user);
        subscription.setRejectionReason(reason);

        subscriptionRepository.save(subscription);

        // Notify Customer
        notificationService.createNotification(subscription.getEvent().getUser(), 
            "Update: Your insurance subscription for " + subscription.getEvent().getEventName() + " was not approved. Reason: " + reason, 
            "ALERT");

        return convertToDTO(subscription);
    }

    public String payPremium(Long subscriptionId, String email) {
        PolicySubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // --- LOCKDOWN CHECK ---
        Long eventId = subscription.getEvent().getEventId();
        List<PolicySubscription> eventSubs = subscriptionRepository.findByEvent_EventId(eventId);
        for (PolicySubscription s : eventSubs) {
            java.util.Optional<Claim> existingClaim = claimsRepository.findByPolicySubscription_SubscriptionId(s.getSubscriptionId());
            if (existingClaim.isPresent() && existingClaim.get().getStatus() == ClaimStatus.COLLECTED) {
                throw new InvalidRequestException("This event is locked. Claim collected.");
            }
        }

        subscription.setPaid(true);
        subscription.setStatus(SubscriptionStatus.PAID);
        subscriptionRepository.save(subscription);

        // Notify Admins of payment
        userRepository.findByRoles_RoleName("ADMIN").forEach(admin -> {
            notificationService.createNotification(admin, 
                "Payment Received: ₹" + subscription.getPremiumAmount() + " for " + subscription.getEvent().getEventName(), 
                "SUCCESS");
        });

        return "Premium paid successfully";
    }

    public List<UnderwriterSubscriptionResponse> getAllSubscriptionsForUnderwriter() {
        List<PolicySubscription> subscriptions = subscriptionRepository.findAll();

        return subscriptions.stream()
                .map(this::convertToUnderwriterDTO)
                .collect(Collectors.toList());
    }

    public List<UnderwriterSubscriptionResponse> getAssignedSubscriptionsForUnderwriter(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<PolicySubscription> subscriptions = subscriptionRepository.findByAssignedUnderwriter(user);

        return subscriptions.stream()
                .map(this::convertToUnderwriterDTO)
                .collect(Collectors.toList());
    }

    public List<AdminSubscriptionResponse> getAllSubscriptionsForAdmin() {
        List<PolicySubscription> subscriptions = subscriptionRepository.findAll();

        return subscriptions.stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
    }

    private AdminSubscriptionResponse convertToAdminDTO(PolicySubscription subscription) {
        AdminSubscriptionResponse dto = new AdminSubscriptionResponse();
        dto.setSubscriptionId(subscription.getSubscriptionId());
        dto.setEventName(subscription.getEvent().getEventName());
        dto.setCustomerName(subscription.getEvent().getUser().getFullName());
        dto.setPolicyName(subscription.getPolicy().getPolicyName());
        dto.setPremiumAmount(subscription.getPremiumAmount());
        dto.setRiskPercentage(subscription.getRiskPercentage());
        dto.setStatus(subscription.getStatus().toString());
        dto.setRequestedAt(subscription.getRequestedAt());
        dto.setAssignedUnderwriterName(subscription.getAssignedUnderwriter() != null ? 
            subscription.getAssignedUnderwriter().getFullName() : "NOT ASSIGNED");
        
        dto.setEventRisk(subscription.getEventRisk());
        dto.setWeatherRisk(subscription.getWeatherRisk());
        String riskLevel = "LOW";
        if (subscription.getTotalRisk() != null) {
            if (subscription.getTotalRisk() > 10) riskLevel = "HIGH";
            else if (subscription.getTotalRisk() > 5) riskLevel = "MEDIUM";
        }
        dto.setRiskLevel(riskLevel);
        
        return dto;
    }

    private UnderwriterSubscriptionResponse convertToUnderwriterDTO(PolicySubscription subscription) {
        UnderwriterSubscriptionResponse dto = new UnderwriterSubscriptionResponse();
        dto.setSubscriptionId(subscription.getSubscriptionId());
        dto.setEventName(subscription.getEvent().getEventName());
        dto.setCustomerName(subscription.getEvent().getUser().getFullName());
        dto.setPolicyName(subscription.getPolicy().getPolicyName());
        dto.setPremiumAmount(subscription.getPremiumAmount());
        dto.setRiskPercentage(subscription.getRiskPercentage());
        dto.setStatus(subscription.getStatus().toString());
        dto.setRejectionReason(subscription.getRejectionReason());
        dto.setAssignedUnderwriterName(subscription.getAssignedUnderwriter() != null ? 
            subscription.getAssignedUnderwriter().getFullName() : null);
        
        dto.setEventRisk(subscription.getEventRisk());
        dto.setWeatherRisk(subscription.getWeatherRisk());
        String riskLevel = "LOW";
        if (subscription.getTotalRisk() != null) {
            if (subscription.getTotalRisk() > 10) riskLevel = "HIGH";
            else if (subscription.getTotalRisk() > 5) riskLevel = "MEDIUM";
        }
        dto.setRiskLevel(riskLevel);
        
        return dto;
    }

    private CustomerSubscriptionResponse convertToCustomerDTO(PolicySubscription subscription) {
        CustomerSubscriptionResponse dto = new CustomerSubscriptionResponse();
        dto.setSubscriptionId(subscription.getSubscriptionId());
        dto.setEventId(subscription.getEvent().getEventId());
        dto.setPolicyId(subscription.getPolicy().getPolicyId());
        dto.setEventName(subscription.getEvent().getEventName());
        dto.setEventDate(subscription.getEvent().getEventDate());
        dto.setPolicyName(subscription.getPolicy().getPolicyName());
        dto.setBaseRate(subscription.getPolicy().getBaseRate());
        dto.setMaxCoverageAmount(subscription.getPolicy().getMaxCoverageAmount());
        dto.setPremiumAmount(subscription.getPremiumAmount());
        dto.setStatus(subscription.getStatus().toString());
        dto.setPaid(subscription.isPaid());
        dto.setRiskPercentage(subscription.getRiskPercentage());
        dto.setEventRisk(subscription.getEventRisk());
        dto.setWeatherRisk(subscription.getWeatherRisk());
        dto.setRejectionReason(subscription.getRejectionReason());
        
        // Check if claim exists
        boolean hasClaim = claimsRepository.existsByPolicySubscription_SubscriptionId(subscription.getSubscriptionId());
        dto.setHasClaim(hasClaim);

        // Check if locked
        boolean isLocked = false;
        List<PolicySubscription> eventSubs = subscriptionRepository.findByEvent_EventId(subscription.getEvent().getEventId());
        for (PolicySubscription s : eventSubs) {
            java.util.Optional<Claim> existingClaim = claimsRepository.findByPolicySubscription_SubscriptionId(s.getSubscriptionId());
            if (existingClaim.isPresent() && existingClaim.get().getStatus() == ClaimStatus.COLLECTED) {
                isLocked = true;
                break;
            }
        }
        dto.setIsLocked(isLocked);

        String riskLevel = "LOW";
        if (subscription.getTotalRisk() != null) {
            if (subscription.getTotalRisk() > 10) riskLevel = "HIGH";
            else if (subscription.getTotalRisk() > 5) riskLevel = "MEDIUM";
        }
        dto.setRiskLevel(riskLevel);
        
        return dto;
    }

    private SubscriptionResponseDTO convertToDTO(PolicySubscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setSubscriptionId(subscription.getSubscriptionId());
        dto.setStatus(subscription.getStatus().toString());
        dto.setPremiumAmount(subscription.getPremiumAmount());
        return dto;
    }

    public UnderwriterSubscriptionDetailsResponse getSubscriptionDetails(Long subscriptionId) {
        PolicySubscription subscription = subscriptionRepository.findSubscriptionWithDetails(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        Event event = subscription.getEvent();
        Policy policy = subscription.getPolicy();
        User customer = event.getUser();

        UnderwriterSubscriptionDetailsResponse dto = new UnderwriterSubscriptionDetailsResponse();
        dto.setSubscriptionId(subscription.getSubscriptionId());
        dto.setEventName(event.getEventName());
        dto.setEventType(event.getEventType() != null ? event.getEventType().name() : "UNKNOWN");
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setNumberOfAttendees(event.getNumberOfAttendees());
        dto.setBudget(event.getBudget());
        dto.setDurationInDays(event.getDurationInDays());
        dto.setCustomerName(customer.getFullName());
        dto.setCustomerPhone(customer.getPhone());
        dto.setPolicyName(policy.getPolicyName());
        dto.setPolicyDescription(policy.getDescription());
        dto.setBaseRate(policy.getBaseRate());
        dto.setMaxCoverageAmount(policy.getMaxCoverageAmount());
        dto.setRiskPercentage(subscription.getRiskPercentage());
        dto.setStatus(subscription.getStatus().toString());
        dto.setRejectionReason(subscription.getRejectionReason());
        dto.setPremiumAmount(subscription.getPremiumAmount());
        
        // Populate from RiskDetails entity if available
        if (subscription.getRiskDetails() != null) {
            Risk risk = subscription.getRiskDetails();
            dto.setRiskLevel(risk.getRiskLevel());
            dto.setRiskFactors(risk.getRiskFactors());
            dto.setEventRisk(risk.getEventRiskScore());
            dto.setWeatherRisk(risk.getWeatherRiskScore());
            dto.setTotalRisk(risk.getTotalRiskScore());
        } else {
            // Fallback to subscription fields
            dto.setEventRisk(subscription.getEventRisk());
            dto.setWeatherRisk(subscription.getWeatherRisk());
            dto.setTotalRisk(subscription.getTotalRisk());
            
            String riskLevel = "LOW";
            if (subscription.getTotalRisk() != null) {
                if (subscription.getTotalRisk() > 10) riskLevel = "HIGH";
                else if (subscription.getTotalRisk() > 5) riskLevel = "MEDIUM";
            }
            dto.setRiskLevel(riskLevel);
        }

        dto.setTemperature(subscription.getTemperature());
        dto.setWindSpeed(subscription.getWindSpeed());
        dto.setHumidity(subscription.getHumidity());
        dto.setWeatherCondition(subscription.getWeatherCondition());
        dto.setAssignedUnderwriterName(subscription.getAssignedUnderwriter() != null ? 
            subscription.getAssignedUnderwriter().getFullName() : null);
        
        dto.setHasProfessionalSecurity(event.getHasProfessionalSecurity());
        dto.setHasCCTV(event.getHasCCTV());
        dto.setHasMetalDetectors(event.getHasMetalDetectors());
        dto.setHasFireNOC(event.getHasFireNOC());
        dto.setHasOnSiteFireSafety(event.getHasOnSiteFireSafety());
        dto.setSafetyComplianceDocPath(event.getSafetyComplianceDocPath());

        return dto;
    }

    private String getWeatherCondition(WeatherRiskResponse weatherData) {
        if (weatherData.getRainProbability() > 60) return "Thunderstorm";
        if (weatherData.getRainProbability() > 40) return "Rainy";
        if (weatherData.getWindSpeed() > 50) return "Windy";
        if (weatherData.getTemperature() > 35) return "Hot";
        if (weatherData.getTemperature() < 10) return "Cold";
        return "Clear";
    }
}
