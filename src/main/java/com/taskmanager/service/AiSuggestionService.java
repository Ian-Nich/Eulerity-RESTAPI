package com.taskmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.SuggestRequest;
import com.taskmanager.dto.SuggestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AiSuggestionService {

    @Value("${anthropic.api.key:not-configured}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SuggestResponse suggest(SuggestRequest request) {
        String prompt = buildPrompt(request);
        String rawResponse = callClaude(prompt);
        return parseResponse(rawResponse);
    }

    private String buildPrompt(SuggestRequest request) {
        return String.format("""
                You are a task management assistant. Given a task title and optional description,
                suggest appropriate values for priority, due date, and status.

                Task title: %s
                Task description: %s

                Respond with ONLY a JSON object in this exact format, no other text:
                {
                  "suggestedPriority": "LOW|MEDIUM|HIGH",
                  "suggestedDueDate": "YYYY-MM-DD",
                  "suggestedStatus": "TODO|IN_PROGRESS|DONE",
                  "explanation": "brief explanation of your suggestions"
                }

                Today's date is %s. If no due date is apparent, suggest one week from today.
                """,
                request.getTitle(),
                request.getDescription() != null ? request.getDescription() : "none provided",
                java.time.LocalDate.now().toString()
        );
    }

    String callClaude(String prompt) {
        try {
            String body = objectMapper.writeValueAsString(new ClaudeRequest(prompt));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());
            return root.path("content").get(0).path("text").asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to call Claude API: " + e.getMessage(), e);
        }
    }

    private SuggestResponse parseResponse(String raw) {
        try {
            String cleaned = raw.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
            }
            return objectMapper.readValue(cleaned, SuggestResponse.class);
        } catch (Exception e) {
            return new SuggestResponse("MEDIUM",
                    java.time.LocalDate.now().plusWeeks(1).toString(),
                    "TODO",
                    "Could not parse AI response; defaults applied.");
        }
    }

    // Inner class for request serialization
    private static class ClaudeRequest {
        public String model = "claude-sonnet-4-20250514";
        public int max_tokens = 512;
        public Message[] messages;

        ClaudeRequest(String prompt) {
            this.messages = new Message[]{ new Message(prompt) };
        }

        static class Message {
            public String role = "user";
            public String content;
            Message(String content) { this.content = content; }
        }
    }
}