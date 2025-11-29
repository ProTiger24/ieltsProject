package com.myproject.ieltsproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/writing")
public class WritingController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/submit")
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