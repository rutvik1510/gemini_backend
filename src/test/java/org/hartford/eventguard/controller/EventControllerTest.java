package org.hartford.eventguard.controller;

import org.hartford.eventguard.dto.ApiResponse;
import org.hartford.eventguard.dto.EventResponse;
import org.hartford.eventguard.dto.MusicEventRequest;
import org.hartford.eventguard.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private Authentication authentication;

    private EventController eventController;

    @BeforeEach
    void setUp() {
        eventController = new EventController(eventService);
    }

    @Test
    void createMusicEvent_Success() {
        MusicEventRequest request = new MusicEventRequest();
        request.setEventName("Festival");
        
        EventResponse response = new EventResponse();
        response.setEventName("Festival");

        when(authentication.getName()).thenReturn("test@test.com");
        when(eventService.createMusicEvent(any(MusicEventRequest.class), eq("test@test.com"))).thenReturn(response);

        ResponseEntity<ApiResponse<EventResponse>> result = eventController.createMusicEvent(request, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Festival", result.getBody().getData().getEventName());
    }

    @Test
    void getMyEvents_Success() {
        EventResponse response = new EventResponse();
        response.setEventId(1L);

        when(authentication.getName()).thenReturn("test@test.com");
        when(eventService.getMyEventsDTO("test@test.com")).thenReturn(Collections.singletonList(response));

        ResponseEntity<ApiResponse<List<EventResponse>>> result = eventController.getMyEvents(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getData().size());
    }

    @Test
    void deleteEvent_Success() {
        when(eventService.deleteEvent(1L)).thenReturn("Event deleted successfully");

        ResponseEntity<ApiResponse<String>> result = eventController.deleteEvent(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        // ApiResponse.success(message) puts message in 'message' field, 'data' is null
        assertEquals("Event deleted successfully", result.getBody().getMessage());
    }
}
