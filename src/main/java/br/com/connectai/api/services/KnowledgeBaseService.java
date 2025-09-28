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
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    @PostConstruct
    public void loadKnowledgeBase() {
        try {
            this.knowledgeBase = loadFromFile("base/knowledge_base.txt");
        } catch (Exception e) {
            // Fallback para conhecimento padrão se arquivo não existir
            this.knowledgeBase = createDefaultKnowledgeBase();
        }
        System.out.println("Knowledge base carregada com " + knowledgeBase.getQaPairs().size() + " pares de Q&A.");
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
            System.out.println("knowledgeBase está vazio");
            return null;
        }

        double maxSimilarity = 0.0;
        KnowledgeBase.QAPair bestMatch = null;

        String normalizedUserQuestion = userQuestion.toLowerCase();
        boolean isDone = false;
        for (KnowledgeBase.QAPair qaPair : knowledgeBase.getQaPairs()) {
            System.out.println(qaPair.getQuestion());
            System.out.println(qaPair.getAnswer());
            // Similaridade com a pergunta
            double questionSimilarity = similarity.apply(
                    normalizedUserQuestion,
                    qaPair.getQuestion().toLowerCase()
            );

            // Similaridade com palavras-chave
            double keywordSimilarity = 0.0;
            for (String keyword : qaPair.getKeywords()) {
                System.out.println(normalizedUserQuestion);
                System.out.println(keyword);
                System.out.println(normalizedUserQuestion.contains(keyword));
                if (normalizedUserQuestion.contains(keyword)) {
                    keywordSimilarity += 0.3;
                }
            }

            double totalSimilarity = questionSimilarity + keywordSimilarity;
            System.out.println("totalSimilarity" + totalSimilarity);
            boolean x = totalSimilarity > maxSimilarity && keywordSimilarity > 0.8;
            System.out.println("totalSimilarity > maxSimilarity && keywordSimilarity > 0.8: ");
            System.out.println(x);
            System.out.println("keywordSimilarity: " + keywordSimilarity);
            System.out.println("maxSimilarity: " + maxSimilarity);
            if (totalSimilarity > maxSimilarity && keywordSimilarity > 0.8) {
                maxSimilarity = totalSimilarity;
                bestMatch = qaPair;
                isDone = true;
            }
        }

        if(!isDone) {
            System.out.println("isDone está vazio");

            return null;
        }

        // Retorna apenas se a similaridade for razoável
        return maxSimilarity > 0.4 ? bestMatch : null;
    }

    public String getContextForGeneration(String userQuestion) {
        StringBuilder context = new StringBuilder();

        // Contexto do agente médico
        context.append("Você é um agente médico especializado e está aqui para ajudar usuários a sanarem dúvidas relacionadas à área da saúde. ");
        context.append("Suas responsabilidades incluem:\n");
        context.append("- Responder apenas perguntas relacionadas à saúde, medicina, sintomas, tratamentos e bem-estar\n");
        context.append("- Fornecer informações educativas e orientações gerais de saúde\n");
        context.append("- Sempre recomendar consulta médica para diagnósticos e tratamentos específicos\n");
        context.append("- Não responder perguntas sobre outros assuntos não relacionados à saúde\n");
        context.append("- Ser empático, profissional e responsável nas respostas\n\n");

        context.append("Diretrizes importantes:\n");
        context.append("- Para perguntas não relacionadas à saúde, responda educadamente que você é um assistente médico especializado\n");
        context.append("- Nunca forneça diagnósticos definitivos ou receite medicamentos\n");
        context.append("- Sempre recomendar consulta médica para diagnósticos e tratamentos específicosSempre enfatize a importância da consulta médica profissional\n");
        context.append("- Use linguagem clara e acessível, evitando termos técnicos excessivos\n");
        context.append("- Seja sensível a questões de saúde mental e bem-estar emocional\n");
        context.append("- Em caso de emergências médicas, oriente a procurar atendimento imediato\n\n");

        context.append("IMPORTANTE: Para perguntas não relacionadas à saúde, responda educadamente que você é um assistente médico e só pode ajudar com questões de saúde.\n\n");

        context.append("Base de conhecimento médico:\n\n");

        // Adiciona as perguntas e respostas mais relevantes
        knowledgeBase.getQaPairs().forEach(qa -> {
            context.append("Q: ").append(qa.getQuestion()).append("\n");
            context.append("A: ").append(qa.getAnswer()).append("\n\n");
        });

        context.append("Pergunta do usuário: ").append(userQuestion).append("\n");
        context.append("Resposta profissional como agente médico:");

        return context.toString();
    }

    public boolean isHealthRelatedQuestion(String question) {
        String normalizedQuestion = question.toLowerCase();

        // Palavras-chave relacionadas à saúde
        String[] healthKeywords = {
                "saúde", "médico", "medicina", "doença", "sintoma", "tratamento", "remedio", "remédio",
                "dor", "febre", "hospital", "clínica", "exame", "diagnóstico", "cirurgia", "medicamento",
                "enfermagem", "paciente", "consulta", "pressão", "diabetes", "coração", "pulmão",
                "estômago", "cabeça", "gripe", "resfriado", "alergia", "infecção", "vitamina",
                "dieta", "alimentação", "exercício", "sono", "stress", "ansiedade", "depressão",
                "gravidez", "criança", "idoso", "vacinação", "vacina", "prevenção", "cuidado"
        };

        // Palavras que indicam perguntas não médicas
        String[] nonHealthKeywords = {
                "futebol", "música", "filme", "política", "economia", "matemática", "história",
                "geografia", "tecnologia", "programação", "carro", "viagem", "receita culinária",
                "jogo", "esporte", "moda", "beleza", "clima", "tempo", "notícia"
        };

        // Verifica se contém palavras-chave de saúde
        for (String keyword : healthKeywords) {
            if (normalizedQuestion.contains(keyword)) {
                return true;
            }
        }

        // Verifica se contém palavras claramente não médicas
        for (String keyword : nonHealthKeywords) {
            if (normalizedQuestion.contains(keyword)) {
                return false;
            }
        }

        // Se não identificou claramente, assume que pode ser relacionado à saúde
        // (melhor errar dando uma resposta educativa do que ignorar uma pergunta médica válida)
        return true;
    }
}

