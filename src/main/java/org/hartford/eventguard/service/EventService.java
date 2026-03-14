package org.hartford.eventguard.service;



import org.hartford.eventguard.dto.CorporateEventRequest;
import org.hartford.eventguard.dto.EventRequest;
import org.hartford.eventguard.dto.EventResponse;
import org.hartford.eventguard.dto.MusicEventRequest;
import org.hartford.eventguard.entity.*;
import org.hartford.eventguard.exception.InvalidRequestException;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.repo.ClaimsRepository;
import org.hartford.eventguard.repo.EventRepository;
import org.hartford.eventguard.repo.PolicySubscriptionRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PolicySubscriptionRepository subscriptionRepository;
    private final ClaimsRepository claimsRepository;
    private final NotificationService notificationService;

    public EventService(EventRepository eventRepository, 
                        UserRepository userRepository, 
                        PolicySubscriptionRepository subscriptionRepository, 
                        ClaimsRepository claimsRepository,
                        NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.claimsRepository = claimsRepository;
        this.notificationService = notificationService;
    }

    public EventResponse createCorporateEvent(CorporateEventRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate event date is not in the past
        if (request.getEventDate().isBefore(java.time.LocalDate.now())) {
            throw new InvalidRequestException("Event date cannot be in the past");
        }

        // Validate corporate-specific required fields
        validateCorporateEventRequest(request);

        Event event = new Event();

        // Common fields
        event.setEventName(request.getEventName());
        event.setEventType(EventDomain.CORPORATE_TECH_CONFERENCE);
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setBudget(request.getBudget());
        event.setNumberOfAttendees(request.getNumberOfAttendees());
        event.setDurationInDays(request.getDurationInDays());

        // Objective fields
        event.setHasProfessionalSecurity(request.getHasProfessionalSecurity());
        event.setHasCCTV(request.getHasCCTV());
        event.setHasMetalDetectors(request.getHasMetalDetectors());
        event.setHasFireNOC(request.getHasFireNOC());
        event.setHasOnSiteFireSafety(request.getHasOnSiteFireSafety());
        event.setSafetyComplianceDocPath(request.getSafetyComplianceDocPath());

        // Calculate Risk Levels
        event.setSecurityLevel(calculateSecurityLevel(request));
        event.setLocationRiskLevel(calculateLocationRiskLevel(request));

        // Corporate-specific fields
        event.setVenueType(request.getVenueType());
        event.setTemporaryBooths(request.getTemporaryBooths());
        event.setHighValueEquipment(request.getHighValueEquipment());
        event.setEmergencyPreparednessLevel(request.getEmergencyPreparednessLevel());

        // Set music-specific fields to null (not applicable for corporate)
        event.setIsOutdoor(null);
        event.setAlcoholAllowed(null);
        event.setTemporaryStructure(null);
        event.setTemporaryStage(null);
        event.setFireworksUsed(null);
        event.setCelebrityInvolved(null);

        event.setUser(user);

        Event savedEvent = eventRepository.save(event);

        // Notify Admins
        userRepository.findByRoles_RoleName("ADMIN").forEach(admin -> {
            notificationService.createNotification(admin, 
                "New Corporate Event created: " + savedEvent.getEventName() + " by " + user.getFullName(), 
                "INFO");
        });

        return convertToDTO(savedEvent);
    }

    public EventResponse createMusicEvent(MusicEventRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate event date is not in the past
        if (request.getEventDate().isBefore(java.time.LocalDate.now())) {
            throw new InvalidRequestException("Event date cannot be in the past");
        }

        // Validate music-specific required fields
        validateMusicEventRequest(request);

        Event event = new Event();

        // Common fields
        event.setEventName(request.getEventName());
        event.setEventType(EventDomain.OUTDOOR_MUSIC_CONCERT);
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setBudget(request.getBudget());
        event.setNumberOfAttendees(request.getNumberOfAttendees());
        event.setDurationInDays(request.getDurationInDays());

        // Objective fields
        event.setHasProfessionalSecurity(request.getHasProfessionalSecurity());
        event.setHasCCTV(request.getHasCCTV());
        event.setHasMetalDetectors(request.getHasMetalDetectors());
        event.setHasFireNOC(request.getHasFireNOC());
        event.setHasOnSiteFireSafety(request.getHasOnSiteFireSafety());
        event.setSafetyComplianceDocPath(request.getSafetyComplianceDocPath());

        // Calculate Risk Levels
        event.setSecurityLevel(calculateSecurityLevel(request));
        event.setLocationRiskLevel(calculateLocationRiskLevel(request));

        // Music-specific fields
        event.setIsOutdoor(request.getIsOutdoor());
        event.setAlcoholAllowed(request.getAlcoholAllowed());
        event.setTemporaryStructure(request.getTemporaryStructure());
        event.setTemporaryStage(request.getTemporaryStage());
        event.setFireworksUsed(request.getFireworksUsed());
        event.setCelebrityInvolved(request.getCelebrityInvolved());

        // Set corporate-specific fields to null (not applicable for music)
        event.setTemporaryBooths(null);
        event.setHighValueEquipment(null);
        event.setEmergencyPreparednessLevel(null);
        event.setVenueType(null);

        event.setUser(user);

        Event savedEvent = eventRepository.save(event);

        // Notify Admins
        userRepository.findByRoles_RoleName("ADMIN").forEach(admin -> {
            notificationService.createNotification(admin, 
                "New Music Event created: " + savedEvent.getEventName() + " by " + user.getFullName(), 
                "INFO");
        });

        return convertToDTO(savedEvent);
    }

    private void validateCorporateEventRequest(CorporateEventRequest request) {
        if (request.getVenueType() == null) {
            throw new IllegalArgumentException("venueType is required for Corporate Conference events");
        }
        if (request.getHighValueEquipment() == null) {
            throw new IllegalArgumentException("highValueEquipment is required for Corporate Conference events");
        }
        if (request.getEmergencyPreparednessLevel() == null || request.getEmergencyPreparednessLevel().isEmpty()) {
            throw new IllegalArgumentException("emergencyPreparednessLevel is required for Corporate Conference events");
        }
    }

    private void validateMusicEventRequest(MusicEventRequest request) {
        if (request.getIsOutdoor() == null) {
            throw new IllegalArgumentException("isOutdoor is required for Music Concert events");
        }
        if (request.getTemporaryStage() == null) {
            throw new IllegalArgumentException("temporaryStage is required for Music Concert events");
        }
        if (request.getFireworksUsed() == null) {
            throw new IllegalArgumentException("fireworksUsed is required for Music Concert events");
        }
        if (request.getCelebrityInvolved() == null) {
            throw new IllegalArgumentException("celebrityInvolved is required for Music Concert events");
        }
    }

    public List<EventResponse> getMyEventsDTO(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Event> events = eventRepository.findByUser(user);

        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EventResponse getEventByIdDTO(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return convertToDTO(event);
    }

    public String deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        eventRepository.delete(event);
        return "Event deleted successfully";
    }

    private EventResponse convertToDTO(Event event) {
        EventResponse dto = new EventResponse();
        dto.setEventId(event.getEventId());
        dto.setEventName(event.getEventName());
        dto.setEventType(event.getEventType());
        dto.setLocation(event.getLocation());
        dto.setEventDate(event.getEventDate());
        dto.setBudget(event.getBudget());
        dto.setNumberOfAttendees(event.getNumberOfAttendees());
        dto.setDurationInDays(event.getDurationInDays());

        // Add insurance status info
        List<PolicySubscription> subscriptions = subscriptionRepository.findByEvent_EventId(event.getEventId());
        if (!subscriptions.isEmpty()) {
            PolicySubscription sub = subscriptions.get(0);
            dto.setSubscriptionId(sub.getSubscriptionId());
            dto.setStatus(sub.getStatus().toString());
            dto.setIsPremiumPaid(sub.getStatus() == SubscriptionStatus.PAID);
            dto.setPremiumAmount(sub.getPremiumAmount());

            // Check for claims
            Optional<Claim> claim = claimsRepository.findByPolicySubscription_SubscriptionId(sub.getSubscriptionId());
            if (claim.isPresent()) {
                dto.setHasClaim(true);
                dto.setClaimStatus(claim.get().getStatus().toString());
            } else {
                dto.setHasClaim(false);
            }
        } else {
            dto.setStatus("NO_SUBSCRIPTION");
            dto.setIsPremiumPaid(false);
            dto.setHasClaim(false);
        }

        return dto;
    }

    // Admin method to get all events
    public List<EventResponse> getAllEventsForAdmin() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EventResponse convertToResponse(Event event) {
        return convertToDTO(event);
    }

    private String calculateSecurityLevel(EventRequest request) {
        return calculateSecurity(request.getHasProfessionalSecurity(), request.getHasCCTV(), request.getHasMetalDetectors());
    }

    private String calculateSecurityLevel(CorporateEventRequest request) {
        return calculateSecurity(request.getHasProfessionalSecurity(), request.getHasCCTV(), request.getHasMetalDetectors());
    }

    private String calculateSecurityLevel(MusicEventRequest request) {
        return calculateSecurity(request.getHasProfessionalSecurity(), request.getHasCCTV(), request.getHasMetalDetectors());
    }

    private String calculateSecurity(Boolean hasSecurity, Boolean hasCCTV, Boolean hasDetectors) {
        int score = 0;
        if (Boolean.TRUE.equals(hasSecurity)) score += 2;
        if (Boolean.TRUE.equals(hasCCTV)) score += 1;
        if (Boolean.TRUE.equals(hasDetectors)) score += 1;

        if (score >= 3) return "HIGH";
        if (score >= 1) return "MEDIUM";
        return "LOW";
    }

    private String calculateLocationRiskLevel(EventRequest request) {
        return calculateLocationRisk(request.getHasFireNOC(), request.getHasOnSiteFireSafety());
    }

    private String calculateLocationRiskLevel(CorporateEventRequest request) {
        return calculateLocationRisk(request.getHasFireNOC(), request.getHasOnSiteFireSafety());
    }

    private String calculateLocationRiskLevel(MusicEventRequest request) {
        return calculateLocationRisk(request.getHasFireNOC(), request.getHasOnSiteFireSafety());
    }

    private String calculateLocationRisk(Boolean hasFireNOC, Boolean hasFireSafety) {
        int safetyScore = 0;
        if (Boolean.TRUE.equals(hasFireNOC)) safetyScore += 2;
        if (Boolean.TRUE.equals(hasFireSafety)) safetyScore += 1;

        if (safetyScore >= 3) return "LOW";
        if (safetyScore >= 1) return "MEDIUM";
        return "HIGH";
    }
}

