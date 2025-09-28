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
        // Verifica se a pergunta é relacionada à saúde
        if (!knowledgeBaseService.isHealthRelatedQuestion(userMessage)) {
            return Mono.just("Olá! Sou um assistente médico especializado e estou aqui para ajudar com questões relacionadas à saúde. " +
                    "Para outros assuntos, recomendo que consulte um especialista na área apropriada. " +
                    "Como posso ajudá-lo com alguma dúvida sobre saúde?");
        }

        // Primeiro, tenta encontrar resposta na base de conhecimento médico
        KnowledgeBase.QAPair exactMatch = knowledgeBaseService.findMostSimilarQA(userMessage);

        if (exactMatch != null) {
            // Se encontrou uma correspondência boa, usa a resposta da base
            return Mono.just(exactMatch.getAnswer());
        } else {
            System.out.println("Não encontrou correspondência exata na base de conhecimento. Usando IA generativa.");
            // Se não encontrou, usa IA generativa com contexto médico
            String context = knowledgeBaseService.getContextForGeneration(userMessage);
            return huggingFaceService.generateResponse(context)
                    .map(response -> postProcessMedicalResponse(response, userMessage));
        }
    }

    private String postProcessMedicalResponse(String response, String originalQuestion) {
        // Pós-processamento específico para respostas médicas
        if (response.length() > 500) {
            response = response.substring(0, 497) + "...";
        }

        // Garante que a resposta seja útil e contenha aviso médico
        if (response.trim().isEmpty() || response.length() < 10) {
            return "Desculpe, não tenho informações específicas sobre sua consulta. " +
                    "Recomendo fortemente que consulte um médico para uma avaliação adequada. " +
                    "Posso ajudar com outras dúvidas gerais sobre saúde?";
        }

        // Adiciona aviso médico se a resposta não contém
        if (!response.toLowerCase().contains("médico") &&
                !response.toLowerCase().contains("consulte") &&
                !response.toLowerCase().contains("profissional")) {

            response += " Importante: Esta é apenas uma orientação geral. " +
                    "Para um diagnóstico preciso e tratamento adequado, consulte sempre um médico.";
        }

        return response;
    }

    public String generateConversationId() {
        return UUID.randomUUID().toString();
    }
}

