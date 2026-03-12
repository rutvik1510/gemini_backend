package org.hartford.eventguard.dto;

import java.time.LocalDate;

public class MusicEventRequest {

    // Common fields
    private String eventName;
    private LocalDate eventDate;
    private String location;
    private Double budget;
    private Integer numberOfAttendees;
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
}
