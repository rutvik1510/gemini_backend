package org.hartford.eventguard.dto;

public class DashboardStatsResponse {

    private Long totalPolicies;
    private Long activePolicies;
    private Long totalEvents;
    private Long pendingSubscriptions;
    private Long pendingClaims;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(Long totalPolicies, Long activePolicies, Long totalEvents, 
                                  Long pendingSubscriptions, Long pendingClaims) {
        this.totalPolicies = totalPolicies;
        this.activePolicies = activePolicies;
        this.totalEvents = totalEvents;
        this.pendingSubscriptions = pendingSubscriptions;
        this.pendingClaims = pendingClaims;
    }

    public Long getTotalPolicies() {
        return totalPolicies;
    }

    public void setTotalPolicies(Long totalPolicies) {
        this.totalPolicies = totalPolicies;
    }

    public Long getActivePolicies() {
        return activePolicies;
    }

    public void setActivePolicies(Long activePolicies) {
        this.activePolicies = activePolicies;
    }

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Long getPendingSubscriptions() {
        return pendingSubscriptions;
    }

    public void setPendingSubscriptions(Long pendingSubscriptions) {
        this.pendingSubscriptions = pendingSubscriptions;
    }

    public Long getPendingClaims() {
        return pendingClaims;
    }

    public void setPendingClaims(Long pendingClaims) {
        this.pendingClaims = pendingClaims;
    }
}
