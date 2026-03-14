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

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        PolicySubscriptionRepository subscriptionRepository,
                        ClaimsRepository claimsRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.claimsRepository = claimsRepository;
    }

    public EventResponse createMusicEvent(MusicEventRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setBudget(request.getBudget());
        event.setNumberOfAttendees(request.getNumberOfAttendees());
        event.setDurationInDays(request.getDurationInDays());
        event.setEventType(EventDomain.OUTDOOR_MUSIC_CONCERT);
        event.setUser(user);

        // Security
        event.setHasProfessionalSecurity(request.getHasProfessionalSecurity());
        event.setHasCCTV(request.getHasCCTV());
        event.setHasMetalDetectors(request.getHasMetalDetectors());
        event.setSecurityLevel(calculateSecurityLevel(request));

        // Safety
        event.setHasFireNOC(request.getHasFireNOC());
        event.setHasOnSiteFireSafety(request.getHasOnSiteFireSafety());
        event.setLocationRiskLevel(calculateLocationRiskLevel(request));
        event.setSafetyComplianceDocPath(request.getSafetyComplianceDocPath());

        // Music Specific
        event.setIsOutdoor(request.getIsOutdoor());
        event.setAlcoholAllowed(request.getAlcoholAllowed());
        event.setTemporaryStage(request.getTemporaryStage());
        event.setFireworksUsed(request.getFireworksUsed());
        event.setCelebrityInvolved(request.getCelebrityInvolved());

        Event saved = eventRepository.save(event);
        return convertToDTO(saved);
    }

    public EventResponse createCorporateEvent(CorporateEventRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setBudget(request.getBudget());
        event.setNumberOfAttendees(request.getNumberOfAttendees());
        event.setDurationInDays(request.getDurationInDays());
        event.setEventType(EventDomain.CORPORATE_TECH_CONFERENCE);
        event.setUser(user);

        // Security
        event.setHasProfessionalSecurity(request.getHasProfessionalSecurity());
        event.setHasCCTV(request.getHasCCTV());
        event.setHasMetalDetectors(request.getHasMetalDetectors());
        event.setSecurityLevel(calculateSecurityLevel(request));

        // Safety
        event.setHasFireNOC(request.getHasFireNOC());
        event.setHasOnSiteFireSafety(request.getHasOnSiteFireSafety());
        event.setLocationRiskLevel(calculateLocationRiskLevel(request));
        event.setSafetyComplianceDocPath(request.getSafetyComplianceDocPath());

        // Corporate Specific
        event.setVenueType(request.getVenueType());
        event.setHighValueEquipment(request.getHighValueEquipment());
        event.setTemporaryBooths(request.getTemporaryBooths());
        event.setEmergencyPreparednessLevel(request.getEmergencyPreparednessLevel());

        Event saved = eventRepository.save(event);
        return convertToDTO(saved);
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
        
        // Check if there are active subscriptions
        List<PolicySubscription> subs = subscriptionRepository.findByEvent_EventId(id);
        if (!subs.isEmpty()) {
            throw new InvalidRequestException("Cannot delete event with active subscriptions");
        }

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
            // Find the active subscription (Paid, Approved, or the first one)
            PolicySubscription sub = subscriptions.stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.PAID)
                .findFirst()
                .or(() -> subscriptions.stream()
                        .filter(s -> s.getStatus() == SubscriptionStatus.APPROVED)
                        .findFirst())
                .orElse(subscriptions.get(0));

            dto.setSubscriptionId(sub.getSubscriptionId());
            dto.setStatus(sub.getStatus().toString());
            dto.setIsPremiumPaid(sub.getStatus() == SubscriptionStatus.PAID);
            dto.setPremiumAmount(sub.getPremiumAmount());

            // Check for claims across ALL subscriptions for this event
            boolean anyClaim = false;
            boolean eventLocked = false;
            String topClaimStatus = "NONE";

            for (PolicySubscription s : subscriptions) {
                Optional<Claim> c = claimsRepository.findByPolicySubscription_SubscriptionId(s.getSubscriptionId());
                if (c.isPresent()) {
                    anyClaim = true;
                    topClaimStatus = c.get().getStatus().toString();
                    if (c.get().getStatus() == ClaimStatus.COLLECTED) {
                        eventLocked = true;
                        break;
                    }
                }
            }

            dto.setHasClaim(anyClaim);
            dto.setClaimStatus(topClaimStatus);
            dto.setIsLocked(eventLocked);
        } else {
            dto.setHasClaim(false);
            dto.setIsLocked(false);
            dto.setClaimStatus("NONE");
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
