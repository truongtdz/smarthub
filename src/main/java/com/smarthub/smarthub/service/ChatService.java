package com.smarthub.smarthub.service;

import com.smarthub.smarthub.domain.dto.GoogleAIRequest;
import com.smarthub.smarthub.domain.dto.GoogleAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ChatService {

    @Value("${google.ai.api-key}")
    private String apiKey;

    @Value("${google.ai.api-url}")
    private String apiUrl;

    private final WebClient webClient;

    public ChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String chat(String message) {
        // Tạo request
        GoogleAIRequest.Part part = new GoogleAIRequest.Part();
        part.setText(message);

        GoogleAIRequest.Content content = new GoogleAIRequest.Content();
        content.setParts(List.of(part));

        GoogleAIRequest request = new GoogleAIRequest();
        request.setContents(List.of(content));

        // Call API
        GoogleAIResponse response = webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GoogleAIResponse.class)
                .block();

        // Extract response
        return response.getCandidates().get(0)
                .getContent().getParts().get(0).getText();
    }
}
