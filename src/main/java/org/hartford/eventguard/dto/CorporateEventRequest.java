package org.hartford.eventguard.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hartford.eventguard.entity.VenueType;

import java.time.LocalDate;

public class CorporateEventRequest {

    // Common fields
    @NotBlank(message = "Event name is required")
    private String eventName;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Budget is required")
    @Min(value = 1000, message = "Budget must be at least 1000")
    private Double budget;

    @NotNull(message = "Number of attendees is required")
    @Min(value = 1, message = "Number of attendees must be at least 1")
    private Integer numberOfAttendees;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationInDays;

    // Corporate-specific risk fields
    @NotNull(message = "Venue type is required")
    private VenueType venueType;
    
    private Boolean temporaryBooths;
    private Boolean highValueEquipment;
    private String emergencyPreparednessLevel;

    // Objective Security & Safety fields
    private Boolean hasProfessionalSecurity;
    private Boolean hasCCTV;
    private Boolean hasMetalDetectors;
    private Boolean hasFireNOC;
    private Boolean hasOnSiteFireSafety;
    private String safetyComplianceDocPath;

    public CorporateEventRequest() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Integer getNumberOfAttendees() {
        return numberOfAttendees;
    }

    public void setNumberOfAttendees(Integer numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
    }

    public VenueType getVenueType() {
        return venueType;
    }

    public void setVenueType(VenueType venueType) {
        this.venueType = venueType;
    }

    public Boolean getTemporaryBooths() {
        return temporaryBooths;
    }

    public void setTemporaryBooths(Boolean temporaryBooths) {
        this.temporaryBooths = temporaryBooths;
    }

    public Boolean getHighValueEquipment() {
        return highValueEquipment;
    }

    public void setHighValueEquipment(Boolean highValueEquipment) {
        this.highValueEquipment = highValueEquipment;
    }

    public String getEmergencyPreparednessLevel() {
        return emergencyPreparednessLevel;
    }

    public void setEmergencyPreparednessLevel(String emergencyPreparednessLevel) {
        this.emergencyPreparednessLevel = emergencyPreparednessLevel;
    }

    public Boolean getHasProfessionalSecurity() {
        return hasProfessionalSecurity;
    }

    public void setHasProfessionalSecurity(Boolean hasProfessionalSecurity) {
        this.hasProfessionalSecurity = hasProfessionalSecurity;
    }

    public Boolean getHasCCTV() {
        return hasCCTV;
    }

    public void setHasCCTV(Boolean hasCCTV) {
        this.hasCCTV = hasCCTV;
    }

    public Boolean getHasMetalDetectors() {
        return hasMetalDetectors;
    }

    public void setHasMetalDetectors(Boolean hasMetalDetectors) {
        this.hasMetalDetectors = hasMetalDetectors;
    }

    public Boolean getHasFireNOC() {
        return hasFireNOC;
    }

    public void setHasFireNOC(Boolean hasFireNOC) {
        this.hasFireNOC = hasFireNOC;
    }

    public Boolean getHasOnSiteFireSafety() {
        return hasOnSiteFireSafety;
    }

    public void setHasOnSiteFireSafety(Boolean hasOnSiteFireSafety) {
        this.hasOnSiteFireSafety = hasOnSiteFireSafety;
    }

    public String getSafetyComplianceDocPath() {
        return safetyComplianceDocPath;
    }

    public void setSafetyComplianceDocPath(String safetyComplianceDocPath) {
        this.safetyComplianceDocPath = safetyComplianceDocPath;
    }
}
