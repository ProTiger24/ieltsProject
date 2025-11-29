package com.myproject.ieltsproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/speaking")
public class SpeakingController {

    @Autowired
    private GeminiService geminiService;

    // ✅ Text input endpoint
    @PostMapping("/submit-text")
    public ResponseEntity<String> submitSpeakingText(@RequestBody Map<String, String> request) {
        try {
            String spokenText = request.get("text");

            if (spokenText == null || spokenText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"No text provided\"}");
            }

            System.out.println("🎯 ========== SPEAKING TEXT CONTROLLER CALLED ==========");
            System.out.println("🎤 User Spoken Text: " + spokenText);

            // ✅ GeminiService call করুন
            String result = geminiService.submitSpeaking(spokenText);

            System.out.println("✅ Speaking text evaluation completed successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("❌ Error in submitSpeakingText: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(500)
                    .body("{\"score\": \"0.0\", \"strengths\": \"Error\", \"weaknesses\": \"" +
                            e.getMessage().replace("\"", "'") + "\", \"suggestions\": \"Please try again\"}");
        }
    }

    // ✅ Audio file এর জন্য endpoint
    @PostMapping("/submit-audio")
    public ResponseEntity<String> submitSpeakingAudio(@RequestParam("audioFile") MultipartFile audioFile) {
        System.out.println("🎯 ========== SPEAKING AUDIO CONTROLLER CALLED ==========");

        if (audioFile.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Audio file is empty\"}");
        }

        try {
            byte[] audioBytes = audioFile.getBytes();
            String fileName = audioFile.getOriginalFilename();

            System.out.println("🎤 Processing audio file: " + fileName);

            // ✅ GeminiService call করুন
            String result = geminiService.processSpeakingAudio(audioBytes, fileName);

            System.out.println("✅ Speaking audio evaluation completed successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("❌ Exception in SpeakingController: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(500)
                    .body("{\"score\": \"0.0\", \"strengths\": \"Error\", \"weaknesses\": \"" +
                            e.getMessage().replace("\"", "'") + "\", \"suggestions\": \"Please try again\"}");
        }
    }
}