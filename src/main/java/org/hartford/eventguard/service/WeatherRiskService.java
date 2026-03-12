package org.hartford.eventguard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.eventguard.dto.WeatherRiskResponse;
import org.hartford.eventguard.dto.WeatherRiskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class WeatherRiskService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherRiskService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    public WeatherRiskService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "weatherCache", key = "#location + '-' + #eventDate")
    public WeatherRiskResponse getWeatherRisk(String location, LocalDate eventDate) {
        logger.info("========================================");
        logger.info("WEATHER RISK CALCULATION STARTED");
        logger.info("Location: {}", location);
        logger.info("Event Date: {}", eventDate);
        logger.info("========================================");

        WeatherRiskResponse response = new WeatherRiskResponse();

        try {
            // Call WeatherAPI.com
            String url = weatherApiUrl + "?key=" + apiKey + "&q=" + location;

            logger.info("Calling WeatherAPI.com for location: {}", location);
            logger.info("API URL: {}", weatherApiUrl);

            ResponseEntity<String> apiResponse = restTemplate.getForEntity(url, String.class);

            if (apiResponse.getBody() != null) {
                // Parse JSON response using Jackson
                JsonNode root = objectMapper.readTree(apiResponse.getBody());

                // Extract weather data from WeatherAPI response
                double temperature = root.path("current").path("temp_c").asDouble();
                double windSpeed = root.path("current").path("wind_kph").asDouble();
                double humidity = root.path("current").path("humidity").asDouble();
                double precipitation = root.path("current").path("precip_mm").asDouble();

                response.setTemperature(temperature);
                response.setWindSpeed(windSpeed);
                response.setRainProbability(humidity); // Using humidity as proxy

                logger.info("========================================");
                logger.info("WEATHER DATA RETRIEVED:");
                logger.info("  → Temperature: {}°C", String.format("%.1f", temperature));
                logger.info("  → Wind Speed: {} km/h", String.format("%.1f", windSpeed));
                logger.info("  → Humidity: {}%", String.format("%.1f", humidity));
                logger.info("  → Precipitation: {} mm", String.format("%.1f", precipitation));
                logger.info("========================================");

                // Calculate weather risk using new rules
                WeatherRiskResult riskResult = calculateWeatherRisk(temperature, windSpeed, humidity, precipitation);
                response.setWeatherRiskScore(riskResult.getWeatherRiskScore());

                logger.info("WEATHER RISK CALCULATED: {}%", String.format("%.2f", riskResult.getWeatherRiskScore()));
                logger.info("========================================");
            }

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("ERROR FETCHING WEATHER DATA");
            logger.error("Error message: {}", e.getMessage());
            logger.error("Using default weather values");
            logger.error("========================================");

            // Return default values if API call fails
            response.setTemperature(20.0);
            response.setWindSpeed(10.0);
            response.setRainProbability(30.0);
            response.setWeatherRiskScore(0.5); // Default risk score
        }

        return response;
    }

    public WeatherRiskResult calculateWeatherRisk(String location) {
        logger.info("========================================");
        logger.info("WEATHER RISK CALCULATION STARTED");
        logger.info("Location: {}", location);
        logger.info("========================================");

        try {
            // Call WeatherAPI.com
            String url = weatherApiUrl + "?key=" + apiKey + "&q=" + location;

            logger.info("Calling WeatherAPI.com for location: {}", location);

            ResponseEntity<String> apiResponse = restTemplate.getForEntity(url, String.class);

            if (apiResponse.getBody() != null) {
                // Parse JSON response
                JsonNode root = objectMapper.readTree(apiResponse.getBody());

                // Extract weather data
                double temperature = root.path("current").path("temp_c").asDouble();
                double windSpeed = root.path("current").path("wind_kph").asDouble();
                double humidity = root.path("current").path("humidity").asDouble();
                double precipitation = root.path("current").path("precip_mm").asDouble();

                logger.info("========================================");
                logger.info("WEATHER DATA RETRIEVED:");
                logger.info("Location: {}", location);
                logger.info("Temperature: {}°C", String.format("%.1f", temperature));
                logger.info("Wind Speed: {} km/h", String.format("%.1f", windSpeed));
                logger.info("Humidity: {}%", String.format("%.1f", humidity));
                logger.info("Precipitation: {} mm", String.format("%.1f", precipitation));
                logger.info("========================================");

                // Calculate risk
                WeatherRiskResult result = calculateWeatherRisk(temperature, windSpeed, humidity, precipitation);

                logger.info("Weather Risk Score: {}", String.format("%.2f", result.getWeatherRiskScore()));
                logger.info("========================================");

                return result;
            }

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("ERROR FETCHING WEATHER DATA");
            logger.error("Error message: {}", e.getMessage());
            logger.error("Using default weather values");
            logger.error("========================================");
        }

        // Return default values if API call fails
        return new WeatherRiskResult(20.0, 10.0, 50.0, 0.0, 0.5);
    }

    private WeatherRiskResult calculateWeatherRisk(Double temperature, Double windSpeed,
                                                   Double humidity, Double precipitation) {
        double risk = 0.0;

        logger.info("WEATHER RISK BREAKDOWN:");

        // Temperature risk: > 35°C → +1 risk
        if (temperature > 45) {
            risk += 1.0;
            logger.info("  ✓ Temperature Risk: +1.0% (Temperature: {}°C > 35°C)", String.format("%.1f", temperature));
        } else {
            logger.info("  ✗ Temperature Risk: +0.0% (Temperature: {}°C ≤ 35°C)", String.format("%.1f", temperature));
        }

        // Wind risk: > 20 km/h → +1 risk
        if (windSpeed > 20) {
            risk += 1.0;
            logger.info("  ✓ Wind Risk: +1.0% (Wind speed: {} km/h > 20 km/h)", String.format("%.1f", windSpeed));
        } else {
            logger.info("  ✗ Wind Risk: +0.0% (Wind speed: {} km/h ≤ 20 km/h)", String.format("%.1f", windSpeed));
        }

        // Humidity risk: > 80% → +0.5 risk
        if (humidity > 80) {
            risk += 0.5;
            logger.info("  ✓ Humidity Risk: +0.5% (Humidity: {}% > 80%)", String.format("%.1f", humidity));
        } else {
            logger.info("  ✗ Humidity Risk: +0.0% (Humidity: {}% ≤ 80%)", String.format("%.1f", humidity));
        }

        // Precipitation risk: > 5 mm → +2 risk
        if (precipitation > 5) {
            risk += 2.0;
            logger.info("  ✓ Precipitation Risk: +2.0% (Precipitation: {} mm > 5 mm)", String.format("%.1f", precipitation));
        } else {
            logger.info("  ✗ Precipitation Risk: +0.0% (Precipitation: {} mm ≤ 5 mm)", String.format("%.1f", precipitation));
        }

        logger.info("────────────────────────────────────────");
        logger.info("TOTAL WEATHER RISK: {}%", String.format("%.2f", risk));

        return new WeatherRiskResult(temperature, windSpeed, humidity, precipitation, risk);
    }
}
