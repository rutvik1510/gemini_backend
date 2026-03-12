package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Event;

public interface RiskCalculationStrategy {

    double calculateRisk(Event event);
}
