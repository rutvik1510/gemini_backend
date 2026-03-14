package org.hartford.eventguard.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MusicEventRequest {

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

    // Music-specific risk fields
    private Boolean isOutdoor;
    private Boolean alcoholAllowed;
    private Boolean temporaryStructure;
    private Boolean temporaryStage;
    private Boolean fireworksUsed;
    private Boolean celebrityInvolved;

    // Objective Security & Safety fields
    private Boolean hasProfessionalSecurity;
    private Boolean hasCCTV;
    private Boolean hasMetalDetectors;
    private Boolean hasFireNOC;
    private Boolean hasOnSiteFireSafety;
    private String safetyComplianceDocPath;

    public MusicEventRequest() {
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

    public Boolean getIsOutdoor() {
        return isOutdoor;
    }

    public void setIsOutdoor(Boolean isOutdoor) {
        this.isOutdoor = isOutdoor;
    }

    public Boolean getAlcoholAllowed() {
        return alcoholAllowed;
    }

    public void setAlcoholAllowed(Boolean alcoholAllowed) {
        this.alcoholAllowed = alcoholAllowed;
    }

    public Boolean getTemporaryStructure() {
        return temporaryStructure;
    }

    public void setTemporaryStructure(Boolean temporaryStructure) {
        this.temporaryStructure = temporaryStructure;
    }

    public Boolean getTemporaryStage() {
        return temporaryStage;
    }

    public void setTemporaryStage(Boolean temporaryStage) {
        this.temporaryStage = temporaryStage;
    }

    public Boolean getFireworksUsed() {
        return fireworksUsed;
    }

    public void setFireworksUsed(Boolean fireworksUsed) {
        this.fireworksUsed = fireworksUsed;
    }

    public Boolean getCelebrityInvolved() {
        return celebrityInvolved;
    }

    public void setCelebrityInvolved(Boolean celebrityInvolved) {
        this.celebrityInvolved = celebrityInvolved;
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
