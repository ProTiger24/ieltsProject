package com.myproject.ieltsproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String getCompletion(String userWriting) {
    
        try {
            System.out.println("🚀 Calling Gemini API...");

            if (apiKey == null || apiKey.isEmpty()) {
                return "Please configure a valid GEMINI_API_KEY environment variable.";
            }

            Map<String, Object> part = new HashMap<>();
            part.put("text", userWriting);

            Map<String, Object> content = new HashMap<>();
            content.put("role", "user");
            content.put("parts", Collections.singletonList(part));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(content));

            Map<String, Object> config = new HashMap<>();
            config.put("temperature", 0.7);
            config.put("maxOutputTokens", 500);
            requestBody.put("generationConfig", config);

             
            String finalUrl = apiUrl + "?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.exchange(
                    finalUrl, HttpMethod.POST, entity, Map.class);

            Map<String, Object> body = response.getBody();

           
            if (body != null && body.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");

                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);

                    
                    Map<String, Object> contentObject = (Map<String, Object>) candidate.get("content");
                    if (contentObject == null) {
                        
                        return "⚠️ API returned candidate without 'content' object.";
                    }

                    if (contentObject.containsKey("parts")) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentObject.get("parts");

                        if (parts != null && !parts.isEmpty()) {
                            String contentText = (String) parts.get(0).get("text");

                            if (contentText != null && !contentText.isEmpty()) {
                                System.out.println("✅ Gemini API success: Content Parsed.");
                                return contentText;
                            }
                        }
                    }

                    return "⚠️ API returned a response, but no valid text (Check safety filters).";
                }
            }

            return "⚠️ No valid response body or candidates from Gemini API.";

        } catch (Exception e) {
            System.out.println("❌ Gemini API ERROR: " + e.getMessage());
            return "Gemini API Error: " + e.getMessage();
        }
    }
}