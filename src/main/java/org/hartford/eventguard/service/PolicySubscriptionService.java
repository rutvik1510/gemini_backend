package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.*;
import org.hartford.eventguard.entity.*;
import org.hartford.eventguard.exception.InvalidRequestException;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.exception.UnauthorizedAccessException;
import org.hartford.eventguard.repo.EventRepository;
import org.hartford.eventguard.repo.PolicyRepository;
import org.hartford.eventguard.repo.PolicySubscriptionRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.hartford.eventguard.service.RiskCalculationService;
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

    public PolicySubscriptionService(PolicySubscriptionRepository subscriptionRepository,
                                     EventRepository eventRepository,
                                     PolicyRepository policyRepository,
                                     UserRepository userRepository,
                                     RiskCalculationService riskCalculationService) {
        this.subscriptionRepository = subscriptionRepository;
        this.eventRepository = eventRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.riskCalculationService = riskCalculationService;
    }

    public CustomerSubscriptionResponse createSubscription(Long eventId, Long policyId, String email) {
        // Fetch User using email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch Event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

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
            throw new InvalidRequestException(
                    "This policy is already subscribed for the event"
            );
        }

        // Calculate detailed risk breakdown
        DetailedRiskBreakdown riskBreakdown = riskCalculationService.calculateRiskWithBreakdown(event);
        double totalRisk = riskBreakdown.getEventRisk() + riskBreakdown.getWeatherRisk();

        // Calculate premium amount: Budget × (Base Rate + Risk) / 100
        double premiumAmount = event.getBudget() * (policy.getBaseRate() + totalRisk) / 100;

        // Log premium calculation breakdown
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          PREMIUM CALCULATION BREAKDOWN                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Event: " + event.getEventName());
        System.out.println("Policy: " + policy.getPolicyName());
        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("Event Budget:        ₹" + String.format("%,.2f", event.getBudget()));
        System.out.println("Policy Base Rate:    " + String.format("%.2f", policy.getBaseRate()) + "%");
        System.out.println("Event Risk:          " + String.format("%.2f", riskBreakdown.getEventRisk()) + "%");
        System.out.println("Weather Risk:        " + String.format("%.2f", riskBreakdown.getWeatherRisk()) + "%");
        System.out.println("Total Risk:          " + String.format("%.2f", totalRisk) + "%");
        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("Combined Rate:       " + String.format("%.2f", policy.getBaseRate() + totalRisk) + "% (Base + Risk)");
        System.out.println("Formula:             ₹" + String.format("%,.2f", event.getBudget()) + " × " + String.format("%.2f", policy.getBaseRate() + totalRisk) + "% / 100");
        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("💰 FINAL PREMIUM:    ₹" + String.format("%,.2f", premiumAmount));
        System.out.println("════════════════════════════════════════════════════════════\n");

        // Create new PolicySubscription object
        PolicySubscription subscription = new PolicySubscription();
        subscription.setEvent(event);
        subscription.setPolicy(policy);

        // Store risk breakdown
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

        // Convert to customer DTO
        return convertToCustomerDTO(savedSubscription);
    }

    public List<CustomerSubscriptionResponse> getCustomerSubscriptions(String email) {
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

    public SubscriptionResponseDTO approveSubscription(Long id, String email) {
        // Fetch subscription
        PolicySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update subscription
        subscription.setStatus(SubscriptionStatus.APPROVED);
        subscription.setApprovedAt(LocalDateTime.now());
        subscription.setApprovedBy(user);

        subscriptionRepository.save(subscription);

        // Convert to DTO and return
        return convertToDTO(subscription);
    }

    public SubscriptionResponseDTO rejectSubscription(Long id, String email) {
        // Fetch subscription
        PolicySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update subscription
        subscription.setStatus(SubscriptionStatus.REJECTED);
        subscription.setApprovedAt(LocalDateTime.now());
        subscription.setApprovedBy(user);

        subscriptionRepository.save(subscription);

        // Convert to DTO and return
        return convertToDTO(subscription);
    }

    public QuoteResponse calculateQuote(Long eventId, Long policyId) {
        // Fetch Event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Fetch Policy
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        // Calculate risk percentage with detailed breakdown
        double totalRisk = riskCalculationService.calculateRisk(event);

        // Get detailed risk breakdown
        DetailedRiskBreakdown breakdown = riskCalculationService.calculateRiskWithBreakdown(event);

        // Calculate estimated premium: Budget × (Base Rate + Risk) / 100
        double estimatedPremium = event.getBudget() * (policy.getBaseRate() + totalRisk) / 100;

        // Log quote calculation breakdown
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          QUOTE CALCULATION (ESTIMATE)                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Event: " + event.getEventName());
        System.out.println("Policy: " + policy.getPolicyName());
        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("Event Budget:        ₹" + String.format("%,.2f", event.getBudget()));
        System.out.println("Policy Base Rate:    " + String.format("%.2f", policy.getBaseRate()) + "%");
        System.out.println("Event Risk:          " + String.format("%.2f", breakdown.getEventRisk()) + "%");
        System.out.println("Weather Risk:        " + String.format("%.2f", breakdown.getWeatherRisk()) + "%");
        System.out.println("Total Risk:          " + String.format("%.2f", totalRisk) + "%");
        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("Combined Rate:       " + String.format("%.2f", policy.getBaseRate() + totalRisk) + "% (Base + Risk)");
        System.out.println("Formula:             ₹" + String.format("%,.2f", event.getBudget()) + " × " + String.format("%.2f", policy.getBaseRate() + totalRisk) + "% / 100");
        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("💰 ESTIMATED PREMIUM: ₹" + String.format("%,.2f", estimatedPremium));
        System.out.println("════════════════════════════════════════════════════════════\n");

        // Create and return QuoteResponse with detailed information
        QuoteResponse response = new QuoteResponse();
        response.setEventId(eventId);
        response.setPolicyId(policyId);
        response.setBaseRate(policy.getBaseRate());
        response.setRiskPercentage(totalRisk);
        response.setEstimatedPremium(estimatedPremium);
        response.setEventRisk(breakdown.getEventRisk());
        response.setWeatherRisk(breakdown.getWeatherRisk());
        response.setWeatherData(breakdown.getWeatherData());
        response.setVenueType(event.getVenueType() != null ? event.getVenueType().toString() : "UNKNOWN");

        return response;
    }

    public CustomerSubscriptionResponse calculateQuoteForCustomer(Long eventId, Long policyId) {
        // Fetch Event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Fetch Policy
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        // Calculate risk percentage
        double totalRisk = riskCalculationService.calculateRisk(event);

        // Calculate estimated premium: Budget × (Base Rate + Risk) / 100
        double estimatedPremium = event.getBudget() * (policy.getBaseRate() + totalRisk) / 100;

        // Log customer quote calculation
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          CUSTOMER QUOTE CALCULATION                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Event: " + event.getEventName());
        System.out.println("Policy: " + policy.getPolicyName());
        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("Budget:              ₹" + String.format("%,.2f", event.getBudget()));
        System.out.println("Base Rate:           " + String.format("%.2f", policy.getBaseRate()) + "%");
        System.out.println("Total Risk:          " + String.format("%.2f", totalRisk) + "%");
        System.out.println("Combined Rate:       " + String.format("%.2f", policy.getBaseRate() + totalRisk) + "%");
        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("💰 ESTIMATED PREMIUM: ₹" + String.format("%,.2f", estimatedPremium));
        System.out.println("════════════════════════════════════════════════════════════\n");

        // Create customer response
        CustomerSubscriptionResponse response = new CustomerSubscriptionResponse();
        response.setSubscriptionId(null);  // This is just a quote, not saved yet
        response.setEventName(event.getEventName());
        response.setPolicyName(policy.getPolicyName());
        response.setRiskLevel(getRiskLevel(totalRisk));
        response.setPremiumAmount(estimatedPremium);
        response.setStatus("QUOTE");

        return response;
    }

    private SubscriptionResponseDTO convertToDTO(PolicySubscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setSubscriptionId(subscription.getSubscriptionId());
        dto.setEventId(subscription.getEvent().getEventId());
        dto.setPolicyId(subscription.getPolicy().getPolicyId());
        dto.setRiskPercentage(subscription.getRiskPercentage());
        dto.setPremiumAmount(subscription.getPremiumAmount());
        dto.setStatus(subscription.getStatus().toString());
        dto.setApprovedBy(subscription.getApprovedBy() != null ? subscription.getApprovedBy().getFullName() : null);
        dto.setRequestedAt(subscription.getRequestedAt());
        dto.setApprovedAt(subscription.getApprovedAt());
        return dto;
    }

    // Convert to Customer Subscription Response
    public List<CustomerSubscriptionResponse> getCustomerSubscriptionsDTO(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<PolicySubscription> subscriptions = subscriptionRepository.findByEvent_User(user);

        return subscriptions.stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

    // Convert to Underwriter Subscription Response
    public List<UnderwriterSubscriptionResponse> getAllSubscriptionsForUnderwriter() {
        List<PolicySubscription> subscriptions = subscriptionRepository.findAll();

        return subscriptions.stream()
                .map(this::convertToUnderwriterDTO)
                .collect(Collectors.toList());
    }

    // Admin method to get all subscriptions
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
        dto.setEventRisk(subscription.getEventRisk());
        dto.setWeatherRisk(subscription.getWeatherRisk());
        dto.setRiskLevel(getRiskLevel(subscription.getRiskPercentage()));
        dto.setRiskPercentage(subscription.getRiskPercentage());
        dto.setPremiumAmount(subscription.getPremiumAmount());
        dto.setStatus(subscription.getStatus().toString());
        dto.setRequestedAt(subscription.getRequestedAt());
        return dto;
    }

    private UnderwriterSubscriptionResponse convertToUnderwriterDTO(PolicySubscription subscription) {
        UnderwriterSubscriptionResponse dto = new UnderwriterSubscriptionResponse();

        Event event = subscription.getEvent();
        Policy policy = subscription.getPolicy();
        User customer = event.getUser();

        // Subscription details
        dto.setSubscriptionId(subscription.getSubscriptionId());

        // Event details
        dto.setEventName(event.getEventName());
        dto.setEventType(event.getEventType() != null ? event.getEventType().name() : "UNKNOWN");
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setNumberOfAttendees(event.getNumberOfAttendees());
        dto.setBudget(event.getBudget());

        // Customer details
        dto.setCustomerName(customer.getFullName());

        // Policy details
        dto.setPolicyName(policy.getPolicyName());
        dto.setPolicyDescription(policy.getDescription());
        dto.setBaseRate(policy.getBaseRate());
        dto.setMaxCoverageAmount(policy.getMaxCoverageAmount());

        // Risk breakdown - use stored values from subscription
        dto.setEventRisk(subscription.getEventRisk() != null ? subscription.getEventRisk() : 0.0);
        dto.setWeatherRisk(subscription.getWeatherRisk() != null ? subscription.getWeatherRisk() : 0.0);
        dto.setTotalRisk(subscription.getTotalRisk() != null ? subscription.getTotalRisk() : subscription.getRiskPercentage());

        // Calculate risk level
        double totalRisk = dto.getTotalRisk();
        if (totalRisk < 5) {
            dto.setRiskLevel("LOW");
        } else if (totalRisk < 10) {
            dto.setRiskLevel("MEDIUM");
        } else {
            dto.setRiskLevel("HIGH");
        }

        // Weather details - use stored values from subscription
        dto.setWeatherCondition(subscription.getWeatherCondition());
        dto.setTemperature(subscription.getTemperature());
        dto.setWindSpeed(subscription.getWindSpeed());
        dto.setHumidity(subscription.getHumidity());

        // Set weather details object if needed
        if (subscription.getWeatherCondition() != null) {
            WeatherDetails weatherDetails = new WeatherDetails();
            weatherDetails.setLocation(event.getLocation());
            weatherDetails.setTemperature(subscription.getTemperature());
            weatherDetails.setWindSpeed(subscription.getWindSpeed());
            weatherDetails.setRainProbability(subscription.getHumidity() != null ? subscription.getHumidity() / 0.6 : 0.0);
            weatherDetails.setWeatherCondition(subscription.getWeatherCondition());
            dto.setWeatherDetails(weatherDetails);
        }

        // Premium and status
        dto.setPremiumAmount(subscription.getPremiumAmount());
        dto.setStatus(subscription.getStatus().toString());

        return dto;
    }

    // Get detailed subscription for underwriter review
    public UnderwriterSubscriptionDetailsResponse getSubscriptionDetails(Long subscriptionId) {
        // Fetch PolicySubscription with related entities
        PolicySubscription subscription = subscriptionRepository.findSubscriptionWithDetails(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // Get Event, Policy, and Customer
        Event event = subscription.getEvent();
        Policy policy = subscription.getPolicy();
        User customer = event.getUser();

        // Create response DTO
        UnderwriterSubscriptionDetailsResponse dto = new UnderwriterSubscriptionDetailsResponse();

        // Subscription ID
        dto.setSubscriptionId(subscription.getSubscriptionId());

        // Event details
        dto.setEventName(event.getEventName());
        dto.setEventType(event.getEventType() != null ? event.getEventType().name() : "UNKNOWN");
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setVenueType(event.getVenueType() != null ? event.getVenueType().toString() : "UNKNOWN");
        dto.setNumberOfAttendees(event.getNumberOfAttendees());
        dto.setAttendees(event.getNumberOfAttendees());
        dto.setBudget(event.getBudget());
        dto.setDuration(event.getDurationInDays());
        dto.setDurationInDays(event.getDurationInDays());

        // Customer
        dto.setCustomerName(customer.getFullName());

        // Policy details
        dto.setPolicyName(policy.getPolicyName());
        dto.setPolicyDescription(policy.getDescription());
        dto.setBaseRate(policy.getBaseRate());
        dto.setMaxCoverageAmount(policy.getMaxCoverageAmount());

        // Risk breakdown - use stored values from subscription if available
        if (subscription.getEventRisk() != null) {
            dto.setEventRisk(subscription.getEventRisk());
            dto.setWeatherRisk(subscription.getWeatherRisk());
            dto.setTotalRisk(subscription.getTotalRisk());
        } else {
            // Fallback to stored riskPercentage
            dto.setEventRisk(subscription.getRiskPercentage());
            dto.setWeatherRisk(0.0);
            dto.setTotalRisk(subscription.getRiskPercentage());
        }

        // Calculate risk level
        Double totalRisk = dto.getTotalRisk();
        if (totalRisk != null) {
            if (totalRisk < 5) {
                dto.setRiskLevel("LOW");
            } else if (totalRisk < 10) {
                dto.setRiskLevel("MEDIUM");
            } else {
                dto.setRiskLevel("HIGH");
            }
        }

        // Premium
        dto.setPremiumAmount(subscription.getPremiumAmount());

        // Status
        dto.setStatus(subscription.getStatus().name());

        // Risk inputs (optional fields)
        dto.setIsOutdoor(event.getIsOutdoor());
        dto.setAlcoholAllowed(event.getAlcoholAllowed());
        dto.setFireworksUsed(event.getFireworksUsed());
        dto.setCelebrityInvolved(event.getCelebrityInvolved());
        dto.setTemporaryStructure(event.getTemporaryStructure());
        dto.setLocationRiskLevel(event.getLocationRiskLevel());
        dto.setSecurityLevel(event.getSecurityLevel());

        // Weather details - use stored values from subscription if available
        if (subscription.getWeatherCondition() != null) {
            dto.setTemperature(subscription.getTemperature());
            dto.setWindSpeed(subscription.getWindSpeed());
            dto.setHumidity(subscription.getHumidity());
            dto.setWeatherCondition(subscription.getWeatherCondition());
        }

        return dto;
    }

    private String getRiskLevel(double riskPercentage) {
        if (riskPercentage < 5) {
            return "LOW";
        } else if (riskPercentage < 10) {
            return "MEDIUM";
        } else if (riskPercentage < 15) {
            return "HIGH";
        } else {
            return "VERY_HIGH";
        }
    }

    private String getWeatherCondition(WeatherRiskResponse weatherData) {
        if (weatherData.getRainProbability() > 60) {
            return "Thunderstorm";
        } else if (weatherData.getRainProbability() > 40) {
            return "Rainy";
        } else if (weatherData.getWindSpeed() > 50) {
            return "Windy";
        } else if (weatherData.getTemperature() > 35) {
            return "Hot";
        } else if (weatherData.getTemperature() < 10) {
            return "Cold";
        } else {
            return "Clear";
        }
    }
}
