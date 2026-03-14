package org.hartford.eventguard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.eventguard.dto.WeatherRiskResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherRiskServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WeatherRiskService weatherRiskService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherRiskService, "apiKey", "test_key");
        ReflectionTestUtils.setField(weatherRiskService, "weatherApiUrl", "http://api.weatherapi.com/v1/current.json");
    }

    @Test
    void getWeatherRisk_Success() throws Exception {
        String jsonResponse = "{\"current\": {\"temp_c\": 25.0, \"wind_kph\": 10.0, \"humidity\": 50, \"precip_mm\": 0.0}}";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(jsonResponse);
        
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode currentNode = mock(JsonNode.class);
        JsonNode tempNode = mock(JsonNode.class);
        JsonNode windNode = mock(JsonNode.class);
        JsonNode humidNode = mock(JsonNode.class);
        JsonNode precipNode = mock(JsonNode.class);

        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.path("current")).thenReturn(currentNode);
        when(currentNode.path("temp_c")).thenReturn(tempNode);
        when(tempNode.asDouble()).thenReturn(25.0);
        when(currentNode.path("wind_kph")).thenReturn(windNode);
        when(windNode.asDouble()).thenReturn(10.0);
        when(currentNode.path("humidity")).thenReturn(humidNode);
        when(humidNode.asDouble()).thenReturn(50.0);
        when(currentNode.path("precip_mm")).thenReturn(precipNode);
        when(precipNode.asDouble()).thenReturn(0.0);

        WeatherRiskResponse response = weatherRiskService.getWeatherRisk("London", LocalDate.now());

        assertNotNull(response);
        assertEquals(25.0, response.getTemperature());
        assertEquals(10.0, response.getWindSpeed());
        assertEquals(0.0, response.getWeatherRiskScore());
    }

    @Test
    void getWeatherRisk_HighRisk_Success() throws Exception {
        String jsonResponse = "{\"current\": {\"temp_c\": 46.0, \"wind_kph\": 21.0, \"humidity\": 81, \"precip_mm\": 6.0}}";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(jsonResponse);
        
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode currentNode = mock(JsonNode.class);
        
        JsonNode tempNode = mock(JsonNode.class);
        JsonNode windNode = mock(JsonNode.class);
        JsonNode humidNode = mock(JsonNode.class);
        JsonNode precipNode = mock(JsonNode.class);

        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.path("current")).thenReturn(currentNode);
        
        when(currentNode.path("temp_c")).thenReturn(tempNode);
        when(tempNode.asDouble()).thenReturn(46.0);
        when(currentNode.path("wind_kph")).thenReturn(windNode);
        when(windNode.asDouble()).thenReturn(21.0);
        when(currentNode.path("humidity")).thenReturn(humidNode);
        when(humidNode.asDouble()).thenReturn(81.0);
        when(currentNode.path("precip_mm")).thenReturn(precipNode);
        when(precipNode.asDouble()).thenReturn(6.0);

        WeatherRiskResponse response = weatherRiskService.getWeatherRisk("London", LocalDate.now());

        assertNotNull(response);
        // Risk logic: temp > 45 (+1.0), wind > 20 (+1.0), humid > 80 (+0.5), precip > 5 (+2.0) = 4.5
        assertEquals(4.5, response.getWeatherRiskScore());
    }

    @Test
    void getWeatherRisk_ApiFailure_ReturnsDefault() {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenThrow(new RuntimeException("API Down"));

        WeatherRiskResponse response = weatherRiskService.getWeatherRisk("London", LocalDate.now());

        assertNotNull(response);
        assertEquals(20.0, response.getTemperature());
        assertEquals(0.5, response.getWeatherRiskScore());
    }
}
