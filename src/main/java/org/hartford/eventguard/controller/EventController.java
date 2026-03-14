package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.CorporateEventRequest;
import org.hartford.eventguard.dto.EventResponse;
import org.hartford.eventguard.dto.MusicEventRequest;
import org.hartford.eventguard.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    // Constructor injection (best practice)
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**

     * @return Success message with EventResponse data
     */
    @PostMapping("/music")
    public ResponseEntity<ApiResponse<EventResponse>> createMusicEvent(
            @jakarta.validation.Valid @RequestBody MusicEventRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        EventResponse eventResponse = eventService.createMusicEvent(request, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Music event created successfully", eventResponse));
    }

    @PostMapping("/corporate")
    public ResponseEntity<ApiResponse<EventResponse>> createCorporateEvent(
            @jakarta.validation.Valid @RequestBody CorporateEventRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        EventResponse eventResponse = eventService.createCorporateEvent(request, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Corporate event created successfully", eventResponse));
    }

    /**

     * @return List of events
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getMyEvents(Authentication authentication) {
        String email = authentication.getName();
        List<EventResponse> events = eventService.getMyEventsDTO(email);
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
    }

    /**
     * GET /events/my - Get all events for logged-in user (alternative endpoint)
     * @param authentication Spring Security Authentication object
     * @return List of events
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getMyEventsAlternative(Authentication authentication) {
        String email = authentication.getName();
        List<EventResponse> events = eventService.getMyEventsDTO(email);
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
    }

    /**
     * GET /events/{id} - Get event by ID
     * @param id Event ID
     * @return Event details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        EventResponse event = eventService.getEventByIdDTO(id);
        return ResponseEntity.ok(ApiResponse.success("Event retrieved successfully", event));
    }


    /**
     * DELETE /events/{id} - Delete event
     * @param id Event ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEvent(@PathVariable Long id) {
        String message = eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}
