package br.com.connectai.api.services;

import br.com.connectai.api.models.dto.KnowledgeBase;
import jakarta.annotation.PostConstruct;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            this.knowledgeBase = createDefaultKnowledgeBase();
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
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
                String currentQuestion = null;
                StringBuilder currentAnswer = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.startsWith("Q:") || line.startsWith("PERGUNTA:")) {
                        if (currentQuestion != null && currentAnswer.length() > 0) {
                            qaPairs.add(new KnowledgeBase.QAPair(
                                    currentQuestion,
                                    currentAnswer.toString().trim(),
                                    extractKeywords(currentQuestion + " " + currentAnswer.toString())
                            ));
                        }
                        currentQuestion = line.replaceFirst("^(Q:|PERGUNTA:)\\s*", "");
                        currentAnswer = new StringBuilder();

                    } else if (line.startsWith("A:") || line.startsWith("RESPOSTA:")) {
                        currentAnswer.append(line.replaceFirst("^(A:|RESPOSTA:)\\s*", ""));

                    } else if (!line.isEmpty() && currentAnswer.length() > 0) {
                        currentAnswer.append(" ").append(line);
                    }
                }

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
                ),
                new KnowledgeBase.QAPair(
                        "Preciso agendar uma consulta",
                        "Você pode agendar uma consulta pelo aplicativo ou entrar em contato via Whatsapp ou Ligação.",
                        Arrays.asList("agendar", "consulta", "aplicativo", "whatsapp", "ligação")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso buscar agenda disponivel",
                        "Você pode buscar agenda disponivel via aplicativo filtrando a cidade, especialidade, médico ou data. Se você ligar ou entrar em contato via Whatsapp, basta informar seus dados de beneficiario e serão informadas as agendas disponiveis.",
                        Arrays.asList("buscar", "agenda", "disponivel", "aplicativo", "whatsapp", "filtro")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso visualizar vagas disponiveis",
                        "Você pode usar o aplicativo para verificar as vagas disponiveis. Se você ligar ou entrar em contato via Whatsapp basta informar os dados para saber das vagas.",
                        Arrays.asList("visualizar", "vagas", "disponiveis", "aplicativo", "whatsapp")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso reservar ou fazer um agendamento",
                        "Pelo aplicativo basta selecionar dia, horario e profissional de preferencia. Se voce ligar ou entrar em contato via Whatsapp, basta informar seus dados e sera informada uma agenda para voce escolher e assim ter seu atendimento confirmado.",
                        Arrays.asList("reservar", "agendamento", "aplicativo", "whatsapp", "profissional")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso confirmar agendamento",
                        "Pelo aplicativo voce pode confirmar os dados ao fim do agendamento da consulta. Se voce ligar ou entrar em contato via whatsapp, ao fim do atendimento seu atendimento será confirmado.",
                        Arrays.asList("confirmar", "agendamento", "aplicativo", "whatsapp")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso atualizar meus dados",
                        "Pode entrar em contato pelo whatsapp e soliticar uma atualização cadastral.",
                        Arrays.asList("atualizar", "dados", "cadastro", "whatsapp")
                ),
                new KnowledgeBase.QAPair(
                        "Quais informações devo enviar para atualizar meus dados",
                        "As informações faltantes ou desatualizadas devem ser enviadas.",
                        Arrays.asList("informações", "atualizar", "dados", "documentos")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso enviar a documentação comprobatoria",
                        "Pelo chat do whatsapp você pode fazer o envio dos documentos.",
                        Arrays.asList("enviar", "documentação", "comprobatoria", "whatsapp")
                ),
                new KnowledgeBase.QAPair(
                        "Como o atendimento pode atualizar os dados",
                        "Pode ser feita a atualização dos dados via CRM.",
                        Arrays.asList("atendimento", "atualizar", "dados", "crm")
                ),
                new KnowledgeBase.QAPair(
                        "Como o atendimento pode informar o beneficiario que os dados estão incorretos",
                        "Via Whatsapp.",
                        Arrays.asList("atendimento", "informar", "dados", "incorretos", "whatsapp")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso relatar uma cobrança que considero indevida?",
                        "Você pode entrar em contato com nosso atendimento para iniciar o processo de verificação da cobrança.",
                        Arrays.asList("relatar", "cobrança", "indevida", "reclamação", "atendimento")
                ),
                new KnowledgeBase.QAPair(
                        "Quais informações preciso fornecer para contestar uma cobrança?",
                        "Para registrar seu atendimento, você precisa nos informar os dados do boleto e qual procedimento você considera incorreto.",
                        Arrays.asList("informações", "contestar", "cobrança", "boleto", "procedimento")
                ),
                new KnowledgeBase.QAPair(
                        "Receberei um número de protocolo para minha reclamação?",
                        "Sim, ao registrar a solicitação, nosso atendente irá fornecer um número de protocolo para seu acompanhamento, seja verbalmente ou por texto.",
                        Arrays.asList("protocolo", "reclamação", "número", "acompanhamento")
                ),
                new KnowledgeBase.QAPair(
                        "O que acontece depois que eu faço a reclamação?",
                        "Após o registro, sua solicitação é enviada para análise interna do setor de contas médicas. É necessário aguardar o retorno desta análise para prosseguir.",
                        Arrays.asList("reclamação", "análise", "contas médicas", "processo")
                ),
                new KnowledgeBase.QAPair(
                        "E se a análise mostrar que a cobrança está correta?",
                        "Caso a cobrança seja devida, nosso atendimento enviará a você o comprovante da realização do procedimento por e-mail ou outro meio de contato.",
                        Arrays.asList("análise", "cobrança", "correta", "comprovante", "procedimento")
                ),
                new KnowledgeBase.QAPair(
                        "O que acontece se for confirmado que a cobrança foi errada?",
                        "Se a cobrança for confirmada como indevida, nosso atendimento entrará em contato para verificar com você a melhor forma de aplicar o desconto.",
                        Arrays.asList("cobrança", "indevida", "confirmada", "desconto")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso receber o valor da cobrança indevida?",
                        "Você pode escolher entre ter o desconto aplicado no boleto atual ou receber um crédito para uma cobrança futura.",
                        Arrays.asList("receber", "cobrança", "indevida", "desconto", "crédito")
                ),
                new KnowledgeBase.QAPair(
                        "Como faço para ter o desconto na minha fatura atual?",
                        "Se você optar pelo desconto no boleto atual, nosso atendimento informará diretamente ao setor financeiro para que a correção seja feita.",
                        Arrays.asList("desconto", "fatura", "atual", "financeiro")
                ),
                new KnowledgeBase.QAPair(
                        "Como faço para usar o valor como crédito no futuro?",
                        "Para receber o valor como crédito em uma fatura futura, o procedimento é solicitar presencialmente na tesouraria.",
                        Arrays.asList("crédito", "futuro", "tesouraria", "presencial")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso fazer minha solicitação/cadastro?",
                        "Este é um processo que deve ser realizado exclusivamente de forma presencial em uma de nossas unidades de atendimento.",
                        Arrays.asList("solicitação", "cadastro", "presencial", "unidade")
                ),
                new KnowledgeBase.QAPair(
                        "Quais documentos eu preciso levar para o atendimento?",
                        "É necessário apresentar seu RG e CPF, além de um comprovante de matrícula da faculdade e um comprovante de frequência.",
                        Arrays.asList("documentos", "rg", "cpf", "matrícula", "frequência")
                ),
                new KnowledgeBase.QAPair(
                        "O que acontece quando eu chegar para o atendimento?",
                        "Nossa equipe irá solicitar seus documentos, tirar uma cópia, registrar o atendimento em nosso sistema e, ao final, entregar a você uma via física do protocolo.",
                        Arrays.asList("atendimento", "documentos", "protocolo", "sistema")
                ),
                new KnowledgeBase.QAPair(
                        "O meu cadastro é finalizado na hora?",
                        "Não. Após o registro inicial, sua documentação é encaminhada internamente para o setor de cadastro para ser validada.",
                        Arrays.asList("cadastro", "finalizado", "validação", "documentação")
                ),
                new KnowledgeBase.QAPair(
                        "O que acontece se houver algum problema com meus documentos?",
                        "Se o setor de cadastro identificar que algum documento está incorreto ou faltando, ele notificará nossa equipe de atendimento para que o contato seja feito com você.",
                        Arrays.asList("problema", "documentos", "incorreto", "faltando", "contato")
                ),
                new KnowledgeBase.QAPair(
                        "Como serei informado se precisar corrigir minha documentação?",
                        "Caso haja necessidade de correção, nossa equipe de atendimento entrará em contato com você por telefone ou por nosso canal de comunicação para solicitar os documentos corretos.",
                        Arrays.asList("informado", "corrigir", "documentação", "contato", "telefone")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso emitir a segunda via do meu boleto?",
                        "Você pode emitir a segunda via do boleto diretamente em nossa plataforma, seja pelo site ou pelo aplicativo.",
                        Arrays.asList("emitir", "segunda via", "boleto", "plataforma", "site", "aplicativo")
                ),
                new KnowledgeBase.QAPair(
                        "Qual o passo a passo para imprimir meu boleto pela plataforma?",
                        "Primeiro, acesse o site ou aplicativo e faça seu login. Em seguida, navegue até o menu financeiro. Depois, selecione o mês (competência) do boleto que você deseja e clique na opção para impressão da 2ª via.",
                        Arrays.asList("passo a passo", "imprimir", "boleto", "plataforma", "menu financeiro")
                ),
                new KnowledgeBase.QAPair(
                        "Onde encontro meus boletos e faturas?",
                        "Todos os seus boletos ficam disponíveis na área do 'menu financeiro' do nosso site ou aplicativo.",
                        Arrays.asList("encontrar", "boletos", "faturas", "menu financeiro")
                ),
                new KnowledgeBase.QAPair(
                        "Como seleciono o boleto do mês que eu quero pagar?",
                        "Dentro do menu financeiro, basta selecionar a competência (o mês de referência) que você deseja para visualizar e imprimir o boleto correspondente.",
                        Arrays.asList("selecionar", "boleto", "mês", "competência", "pagar")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso solicitar autorização para um exame ou procedimento médico?",
                        "Você pode iniciar a solicitação de autorização entrando em contato via Whatsapp ou presencialmente em uma de nossas unidades.",
                        Arrays.asList("solicitar", "autorização", "exame", "procedimento", "whatsapp", "presencial")
                ),
                new KnowledgeBase.QAPair(
                        "Quais informações preciso enviar para solicitar uma autorização?",
                        "É necessário fornecer seu nome completo, data de nascimento e enviar uma foto do pedido médico.",
                        Arrays.asList("informações", "autorização", "nome", "data nascimento", "pedido médico")
                ),
                new KnowledgeBase.QAPair(
                        "O que acontece depois que eu envio os documentos?",
                        "Nossa equipe de atendimento irá registrar sua solicitação em nosso sistema. Se o procedimento exigir uma análise mais aprofundada (auditoria), você será informado.",
                        Arrays.asList("documentos", "registro", "sistema", "auditoria", "análise")
                ),
                new KnowledgeBase.QAPair(
                        "Qual o prazo para ter a autorização liberada?",
                        "Se o seu pedido for para auditoria, o prazo de retorno é de 3 dias úteis para procedimentos comuns e 10 dias úteis para casos que envolvem OPME (Órteses, Próteses e Materiais Especiais).",
                        Arrays.asList("prazo", "autorização", "liberada", "auditoria", "opme")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso acompanhar o andamento da minha solicitação?",
                        "Você pode monitorar o status da sua guia de autorização pelo nosso aplicativo, por ligação ou via Whatsapp.",
                        Arrays.asList("acompanhar", "andamento", "solicitação", "aplicativo", "whatsapp")
                ),
                new KnowledgeBase.QAPair(
                        "Como sei se o procedimento foi autorizado?",
                        "Ao final da análise, nossa equipe informará o resultado. Se for aprovado, você receberá o número do protocolo e o número da guia de autorização.",
                        Arrays.asList("procedimento", "autorizado", "protocolo", "guia autorização")
                ),
                new KnowledgeBase.QAPair(
                        "O que acontece se meu pedido de autorização for negado?",
                        "Caso o procedimento não tenha cobertura e seja negado, nossa equipe de atendimento entrará em contato para informar o número do protocolo e o motivo da negativa.",
                        Arrays.asList("autorização", "negado", "protocolo", "motivo", "cobertura")
                ),
                new KnowledgeBase.QAPair(
                        "Como posso solicitar a troca de titularidade do plano?",
                        "A troca de titularidade é um processo realizado exclusivamente de forma presencial em uma de nossas unidades.",
                        Arrays.asList("solicitar", "troca", "titularidade", "plano", "presencial")
                ),
                new KnowledgeBase.QAPair(
                        "Quem precisa comparecer ao local para fazer a troca?",
                        "É necessária a presença tanto do titular atual quanto do futuro titular. Em caso de falecimento do titular atual, apenas o novo responsável precisa comparecer com a documentação necessária.",
                        Arrays.asList("comparecer", "local", "troca", "titular", "falecimento")
                ),
                new KnowledgeBase.QAPair(
                        "Quais documentos são necessários para a troca de titularidade?",
                        "É preciso apresentar RG, CPF e comprovante de endereço do novo titular. Se o titular atual for falecido, é necessário trazer também a certidão de óbito.",
                        Arrays.asList("documentos", "titularidade", "rg", "cpf", "endereço", "certidão óbito")
                ),
                new KnowledgeBase.QAPair(
                        "Qual o procedimento feito no atendimento presencial?",
                        "No local, nossa equipe irá imprimir um documento padrão que deverá ser assinado. Em seguida, faremos cópias dos documentos, registraremos o atendimento e forneceremos a você uma via física do protocolo.",
                        Arrays.asList("procedimento", "atendimento", "presencial", "documento", "protocolo")
                ),
                new KnowledgeBase.QAPair(
                        "A troca de titular é concluída na mesma hora?",
                        "Não. Após o atendimento inicial, sua documentação é encaminhada internamente para o setor de cadastro para ser validada. Este processo é feito uma vez ao dia.",
                        Arrays.asList("troca", "titular", "concluída", "validação", "cadastro")
                ),
                new KnowledgeBase.QAPair(
                        "Como serei informado se houver algum problema com os documentos?",
                        "Caso o setor de cadastro identifique alguma pendência ou erro, nossa equipe de atendimento entrará em contato com você por telefone ou outro canal de comunicação para solicitar a correção.",
                        Arrays.asList("informado", "problema", "documentos", "contato", "correção")
                )
        );

        return new KnowledgeBase(defaultPairs);
    }

    private List<String> extractKeywords(String text) {
        return Arrays.stream(text.toLowerCase()
                        //.replaceAll("[^a-záàâãéêíóôõúçñ\\s]", "")
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
            qaPair = new KnowledgeBase.QAPair(
                    qaPair.getQuestion().toLowerCase(),
                    qaPair.getAnswer().toLowerCase(),
                    extractKeywords(qaPair.getQuestion() + " " + qaPair.getAnswer())
            );
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
                    keywordSimilarity = keywordSimilarity + 0.3;
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

