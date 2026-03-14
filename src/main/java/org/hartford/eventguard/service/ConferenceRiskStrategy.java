package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.VenueType;
import org.springframework.stereotype.Component;

@Component
public class ConferenceRiskStrategy implements RiskCalculationStrategy {

    @Override
    public double calculateRisk(Event event) {
        return calculateDetailedRisk(event).getScore();
    }

    @Override
    public EventRiskResult calculateDetailedRisk(Event event) {
        double risk = 0.0;
        java.util.List<String> factors = new java.util.ArrayList<>();

        System.out.println("\n┌──────────────────────────────────────────────────────────┐");
        System.out.println("│        CORPORATE CONFERENCE RISK ANALYSIS ENGINE         │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("  Event Name : " + event.getEventName());
        System.out.println("  Category   : CORPORATE TECH CONFERENCE");
        System.out.println("  Location   : " + event.getLocation());
        System.out.println("  Attendees  : " + (event.getNumberOfAttendees() != null ? event.getNumberOfAttendees() : "N/A"));
        System.out.println("├──────────────────────────────────────────────────────────┤");

        // attendees > 500 → +1.5
        if (event.getNumberOfAttendees() != null && event.getNumberOfAttendees() > 500) {
            risk += 1.5;
            factors.add("Large Attendance (+1.5)");
            System.out.println("  [+] High Attendance Density   : +1.5%");
        }

        // temporaryBooths → +1
        if (Boolean.TRUE.equals(event.getTemporaryBooths())) {
            risk += 1.0;
            factors.add("Temporary Booths (+1.0)");
            System.out.println("  [+] Structural (Temp Booths)  : +1.0%");
        }

        // highValueEquipment → +2
        if (Boolean.TRUE.equals(event.getHighValueEquipment())) {
            risk += 2.0;
            factors.add("High-Value Equipment (+2.0)");
            System.out.println("  [+] Asset Hazard (Equipment)  : +2.0%");
        }

        // emergencyPreparednessLevel = LOW → +2
        if ("LOW".equalsIgnoreCase(event.getEmergencyPreparednessLevel())) {
            risk += 2.0;
            factors.add("Low Emergency Preparedness (+2.0)");
            System.out.println("  [+] Emergency Plan Deficiency : +2.0%");
        }

        // venueType = OUTDOOR → +1
        if (event.getVenueType() == VenueType.OUTDOOR) {
            risk += 1.0;
            factors.add("Outdoor Venue (+1.0)");
            System.out.println("  [+] Outdoor Venue Exposure    : +1.0%");
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

        // No Fire NOC is a major risk (+2.5)
        if (!Boolean.TRUE.equals(event.getHasFireNOC())) {
            risk += 2.5;
            factors.add("No Fire NOC (+2.5)");
            System.out.println("  [!] CRITICAL: No Fire NOC     : +2.5%");
        }

        // CCTV helps reduce equipment theft risk (-0.5)
        if (Boolean.TRUE.equals(event.getHasCCTV())) {
            risk -= 0.5;
            factors.add("CCTV Surveillance Present (-0.5)");
            System.out.println("  [-] Mitigation: CCTV Present  : -0.5%");
        }

        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("  CORE EVENT RISK TOTAL         : " + risk + "%");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        return new EventRiskResult(risk, factors);
    }
}
