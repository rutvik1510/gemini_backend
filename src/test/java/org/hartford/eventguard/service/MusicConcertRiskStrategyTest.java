package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.VenueType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MusicConcertRiskStrategyTest {

    private MusicConcertRiskStrategy strategy;
    private Event event;

    @BeforeEach
    void setUp() {
        strategy = new MusicConcertRiskStrategy();
        event = new Event();
    }

    @Test
    void calculateRisk_returnsBaseRiskForLowRiskMusicEvent() {
        // Base risk is 3.0 because hasFireNOC defaults to null/false
        event.setNumberOfAttendees(500);
        event.setHasFireNOC(true);
        double risk = strategy.calculateRisk(event);
        assertEquals(0.0, risk);
    }

    @Test
    void calculateRisk_noFireNOC_addsThreePercent() {
        event.setHasFireNOC(false);
        double risk = strategy.calculateRisk(event);
        assertEquals(3.0, risk);
    }

    @Test
    void calculateRisk_addsAllConfiguredMusicRiskFactors() {
        event.setNumberOfAttendees(2000); // +2.0
        event.setVenueType(VenueType.OUTDOOR); // +1.0
        event.setAlcoholAllowed(true); // +1.0
        event.setTemporaryStage(true); // +0.5
        event.setFireworksUsed(true); // +1.5
        event.setCelebrityInvolved(true); // +2.0
        event.setLocationRiskLevel("HIGH"); // +1.5
        event.setSecurityLevel("LOW"); // +1.0
        event.setHasFireNOC(false); // +3.0
        event.setHasMetalDetectors(true); // -0.5

        // Total: 2 + 1 + 1 + 0.5 + 1.5 + 2 + 1.5 + 1 + 3 - 0.5 = 13.0
        double risk = strategy.calculateRisk(event);
        assertEquals(13.0, risk);
    }
}
