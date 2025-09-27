package br.com.connectai.api.controllers;

import br.com.connectai.api.models.dto.ChatRequest;
import br.com.connectai.api.models.dto.ChatResponse;
import br.com.connectai.api.services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {
    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    public Mono<ResponseEntity<ChatResponse>> sendMessage(@RequestBody ChatRequest request) {
        long startTime = System.currentTimeMillis();

        String conversationId = request.getConversationId() != null ?
                request.getConversationId() : chatbotService.generateConversationId();

        return chatbotService.processMessage(request.getMessage(), conversationId)
                .map(response -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    ChatResponse chatResponse = new ChatResponse(
                            response,
                            conversationId,
                            0.85, // Confidence placeholder
                            responseTime
                    );
                    return ResponseEntity.ok(chatResponse);
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot está funcionando!");
    }
}

