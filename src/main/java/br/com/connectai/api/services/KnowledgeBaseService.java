package br.com.connectai.api.services;

import br.com.connectai.api.models.dto.KnowledgeBase;
import jakarta.annotation.PostConstruct;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseService {

    private KnowledgeBase knowledgeBase;
    private JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    @PostConstruct
    public void loadKnowledgeBase() {
        try {
            this.knowledgeBase = loadFromFile("base/knowledge_base.txt");
        } catch (Exception e) {
            // Fallback para conhecimento padrão se arquivo não existir
            this.knowledgeBase = createDefaultKnowledgeBase();
        }
    }

    private KnowledgeBase loadFromFile(String filePath) throws IOException {
        List<KnowledgeBase.QAPair> qaPairs = new ArrayList<>();

        try {
            File file = ResourceUtils.getFile(filePath);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                String currentQuestion = null;
                StringBuilder currentAnswer = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.startsWith("Q:") || line.startsWith("PERGUNTA:")) {
                        // Salva Q&A anterior se existir
                        if (currentQuestion != null && currentAnswer.length() > 0) {
                            qaPairs.add(new KnowledgeBase.QAPair(
                                    currentQuestion,
                                    currentAnswer.toString().trim(),
                                    extractKeywords(currentQuestion + " " + currentAnswer.toString())
                            ));
                        }

                        // Nova pergunta
                        currentQuestion = line.replaceFirst("^(Q:|PERGUNTA:)\\s*", "");
                        currentAnswer = new StringBuilder();

                    } else if (line.startsWith("A:") || line.startsWith("RESPOSTA:")) {
                        currentAnswer.append(line.replaceFirst("^(A:|RESPOSTA:)\\s*", ""));

                    } else if (!line.isEmpty() && currentAnswer.length() > 0) {
                        currentAnswer.append(" ").append(line);
                    }
                }

                // Adiciona último Q&A
                if (currentQuestion != null && currentAnswer.length() > 0) {
                    qaPairs.add(new KnowledgeBase.QAPair(
                            currentQuestion,
                            currentAnswer.toString().trim(),
                            extractKeywords(currentQuestion + " " + currentAnswer.toString())
                    ));
                }
            }
        } catch (Exception e) {
            throw new IOException("Erro ao carregar base de conhecimento: " + e.getMessage());
        }

        return new KnowledgeBase(qaPairs);
    }

    private KnowledgeBase createDefaultKnowledgeBase() {
        List<KnowledgeBase.QAPair> defaultPairs = Arrays.asList(
                new KnowledgeBase.QAPair(
                        "Como posso fazer um pedido?",
                        "Para fazer um pedido, acesse nosso site, escolha os produtos desejados e finalize no carrinho. Você também pode ligar para nosso atendimento.",
                        Arrays.asList("pedido", "compra", "carrinho", "site", "atendimento")
                ),
                new KnowledgeBase.QAPair(
                        "Quais são as formas de pagamento?",
                        "Aceitamos cartão de crédito, débito, PIX e boleto bancário. Para compras online, também oferecemos parcelamento em até 12x.",
                        Arrays.asList("pagamento", "cartão", "pix", "boleto", "parcelamento")
                ),
                new KnowledgeBase.QAPair(
                        "Quanto tempo demora a entrega?",
                        "O prazo de entrega varia conforme sua região: Capital: 2-3 dias úteis, Interior: 5-7 dias úteis. Entregas expressas disponíveis.",
                        Arrays.asList("entrega", "prazo", "tempo", "frete", "envio")
                )
        );

        return new KnowledgeBase(defaultPairs);
    }

    private List<String> extractKeywords(String text) {
        return Arrays.stream(text.toLowerCase()
                        .replaceAll("[^a-záàâãéêíóôõúçñ\\s]", "")
                        .split("\\s+"))
                .filter(word -> word.length() > 2)
                .collect(Collectors.toList());
    }

    public KnowledgeBase.QAPair findMostSimilarQA(String userQuestion) {
        if (knowledgeBase.getQaPairs().isEmpty()) {
            return null;
        }

        double maxSimilarity = 0.0;
        KnowledgeBase.QAPair bestMatch = null;

        String normalizedUserQuestion = userQuestion.toLowerCase();

        for (KnowledgeBase.QAPair qaPair : knowledgeBase.getQaPairs()) {
            // Similaridade com a pergunta
            double questionSimilarity = similarity.apply(
                    normalizedUserQuestion,
                    qaPair.getQuestion().toLowerCase()
            );

            // Similaridade com palavras-chave
            double keywordSimilarity = 0.0;
            for (String keyword : qaPair.getKeywords()) {
                if (normalizedUserQuestion.contains(keyword)) {
                    keywordSimilarity += 0.3;
                }
            }

            double totalSimilarity = questionSimilarity + Math.min(keywordSimilarity, 0.5);

            if (totalSimilarity > maxSimilarity) {
                maxSimilarity = totalSimilarity;
                bestMatch = qaPair;
            }
        }

        // Retorna apenas se a similaridade for razoável
        return maxSimilarity > 0.4 ? bestMatch : null;
    }

    public String getContextForGeneration(String userQuestion) {
        StringBuilder context = new StringBuilder();
        context.append("Base de conhecimento da empresa:\n\n");

        // Adiciona as perguntas e respostas mais relevantes
        knowledgeBase.getQaPairs().forEach(qa -> {
            context.append("P: ").append(qa.getQuestion()).append("\n");
            context.append("R: ").append(qa.getAnswer()).append("\n\n");
        });

        context.append("Pergunta do cliente: ").append(userQuestion).append("\n");
        context.append("Resposta baseada no conhecimento acima:");

        return context.toString();
    }
}

