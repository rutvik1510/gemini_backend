package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.VenueType;
import org.springframework.stereotype.Component;

@Component
public class MusicConcertRiskStrategy implements RiskCalculationStrategy {

    @Override
    public double calculateRisk(Event event) {
        double risk = 0.0;

        // attendees > 1000 → +2
        if (event.getNumberOfAttendees() != null && event.getNumberOfAttendees() > 1000) {
            risk += 2.0;
        }

        // venueType = OUTDOOR → +1
        if (event.getVenueType() == VenueType.OUTDOOR) {
            risk += 1.0;
        }

        // alcoholAllowed → +1
        if (Boolean.TRUE.equals(event.getAlcoholAllowed())) {
            risk += 1.0;
        }

        // temporaryStage → +0.5
        if (Boolean.TRUE.equals(event.getTemporaryStage())) {
            risk += 0.5;
        }

        // fireworksUsed → +1.5
        if (Boolean.TRUE.equals(event.getFireworksUsed())) {
            risk += 1.5;
        }

        // celebrityInvolved → +2
        if (Boolean.TRUE.equals(event.getCelebrityInvolved())) {
            risk += 2.0;
        }

        // locationRiskLevel = HIGH → +1.5
        if ("HIGH".equalsIgnoreCase(event.getLocationRiskLevel())) {
            risk += 1.5;
        }

        // securityLevel = LOW → +1
        if ("LOW".equalsIgnoreCase(event.getSecurityLevel())) {
            risk += 1.0;
        }

        // --- NEW OBJECTIVE CHECKS ---
        
        // No Fire NOC is a major risk (+3.0)
        if (!Boolean.TRUE.equals(event.getHasFireNOC())) {
            risk += 3.0;
        }

        // Presence of metal detectors at a concert reduces risk (-0.5)
        if (Boolean.TRUE.equals(event.getHasMetalDetectors())) {
            risk -= 0.5;
        }

        return risk;
    }
}
