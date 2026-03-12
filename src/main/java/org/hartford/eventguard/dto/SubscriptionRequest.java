package org.hartford.eventguard.dto;

public class SubscriptionRequest {

    private Long eventId;
    private Long policyId;

    public SubscriptionRequest() {}

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }
}
