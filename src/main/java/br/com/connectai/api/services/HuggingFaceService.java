package br.com.connectai.api.services;

import br.com.connectai.api.models.dto.HuggingFaceRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;



@Service
public class HuggingFaceService {

    private final WebClient webClient;

    @Value("${huggingface.api.key:}")
    private String apiKey;

    @Value("${huggingface.model:microsoft/DialoGPT-medium}")
    private String modelName;

    public HuggingFaceService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api-inference.huggingface.co")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> generateResponse(String prompt) {
        String endpoint = "/models/" + modelName;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("max_length", 150);
        parameters.put("temperature", 0.7);
        parameters.put("do_sample", true);
        parameters.put("pad_token_id", 50256);

        HuggingFaceRequest request = new HuggingFaceRequest(prompt, parameters);

        WebClient.RequestHeadersSpec<?> requestSpec = webClient
                .post()
                .uri(endpoint)
                .bodyValue(request);

        if (apiKey != null && !apiKey.isEmpty()) {
            requestSpec = requestSpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        }

        return requestSpec
                .retrieve()
                .bodyToMono(List.class)
                .timeout(Duration.ofSeconds(30))
                .map(this::extractGeneratedText)
                .onErrorReturn("Desculpe, não consegui processar sua pergunta no momento. Tente novamente mais tarde.");
    }

    private String extractGeneratedText(List<?> response) {
        if (response != null && !response.isEmpty()) {
            Object firstItem = response.get(0);
            if (firstItem instanceof Map<?,?>) {
                Map<?, ?> responseMap = (Map<?, ?>) firstItem;
                Object generatedText = responseMap.get("generated_text");
                if (generatedText != null) {
                    String text = generatedText.toString();
                    // Remove o prompt original da resposta
                    return cleanGeneratedText(text);
                }
            }
        }
        return "Não foi possível gerar uma resposta adequada.";
    }

    private String cleanGeneratedText(String text) {
        // Remove quebras de linha excessivas e limpa a resposta
        return text.replaceAll("\\n+", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}

