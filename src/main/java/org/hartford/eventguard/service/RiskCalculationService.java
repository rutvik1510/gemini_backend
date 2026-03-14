package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.WeatherRiskResponse;
import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.EventDomain;
import org.hartford.eventguard.entity.VenueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RiskCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(RiskCalculationService.class);

    private final MusicConcertRiskStrategy musicConcertRiskStrategy;
    private final ConferenceRiskStrategy conferenceRiskStrategy;
    private final WeatherRiskService weatherRiskService;

    public RiskCalculationService(MusicConcertRiskStrategy musicConcertRiskStrategy,
                                 ConferenceRiskStrategy conferenceRiskStrategy,
                                 WeatherRiskService weatherRiskService) {
        this.musicConcertRiskStrategy = musicConcertRiskStrategy;
        this.conferenceRiskStrategy = conferenceRiskStrategy;
        this.weatherRiskService = weatherRiskService;
    }

    public double calculateRisk(Event event) {
        logger.info("╔════════════════════════════════════════════════════════════╗");
        logger.info("║          RISK CALCULATION PROCESS STARTED                  ║");
        logger.info("╚════════════════════════════════════════════════════════════╝");
        logger.info("Event: {}", event.getEventName());
        logger.info("Event Type: {}", event.getEventType());
        logger.info("Venue Type: {}", event.getVenueType());
        logger.info("Is Outdoor: {}", event.getIsOutdoor());
        logger.info("Location: {}", event.getLocation());
        logger.info("Date: {}", event.getEventDate());
        logger.info("════════════════════════════════════════════════════════════");

        // Step 1: Calculate event-specific risk using strategy pattern
        RiskCalculationStrategy strategy = selectStrategy(event.getEventType());
        double eventRisk = strategy.calculateRisk(event);

        logger.info("┌────────────────────────────────────────────────────────────┐");
        logger.info("│ STEP 1: EVENT-SPECIFIC RISK                                │");
        logger.info("└────────────────────────────────────────────────────────────┘");
        logger.info("Strategy Used: {}", strategy.getClass().getSimpleName());
        logger.info("Event Risk Score: {}%", String.format("%.2f", eventRisk));
        logger.info("");

        // Step 2: Determine if event is outdoor
        boolean isOutdoorEvent = false;

        // Music concerts use isOutdoor field
        if (event.getEventType() == EventDomain.OUTDOOR_MUSIC_CONCERT) {
            isOutdoorEvent = (event.getIsOutdoor() != null && event.getIsOutdoor());
            logger.info("Event Type: OUTDOOR_MUSIC_CONCERT");
            logger.info("isOutdoor field: {}", event.getIsOutdoor());
        }
        // Corporate conferences use venueType field
        else if (event.getEventType() == EventDomain.CORPORATE_TECH_CONFERENCE) {
            isOutdoorEvent = (event.getVenueType() == VenueType.OUTDOOR);
            logger.info("Event Type: CORPORATE_TECH_CONFERENCE");
            logger.info("venueType field: {}", event.getVenueType());
        }

        // Step 2: Add weather risk if outdoor event
        double weatherRisk = 0.0;
        if (isOutdoorEvent) {
            logger.info("┌────────────────────────────────────────────────────────────┐");
            logger.info("│ STEP 2: WEATHER RISK CALCULATION                          │");
            logger.info("└────────────────────────────────────────────────────────────┘");
            logger.info("Event is OUTDOOR → Weather risk will be applied");
            logger.info("");

            WeatherRiskResponse weatherResponse = weatherRiskService.getWeatherRisk(
                    event.getLocation(),
                    event.getEventDate()
            );

            weatherRisk = weatherResponse.getWeatherRiskScore();
        } else {
            logger.info("┌────────────────────────────────────────────────────────────┐");
            logger.info("│ STEP 2: WEATHER RISK (INDOOR EVENT - SKIPPED)             │");
            logger.info("└────────────────────────────────────────────────────────────┘");
            if (event.getEventType() == EventDomain.OUTDOOR_MUSIC_CONCERT) {
                logger.info("Event Type: OUTDOOR_MUSIC_CONCERT");
                logger.info("isOutdoor = {} → Weather risk skipped", event.getIsOutdoor());
            } else if (event.getEventType() == EventDomain.CORPORATE_TECH_CONFERENCE) {
                logger.info("Event Type: CORPORATE_TECH_CONFERENCE");
                logger.info("VenueType = {} → Weather risk skipped", event.getVenueType());
            }
            logger.info("");
        }

        // Step 3: Calculate final risk
        double finalRisk = eventRisk + weatherRisk;

        logger.info("╔════════════════════════════════════════════════════════════╗");
        logger.info("║                    FINAL RISK SUMMARY                      ║");
        logger.info("╚════════════════════════════════════════════════════════════╝");
        logger.info("Event Risk:          {}%", String.format("%.2f", eventRisk));
        logger.info("Weather Risk:        {}%", String.format("%.2f", weatherRisk));
        logger.info("────────────────────────────────────────────────────────────");
        logger.info("TOTAL RISK:          {}%", String.format("%.2f", finalRisk));
        logger.info("════════════════════════════════════════════════════════════");
        logger.info("");

        return finalRisk;
    }

    public DetailedRiskBreakdown calculateRiskWithBreakdown(Event event) {
        System.out.println(">>> FULL RISK BREAKDOWN INITIATED: " + event.getEventName());
        
        // Calculate event-specific risk with factors
        RiskCalculationStrategy strategy = selectStrategy(event.getEventType());
        RiskCalculationStrategy.EventRiskResult eventResult = strategy.calculateDetailedRisk(event);
        double eventRisk = eventResult.getScore();
        java.util.List<String> factorList = new java.util.ArrayList<>(eventResult.getFactors());

        // Determine if event is outdoor
        boolean isOutdoorEvent = false;

        // Music concerts use isOutdoor field
        if (event.getEventType() == EventDomain.OUTDOOR_MUSIC_CONCERT) {
            isOutdoorEvent = (event.getIsOutdoor() != null && event.getIsOutdoor());
        }
        // Corporate conferences use venueType field
        else if (event.getEventType() == EventDomain.CORPORATE_TECH_CONFERENCE) {
            isOutdoorEvent = (event.getVenueType() == VenueType.OUTDOOR);
        }

        // Calculate weather risk if outdoor
        double weatherRisk = 0.0;
        WeatherRiskResponse weatherData = null;

        if (isOutdoorEvent) {
            System.out.println(">>> OUTDOOR EVENT DETECTED - CALCULATING WEATHER RISK");
            weatherData = weatherRiskService.getWeatherRisk(
                    event.getLocation(),
                    event.getEventDate()
            );
            weatherRisk = weatherData.getWeatherRiskScore();
            
            // Add weather factors manually based on logic in WeatherRiskService
            if (weatherData.getTemperature() != null && weatherData.getTemperature() > 45) factorList.add("Extreme Heat (+1.0)");
            if (weatherData.getWindSpeed() != null && weatherData.getWindSpeed() > 20) factorList.add("High Wind (+1.0)");
            if (weatherData.getRainProbability() != null && weatherData.getRainProbability() > 80) factorList.add("High Humidity/Rain (+0.5)");
        } else {
            System.out.println(">>> INDOOR EVENT - WEATHER RISK BYPASSED (0.0%)");
        }

        String riskFactors = String.join(", ", factorList);
        
        System.out.println("TOTAL COMBINED RISK: " + (eventRisk + weatherRisk) + "%");
        System.out.println("FACTORS IDENTIFIED: " + riskFactors);
        System.out.println(">>> RISK CALCULATION COMPLETE");

        return new DetailedRiskBreakdown(eventRisk, weatherRisk, riskFactors, weatherData);
    }

    private RiskCalculationStrategy selectStrategy(EventDomain eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }

        switch (eventType) {
            case OUTDOOR_MUSIC_CONCERT:
                return musicConcertRiskStrategy;
            case CORPORATE_TECH_CONFERENCE:
                return conferenceRiskStrategy;
            default:
                throw new IllegalArgumentException("Unsupported event type: " + eventType);
        }
    }
}
