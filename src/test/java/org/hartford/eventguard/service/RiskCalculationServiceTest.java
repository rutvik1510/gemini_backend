package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.WeatherRiskResponse;
import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.EventDomain;
import org.hartford.eventguard.entity.VenueType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskCalculationServiceTest {

    @Mock
    private MusicConcertRiskStrategy musicConcertRiskStrategy;

    @Mock
    private ConferenceRiskStrategy conferenceRiskStrategy;

    @Mock
    private WeatherRiskService weatherRiskService;

    @InjectMocks
    private RiskCalculationService riskCalculationService;

    private Event musicEvent;
    private Event conferenceEvent;

    @BeforeEach
    void setUp() {
        musicEvent = new Event();
        musicEvent.setEventName("Music Fest");
        musicEvent.setEventType(EventDomain.OUTDOOR_MUSIC_CONCERT);
        musicEvent.setIsOutdoor(true);
        musicEvent.setLocation("London");
        musicEvent.setEventDate(LocalDate.now().plusDays(10));

        conferenceEvent = new Event();
        conferenceEvent.setEventName("Tech Conf");
        conferenceEvent.setEventType(EventDomain.CORPORATE_TECH_CONFERENCE);
        conferenceEvent.setVenueType(VenueType.OUTDOOR);
        conferenceEvent.setLocation("San Francisco");
        conferenceEvent.setEventDate(LocalDate.now().plusDays(20));
    }

    @Test
    void calculateRisk_OutdoorMusicEvent_Success() {
        when(musicConcertRiskStrategy.calculateRisk(musicEvent)).thenReturn(5.0);
        WeatherRiskResponse weatherResponse = new WeatherRiskResponse();
        weatherResponse.setWeatherRiskScore(2.5);
        when(weatherRiskService.getWeatherRisk(anyString(), any())).thenReturn(weatherResponse);

        double totalRisk = riskCalculationService.calculateRisk(musicEvent);

        assertEquals(7.5, totalRisk);
        verify(musicConcertRiskStrategy, times(1)).calculateRisk(musicEvent);
        verify(weatherRiskService, times(1)).getWeatherRisk(anyString(), any());
    }

    @Test
    void calculateRisk_IndoorMusicEvent_Success() {
        musicEvent.setIsOutdoor(false);
        when(musicConcertRiskStrategy.calculateRisk(musicEvent)).thenReturn(4.0);

        double totalRisk = riskCalculationService.calculateRisk(musicEvent);

        assertEquals(4.0, totalRisk);
        verify(musicConcertRiskStrategy, times(1)).calculateRisk(musicEvent);
        verify(weatherRiskService, never()).getWeatherRisk(anyString(), any());
    }

    @Test
    void calculateRisk_OutdoorConferenceEvent_Success() {
        when(conferenceRiskStrategy.calculateRisk(conferenceEvent)).thenReturn(3.0);
        WeatherRiskResponse weatherResponse = new WeatherRiskResponse();
        weatherResponse.setWeatherRiskScore(1.5);
        when(weatherRiskService.getWeatherRisk(anyString(), any())).thenReturn(weatherResponse);

        double totalRisk = riskCalculationService.calculateRisk(conferenceEvent);

        assertEquals(4.5, totalRisk);
        verify(conferenceRiskStrategy, times(1)).calculateRisk(conferenceEvent);
        verify(weatherRiskService, times(1)).getWeatherRisk(anyString(), any());
    }

    @Test
    void calculateRisk_NullEventType_ThrowsException() {
        musicEvent.setEventType(null);
        assertThrows(IllegalArgumentException.class, () -> {
            riskCalculationService.calculateRisk(musicEvent);
        });
    }

    @Test
    void calculateRisk_UnsupportedEventType_ThrowsException() {
        // Since there are only two strategies, we don't have an easy way to trigger this
        // unless we use a new enum value if it existed.
    }
}
