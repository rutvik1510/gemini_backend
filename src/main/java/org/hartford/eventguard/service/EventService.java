package org.hartford.eventguard.service;



import org.hartford.eventguard.dto.CorporateEventRequest;
import org.hartford.eventguard.dto.EventRequest;
import org.hartford.eventguard.dto.EventResponse;
import org.hartford.eventguard.dto.MusicEventRequest;
import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.EventDomain;
import org.hartford.eventguard.entity.User;
import org.hartford.eventguard.exception.InvalidRequestException;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.repo.EventRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public EventResponse createEvent(EventRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate event type specific fields
        validateEventRequest(request);

        Event event = new Event();

        // Common fields
        event.setEventName(request.getEventName());
        event.setEventType(request.getEventType());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setBudget(request.getBudget());
        event.setNumberOfAttendees(request.getNumberOfAttendees());
        event.setDurationInDays(request.getDurationInDays());
        event.setVenueType(request.getVenueType());
        event.setLocationRiskLevel(request.getLocationRiskLevel());
        event.setSecurityLevel(request.getSecurityLevel());

        // Music concert specific fields
        event.setIsOutdoor(request.getIsOutdoor());
        event.setAlcoholAllowed(request.getAlcoholAllowed());
        event.setTemporaryStructure(request.getTemporaryStructure());
        event.setTemporaryStage(request.getTemporaryStage());
        event.setFireworksUsed(request.getFireworksUsed());
        event.setCelebrityInvolved(request.getCelebrityInvolved());

        // Conference specific fields
        event.setTemporaryBooths(request.getTemporaryBooths());
        event.setHighValueEquipment(request.getHighValueEquipment());
        event.setEmergencyPreparednessLevel(request.getEmergencyPreparednessLevel());

        event.setUser(user);

        Event savedEvent = eventRepository.save(event);

        // Convert to DTO and return
        return convertToDTO(savedEvent);
    }

    private void validateEventRequest(EventRequest request) {
        if (request.getEventType() == null) {
            throw new IllegalArgumentException("Event type is required");
        }

        // Validation for OUTDOOR_MUSIC_CONCERT
        if (request.getEventType().name().equals("OUTDOOR_MUSIC_CONCERT")) {
            if (request.getIsOutdoor() == null) {
                throw new IllegalArgumentException("isOutdoor is required for Music Concert events");
            }
            if (request.getFireworksUsed() == null) {
                throw new IllegalArgumentException("fireworksUsed is required for Music Concert events");
            }
            if (request.getCelebrityInvolved() == null) {
                throw new IllegalArgumentException("celebrityInvolved is required for Music Concert events");
            }
            if (request.getTemporaryStage() == null) {
                throw new IllegalArgumentException("temporaryStage is required for Music Concert events");
            }
        }

        // Validation for CORPORATE_TECH_CONFERENCE
        if (request.getEventType().name().equals("CORPORATE_TECH_CONFERENCE")) {
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
    }

    public List<Event> getMyEvents(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return eventRepository.findByUser(user);
    }

    public Event getEventById(Long id) {

        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    public String updateEvent(Long id, EventRequest request) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Common fields
        event.setEventName(request.getEventName());
        event.setEventType(request.getEventType());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setBudget(request.getBudget());
        event.setNumberOfAttendees(request.getNumberOfAttendees());
        event.setDurationInDays(request.getDurationInDays());
        event.setVenueType(request.getVenueType());
        event.setLocationRiskLevel(request.getLocationRiskLevel());
        event.setSecurityLevel(request.getSecurityLevel());

        // Music concert specific fields
        event.setAlcoholAllowed(request.getAlcoholAllowed());
        event.setTemporaryStage(request.getTemporaryStage());
        event.setFireworksUsed(request.getFireworksUsed());
        event.setCelebrityInvolved(request.getCelebrityInvolved());

        // Conference specific fields
        event.setTemporaryBooths(request.getTemporaryBooths());
        event.setHighValueEquipment(request.getHighValueEquipment());
        event.setEmergencyPreparednessLevel(request.getEmergencyPreparednessLevel());

        eventRepository.save(event);

        return "Event updated successfully";
    }

    public String deleteEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        eventRepository.delete(event);

        return "Event deleted successfully";
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
        return dto;
    }

    // ========== NEW METHODS FOR SEPARATE EVENT TYPES ==========

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
        event.setLocationRiskLevel(request.getLocationRiskLevel());
        event.setSecurityLevel(request.getSecurityLevel());

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
        event.setLocationRiskLevel(request.getLocationRiskLevel());
        event.setSecurityLevel(request.getSecurityLevel());

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

    // Admin method to get all events
    public List<EventResponse> getAllEventsForAdmin() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private EventResponse convertToResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setEventId(event.getEventId());
        response.setEventName(event.getEventName());
        response.setEventType(event.getEventType());
        response.setCustomerName(event.getUser().getFullName());
        response.setLocation(event.getLocation());
        response.setEventDate(event.getEventDate());
        response.setBudget(event.getBudget());
        response.setNumberOfAttendees(event.getNumberOfAttendees());
        response.setDurationInDays(event.getDurationInDays());
        return response;
    }
}

