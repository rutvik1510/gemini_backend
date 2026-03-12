package org.hartford.eventguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "events")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(nullable = false)
    private String eventName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventDomain eventType;

    private LocalDate eventDate;

    private String location;

    // ---------------- COMMON RISK FACTORS ----------------

    private Double budget;

    private Integer numberOfAttendees;

    private Integer durationInDays;

    @Enumerated(EnumType.STRING)
    private VenueType venueType;

    private String locationRiskLevel;   // LOW / MEDIUM / HIGH

    private String securityLevel;       // LOW / MEDIUM / HIGH

    // ---------------- MUSIC CONCERT SPECIFIC FIELDS ----------------

    private Boolean isOutdoor;

    private Boolean alcoholAllowed;

    private Boolean temporaryStructure;

    private Boolean temporaryStage;

    private Boolean fireworksUsed;

    private Boolean celebrityInvolved;

    // ---------------- CONFERENCE SPECIFIC FIELDS ----------------

    private Boolean temporaryBooths;

    private Boolean highValueEquipment;

    private String emergencyPreparednessLevel;  // LOW / MEDIUM / HIGH

    // ---------------- RELATIONSHIP ----------------

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ---------------- CONSTRUCTOR ----------------

    public Event() {}

    // ---------------- GETTERS & SETTERS ----------------

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public EventDomain getEventType() {
        return eventType;
    }

    public void setEventType(EventDomain eventType) {
        this.eventType = eventType;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}