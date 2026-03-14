package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.VenueType;
import org.springframework.stereotype.Component;

@Component
public class MusicConcertRiskStrategy implements RiskCalculationStrategy {

    @Override
    public double calculateRisk(Event event) {
        return calculateDetailedRisk(event).getScore();
    }

    @Override
    public EventRiskResult calculateDetailedRisk(Event event) {
        double risk = 0.0;
        java.util.List<String> factors = new java.util.ArrayList<>();

        System.out.println("\n┌──────────────────────────────────────────────────────────┐");
        System.out.println("│        MUSIC CONCERT RISK ANALYSIS ENGINE                │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("  Event Name : " + event.getEventName());
        System.out.println("  Category   : MUSIC CONCERT");
        System.out.println("  Location   : " + event.getLocation());
        System.out.println("  Attendees  : " + (event.getNumberOfAttendees() != null ? event.getNumberOfAttendees() : "N/A"));
        System.out.println("├──────────────────────────────────────────────────────────┤");

        // attendees > 1000 → +2
        if (event.getNumberOfAttendees() != null && event.getNumberOfAttendees() > 1000) {
            risk += 2.0;
            factors.add("Large Crowd (+2.0)");
            System.out.println("  [+] Large Crowd Detected      : +2.0%");
        }

        // venueType = OUTDOOR → +1
        if (event.getVenueType() == VenueType.OUTDOOR) {
            risk += 1.0;
            factors.add("Outdoor Venue (+1.0)");
            System.out.println("  [+] Outdoor Venue Exposure    : +1.0%");
        }

        // alcoholAllowed → +1
        if (Boolean.TRUE.equals(event.getAlcoholAllowed())) {
            risk += 1.0;
            factors.add("Alcohol Served (+1.0)");
            System.out.println("  [+] Alcohol Liability         : +1.0%");
        }

        // temporaryStage → +0.5
        if (Boolean.TRUE.equals(event.getTemporaryStage())) {
            risk += 0.5;
            factors.add("Temporary Stage (+0.5)");
            System.out.println("  [+] Structural (Temp Stage)   : +0.5%");
        }

        // fireworksUsed → +1.5
        if (Boolean.TRUE.equals(event.getFireworksUsed())) {
            risk += 1.5;
            factors.add("Fireworks Used (+1.5)");
            System.out.println("  [+] Pyrotechnic Hazard        : +1.5%");
        }

        // celebrityInvolved → +2
        if (Boolean.TRUE.equals(event.getCelebrityInvolved())) {
            risk += 2.0;
            factors.add("Celebrity Appearance (+2.0)");
            System.out.println("  [+] High-Profile Security     : +2.0%");
        }

        // locationRiskLevel = HIGH → +1.5
        if ("HIGH".equalsIgnoreCase(event.getLocationRiskLevel())) {
            risk += 1.5;
            factors.add("High-Risk Location (+1.5)");
            System.out.println("  [+] Geographic Risk (High)    : +1.5%");
        }

        // securityLevel = LOW → +1
        if ("LOW".equalsIgnoreCase(event.getSecurityLevel())) {
            risk += 1.0;
            factors.add("Low Security Level (+1.0)");
            System.out.println("  [+] Security Deficiency       : +1.0%");
        }

        // No Fire NOC is a major risk (+3.0)
        if (!Boolean.TRUE.equals(event.getHasFireNOC())) {
            risk += 3.0;
            factors.add("No Fire NOC (+3.0)");
            System.out.println("  [!] CRITICAL: No Fire NOC     : +3.0%");
        }

        // Presence of metal detectors at a concert reduces risk (-0.5)
        if (Boolean.TRUE.equals(event.getHasMetalDetectors())) {
            risk -= 0.5;
            factors.add("Metal Detectors Present (-0.5)");
            System.out.println("  [-] Mitigation: Metal Detects : -0.5%");
        }

        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("  CORE EVENT RISK TOTAL         : " + risk + "%");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        return new EventRiskResult(risk, factors);
    }
}
