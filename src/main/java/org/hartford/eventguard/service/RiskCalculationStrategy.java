package org.hartford.eventguard.service;

import org.hartford.eventguard.entity.Event;
import java.util.ArrayList;
import java.util.List;

public interface RiskCalculationStrategy {

    double calculateRisk(Event event);

    EventRiskResult calculateDetailedRisk(Event event);

    public static class EventRiskResult {
        private final double score;
        private final List<String> factors;

        public EventRiskResult(double score, List<String> factors) {
            this.score = score;
            this.factors = factors;
        }

        public double getScore() { return score; }
        public List<String> getFactors() { return factors; }
    }
}
