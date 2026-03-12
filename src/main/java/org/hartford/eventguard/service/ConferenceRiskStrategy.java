package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.VenueType;
import org.springframework.stereotype.Component;

@Component
public class ConferenceRiskStrategy implements RiskCalculationStrategy {

    @Override
    public double calculateRisk(Event event) {
        double risk = 0.0;

        // attendees > 500 → +1.5
        if (event.getNumberOfAttendees() != null && event.getNumberOfAttendees() > 500) {
            risk += 1.5;
        }

        // temporaryBooths → +1
        if (Boolean.TRUE.equals(event.getTemporaryBooths())) {
            risk += 1.0;
        }

        // highValueEquipment → +2
        if (Boolean.TRUE.equals(event.getHighValueEquipment())) {
            risk += 2.0;
        }

        // emergencyPreparednessLevel = LOW → +2
        if ("LOW".equalsIgnoreCase(event.getEmergencyPreparednessLevel())) {
            risk += 2.0;
        }

        // venueType = OUTDOOR → +1
        if (event.getVenueType() == VenueType.OUTDOOR) {
            risk += 1.0;
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

        // No Fire NOC is a major risk for conferences with high-value equipment (+2.5)
        if (!Boolean.TRUE.equals(event.getHasFireNOC())) {
            risk += 2.5;
        }

        // CCTV helps reduce equipment theft risk (-0.5)
        if (Boolean.TRUE.equals(event.getHasCCTV())) {
            risk -= 0.5;
        }

        return risk;
    }
}
