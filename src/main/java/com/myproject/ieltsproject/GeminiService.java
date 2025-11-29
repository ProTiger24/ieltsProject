package com.myproject.ieltsproject;

<<<<<<< HEAD
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
=======
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
>>>>>>> 3166e765e07f8d60d37eb9486f5f60608864602a

@Service
public class GeminiService {

<<<<<<< HEAD
    // Initialize ObjectMapper once for thread-safe JSON parsing
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Properties injected from application.properties/yml
    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    // ======================================================================
    // 📝 WRITING SUBMISSION METHOD (Text to Text)
    // ======================================================================

    public String submitWriting(String userText) throws IOException {
        System.out.println("🎯 ========== WRITING SUBMISSION STARTED IN SERVICE ==========");

        try {
            // 1. Define the Prompt
            String writingPrompt = String.format(
                    "You are an official IELTS Writing examiner. Analyze this IELTS Writing task and provide feedback in Bengali. "
                            +
                            "Evaluate based on: Task Achievement, Coherence and Cohesion, Lexical Resource, and Grammatical Range. "
                            +
                            "Give specific examples from the text. " +
                            "Provide band score between 0-9 with decimal. " +
                            "Return response in exact JSON format with score, strengths, weaknesses, and suggestions in Bengali. "
                            +
                            "Writing: %s",
                    userText);

            // 2. Build the JSON request body
            String jsonBody = buildRequestBody(writingPrompt);
            System.out.println("🔄 Calling Gemini API for writing...");

            // 3. Execute the API call
            return executeGeminiCall(jsonBody);

        } catch (Exception e) {
            System.err.println("❌ Error in submitWriting: " + e.getMessage());
            throw new IOException("Writing evaluation failed: " + e.getMessage());
        }
    }

    // ======================================================================
    // 🎤 SPEAKING SUBMISSION METHOD (Text Transcript)
    // ======================================================================

    public String submitSpeaking(String userSpokenText) throws IOException {
        System.out.println("🎯 ========== SPEAKING SUBMISSION STARTED (TEXT) IN SERVICE ==========");

        try {
            // 1. Define the Prompt
            String speakingPrompt = String.format(
                    "You are an official IELTS Speaking examiner. Analyze this speaking transcript and provide feedback in Bengali. "
                            +
                            "Evaluate: Fluency, Pronunciation, Vocabulary, Grammar. " +
                            "Give specific examples from the transcript. " +
                            "Provide band score between 0-9 with decimal. " +
                            "Return response in exact JSON format with score, strengths, weaknesses, and suggestions in Bengali. "
                            +
                            "Transcript: %s",
                    userSpokenText);

            // 2. Build the JSON request body
            String jsonBody = buildRequestBody(speakingPrompt);
            System.out.println("🔄 Calling Gemini API for speaking (text)...");

            // 3. Execute the API call
            return executeGeminiCall(jsonBody);

        } catch (Exception e) {
            System.err.println("❌ Error in submitSpeaking: " + e.getMessage());
            throw new IOException("Speaking text evaluation failed: " + e.getMessage());
        }
    }

    // ======================================================================
    // 🎧 SPEAKING SUBMISSION METHOD (Audio File - Multimodal)
    // ======================================================================

    public String processSpeakingAudio(byte[] audioBytes, String fileName) throws IOException {
        System.out.println("🎯 ========== SPEAKING AUDIO PROCESSING STARTED IN SERVICE ==========");

        try {
            // 1. Encode the audio bytes into a Base64 string
            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

            // 2. Determine MIME Type
            String mimeType = getAudioMimeType(fileName);

            // 3. Define the Prompt for the multimodal call
            String speakingPrompt = "You are an official IELTS Speaking examiner. Analyze the fluency, " +
                    "pronunciation, vocabulary, and grammar in this audio submission and " +
                    "provide feedback in Bengali. Give specific examples from the transcript. " +
                    "Provide band score between 0-9 with decimal. " +
                    "Return response in exact JSON format with score, strengths, weaknesses, " +
                    "and suggestions in Bengali.";

            // 4. Build the JSON request body including audio data
            String jsonBody = buildAudioRequestBody(speakingPrompt, base64Audio, mimeType);
            System.out.println("🔄 Calling Gemini API for audio analysis...");

            // 5. Execute the API call
            return executeGeminiCall(jsonBody);

        } catch (Exception e) {
            System.err.println("❌ Error in processSpeakingAudio: " + e.getMessage());
            throw new IOException("Audio evaluation failed: " + e.getMessage());
        }
    }

    // ======================================================================
    // ⚙️ HELPER METHODS (Request Body Construction & MIME)
    // ======================================================================

    /**
     * Constructs the JSON body for a standard text-to-text Gemini API call.
     */
    private String buildRequestBody(String prompt) {
        String escapedPrompt = prompt.replaceAll("\"", "\\\"");

        return String.format(
                "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}",
                escapedPrompt);
    }

    /**
     * Constructs the JSON body for a multimodal (text + audio) Gemini API call.
     */
    private String buildAudioRequestBody(String prompt, String base64Audio, String mimeType) {
        String escapedPrompt = prompt.replaceAll("\"", "\\\"");

        return String.format(
                "{\"contents\": [{\"parts\": [" +
                        "{\"text\": \"%s\"}," +
                        "{\"inlineData\": {" +
                        "\"mimeType\": \"%s\"," +
                        "\"data\": \"%s\"" +
                        "}}" +
                        "]}]}",
                escapedPrompt, mimeType, base64Audio);
    }

    /**
     * Determines the MIME Type based on the file name.
     */
    private String getAudioMimeType(String fileName) {
        if (fileName == null)
            return "application/octet-stream";

        String lowerCaseName = fileName.toLowerCase();
        if (lowerCaseName.endsWith(".mp3"))
            return "audio/mp3";
        if (lowerCaseName.endsWith(".wav"))
            return "audio/wav";
        if (lowerCaseName.endsWith(".ogg"))
            return "audio/ogg";
        if (lowerCaseName.endsWith(".flac"))
            return "audio/flac";
        if (lowerCaseName.endsWith(".webm"))
            return "audio/webm"; // Common format for web recording

        return "audio/mpeg";
    }

    // ======================================================================
    // 🌐 CORE EXECUTION METHODS (HTTP Communication & Response Parsing)
    // ======================================================================

    /**
     * Executes the POST request to the Gemini API, handles errors, and
     * extracts the final JSON response from the nested API structure.
     */
    public String executeGeminiCall(String jsonBody) throws IOException {
        // Append the API key to the URL
        URL url = new URL(apiUrl + "?key=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            // Setup connection properties
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code and read response
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Handle API errors
                String error = readStream(conn.getErrorStream());
                throw new IOException("Gemini API call failed with code " + responseCode + ". Error: " + error);
            }

            // Read the raw JSON response from the API
            String rawGeminiResponse = readStream(conn.getInputStream());

            // Process the raw JSON response to extract the actual feedback JSON string
            return extractFeedbackJson(rawGeminiResponse);

        } finally {
            conn.disconnect();
        }
    }

    /**
     * Parses the raw Gemini API response and extracts the user feedback JSON string
     * from the nested 'candidates[0].content.parts[0].text' field, stripping
     * any surrounding Markdown code fences and non-JSON text.
     */
    private String extractFeedbackJson(String rawResponse) throws IOException {
        try {
            // Read the full API response tree
            JsonNode root = objectMapper.readTree(rawResponse);

            // Navigate the nested structure: candidates[0].content.parts[0].text
            JsonNode textNode = root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isTextual()) {
                String fullText = textNode.asText().trim();

                // 1. Strip Markdown fences (```json, ```)
                String jsonCandidate = fullText.replaceAll("^\\s*```json\\s*", "")
                        .replaceAll("^\\s*```\\s*", "")
                        .replaceAll("\\s*```\\s*$", "")
                        .trim();

                // 2. CRITICAL FIX: Isolate the JSON object by finding the first '{' and the
                // last '}'
                // This handles conversational text the model might insert before or after the
                // JSON.
                int startIndex = jsonCandidate.indexOf('{');
                int endIndex = jsonCandidate.lastIndexOf('}');

                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    // Return only the isolated JSON object string
                    return jsonCandidate.substring(startIndex, endIndex + 1);
                }

                // 3. Fallback: If JSON object could not be isolated, log and return the cleaned
                // string
                // (This might still fail on the client, but gives us the problematic content)
                System.err
                        .println("⚠️ Warning: Could not isolate JSON object ({} braces) in cleaned response. Content: "
                                + jsonCandidate.substring(0, Math.min(100, jsonCandidate.length())) + "...");
                return jsonCandidate;

            } else {
                System.err.println(
                        "❌ Could not find expected 'text' field in Gemini response. Raw Response: " + rawResponse);
                // Fallback structure if extraction fails
                return "{\"score\": \"0.0/9.0\", \"strengths\": \"API response format invalid.\", \"weaknesses\": \"API response format invalid.\", \"suggestions\": \"API response format invalid.\"}";
            }

        } catch (Exception e) {
            System.err.println("❌ JSON parsing error during feedback extraction: " + e.getMessage());
            // Fallback structure on parsing exception
            return "{\"score\": \"0.0/9.0\", \"strengths\": \"Failed to parse response.\", \"weaknesses\": \"Failed to parse response.\", \"suggestions\": \"Failed to parse response.\"}";
        }
    }

    /**
     * Reads the full content from an InputStream into a String.
     */
    private String readStream(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                // Removed .trim() here to ensure proper JSON string concatenation if multiple
                // lines exist
                response.append(responseLine);
            }
            return response.toString();
        }
    }
}
=======
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
>>>>>>> 3166e765e07f8d60d37eb9486f5f60608864602a
