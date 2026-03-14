package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.MusicEventRequest;
import org.hartford.eventguard.dto.EventResponse;
import org.hartford.eventguard.entity.*;
import org.hartford.eventguard.exception.InvalidRequestException;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.repo.ClaimsRepository;
import org.hartford.eventguard.repo.EventRepository;
import org.hartford.eventguard.repo.PolicySubscriptionRepository;
import org.hartford.eventguard.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PolicySubscriptionRepository subscriptionRepository;

    @Mock
    private ClaimsRepository claimsRepository;

    @InjectMocks
    private EventService eventService;

    private User testUser;
    private MusicEventRequest musicRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setFullName("Test User");

        musicRequest = new MusicEventRequest();
        musicRequest.setEventName("Music Festival");
        musicRequest.setEventDate(LocalDate.now());
        musicRequest.setLocation("New York");
        musicRequest.setBudget(50000.0);
        musicRequest.setNumberOfAttendees(1000);
        musicRequest.setDurationInDays(2);
        musicRequest.setHasProfessionalSecurity(true);
        musicRequest.setHasCCTV(true);
        musicRequest.setHasFireNOC(true);
    }

    @Test
    void createMusicEvent_Success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            e.setEventId(1L);
            return e;
        });
        when(subscriptionRepository.findByEvent_EventId(1L)).thenReturn(new ArrayList<>());

        EventResponse response = eventService.createMusicEvent(musicRequest, "test@test.com");

        assertNotNull(response);
        assertEquals("Music Festival", response.getEventName());
        assertEquals(1L, response.getEventId());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void createMusicEvent_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.createMusicEvent(musicRequest, "nonexistent@test.com");
        });

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void getEventByIdDTO_Success() {
        Event event = new Event();
        event.setEventId(1L);
        event.setEventName("Existing Event");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(subscriptionRepository.findByEvent_EventId(1L)).thenReturn(new ArrayList<>());

        EventResponse response = eventService.getEventByIdDTO(1L);

        assertEquals("Existing Event", response.getEventName());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void getEventByIdDTO_NotFound_ThrowsException() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getEventByIdDTO(99L);
        });
    }

    @Test
    void deleteEvent_Success() {
        Event event = new Event();
        event.setEventId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(subscriptionRepository.findByEvent_EventId(1L)).thenReturn(new ArrayList<>());

        String result = eventService.deleteEvent(1L);

        assertEquals("Event deleted successfully", result);
        verify(eventRepository, times(1)).delete(event);
    }

    @Test
    void deleteEvent_WithActiveSubscription_ThrowsException() {
        Event event = new Event();
        event.setEventId(1L);

        List<PolicySubscription> subs = new ArrayList<>();
        subs.add(new PolicySubscription());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(subscriptionRepository.findByEvent_EventId(1L)).thenReturn(subs);

        assertThrows(InvalidRequestException.class, () -> {
            eventService.deleteEvent(1L);
        });

        verify(eventRepository, never()).delete(any(Event.class));
    }
}
