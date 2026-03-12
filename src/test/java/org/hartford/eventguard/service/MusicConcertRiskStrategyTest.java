package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.VenueType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MusicConcertRiskStrategyTest {

    private final MusicConcertRiskStrategy strategy = new MusicConcertRiskStrategy();

    @Test
    void calculateRisk_returnsZeroForLowRiskMusicEvent() {
        Event event = new Event();
        event.setNumberOfAttendees(500);
        event.setVenueType(VenueType.INDOOR);
        event.setAlcoholAllowed(false);
        event.setTemporaryStage(false);
        event.setFireworksUsed(false);
        event.setCelebrityInvolved(false);
        event.setLocationRiskLevel("LOW");
        event.setSecurityLevel("HIGH");

        double risk = strategy.calculateRisk(event);

        assertEquals(0.0, risk);
    }

    @Test
    void calculateRisk_addsAllConfiguredMusicRiskFactors() {
        Event event = new Event();
        event.setNumberOfAttendees(2000);
        event.setVenueType(VenueType.OUTDOOR);
        event.setAlcoholAllowed(true);
        event.setTemporaryStage(true);
        event.setFireworksUsed(true);
        event.setCelebrityInvolved(true);
        event.setLocationRiskLevel("HIGH");
        event.setSecurityLevel("LOW");

        double risk = strategy.calculateRisk(event);

        assertEquals(10.5, risk);
    }
}
