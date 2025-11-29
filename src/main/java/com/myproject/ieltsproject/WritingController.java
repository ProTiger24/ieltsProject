package com.myproject.ieltsproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
import java.util.Map;
=======

import java.util.*;
>>>>>>> 3166e765e07f8d60d37eb9486f5f60608864602a

@RestController
@RequestMapping("/api/writing")
public class WritingController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/submit")
<<<<<<< HEAD
    public ResponseEntity<String> submitWriting(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");

            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"No text provided\"}");
            }

            System.out.println("🎯 ========== WRITING CONTROLLER CALLED ==========");
            System.out.println("📝 User Text: " + text);

            // ✅ GeminiService call করুন
            String geminiResponse = geminiService.submitWriting(text);

            System.out.println("✅ Writing evaluation completed successfully");
            return ResponseEntity.ok(geminiResponse);

        } catch (Exception e) {
            System.err.println("❌ Error in WritingController: " + e.getMessage());
            e.printStackTrace();

            // Real error return করুন
            return ResponseEntity.status(500)
                    .body("{\"score\": \"0.0\", \"strengths\": \"Error\", \"weaknesses\": \"" +
                            e.getMessage().replace("\"", "'") + "\", \"suggestions\": \"Please try again\"}");
        }
    }
}
=======
    public ResponseEntity<Map<String, Object>> submitWriting(@RequestBody Map<String, String> request) {
        String text = request.get("text").trim();

        try {

            String feedbackText = geminiService.getCompletion(text);

            if (feedbackText.startsWith("⚠️") || feedbackText.startsWith("Gemini API Error")
                    || feedbackText.startsWith("Please configure")) {
                return getFallbackFeedback(text);
            }

            int score = extractScore(feedbackText); // feedback এর মধ্যে যদি "Overall Score: X/9" থাকে

            Map<String, Object> response = new HashMap<>();
            response.put("feedback", feedbackText);
            response.put("score", score);
            response.put("status", "success");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ GeminiService failed: " + e.getMessage());
            return getFallbackFeedback(text);
        }
    }

    private int extractScore(String feedback) {
        try {
            if (feedback.contains("Overall Score:")) {
                String scorePart = feedback.split("Overall Score:")[1].split("/")[0].trim();
                return Integer.parseInt(scorePart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 5; // default score
    }

    private ResponseEntity<Map<String, Object>> getFallbackFeedback(String text) {
        // Simple fallback: sentence-wise punctuation + capitalization
        String[] sentences = text.split("(?<=[.!?])");
        List<String> suggestions = new ArrayList<>();
        List<String> correctedSentences = new ArrayList<>();

        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty())
                continue;

            String original = sentence;
            String corrected = sentence;

            // Capitalize first letter
            corrected = corrected.substring(0, 1).toUpperCase() + corrected.substring(1);

            // Add period if missing
            if (!corrected.endsWith(".") && !corrected.endsWith("!") && !corrected.endsWith("?")) {
                corrected += ".";
                suggestions.add("Add punctuation: \"" + original + "\" → \"" + corrected + "\"");
            }

            if (!corrected.equals(original)) {
                suggestions.add("Corrected sentence: \"" + original + "\" → \"" + corrected + "\"");
            }

            correctedSentences.add(corrected);
        }

        int wordCount = text.split("\\s+").length;
        int score = 5;
        if (wordCount >= 20 && suggestions.isEmpty())
            score = 8;
        else if (wordCount >= 10 && suggestions.size() <= 2)
            score = 6;
        else if (wordCount < 5)
            score = 4;

        StringBuilder feedback = new StringBuilder();
        feedback.append("Mistakes & Suggestions: ").append(suggestions.isEmpty() ? "None" : suggestions).append("\n");
        feedback.append("Corrected Text: ").append(String.join(" ", correctedSentences)).append("\n");
        feedback.append("Overall Score: ").append(score).append("/9");

        Map<String, Object> response = new HashMap<>();
        response.put("feedback", feedback.toString());
        response.put("score", score);
        response.put("status", "fallback");

        return ResponseEntity.ok(response);
    }
}
>>>>>>> 3166e765e07f8d60d37eb9486f5f60608864602a
