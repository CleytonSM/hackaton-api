package br.com.connectai.api.services;

import br.com.connectai.api.models.dto.KnowledgeBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ChatbotService {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private HuggingFaceService huggingFaceService;

    public Mono<String> processMessage(String userMessage, String conversationId) {
        // Primeiro, tenta encontrar resposta na base de conhecimento
        KnowledgeBase.QAPair exactMatch = knowledgeBaseService.findMostSimilarQA(userMessage);

        if (exactMatch != null) {
            // Se encontrou uma correspondência boa, usa a resposta da base
            return Mono.just(exactMatch.getAnswer());
        } else {
            // Se não encontrou, usa IA generativa com contexto
            String context = knowledgeBaseService.getContextForGeneration(userMessage);
            return huggingFaceService.generateResponse(context)
                    .map(this::postProcessResponse);
        }
    }

    private String postProcessResponse(String response) {
        // Pós-processamento da resposta gerada
        if (response.length() > 500) {
            response = response.substring(0, 497) + "...";
        }

        // Garante que a resposta seja útil e educada
        if (response.trim().isEmpty() || response.length() < 10) {
            return "Desculpe, não tenho uma resposta específica para sua pergunta. " +
                    "Pode reformular ou entrar em contato com nosso atendimento para mais informações?";
        }

        return response;
    }

    public String generateConversationId() {
        return UUID.randomUUID().toString();
    }
}

