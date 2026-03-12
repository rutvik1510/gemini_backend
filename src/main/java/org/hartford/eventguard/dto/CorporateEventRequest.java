package org.hartford.eventguard.dto;

import org.hartford.eventguard.entity.VenueType;

import java.time.LocalDate;

public class CorporateEventRequest {

    // Common fields
    private String eventName;
    private LocalDate eventDate;
    private String location;
    private Double budget;
    private Integer numberOfAttendees;
    private Integer durationInDays;

    // Corporate-specific risk fields
    private VenueType venueType;
    private Boolean temporaryBooths;
    private Boolean highValueEquipment;
    private String emergencyPreparednessLevel;

    // Risk assessment fields
    private String locationRiskLevel;
    private String securityLevel;

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

    public String getLocationRiskLevel() {
        return locationRiskLevel;
    }

    public void setLocationRiskLevel(String locationRiskLevel) {
        this.locationRiskLevel = locationRiskLevel;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }
}
