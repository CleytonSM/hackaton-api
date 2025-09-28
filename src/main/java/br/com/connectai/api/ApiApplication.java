package br.com.connectai.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		String conteudo = """
Q:Preciso agendar uma consulta
A:Você pode agendar uma consulta pelo aplicativo ou entrar em contato via Whatsapp ou Ligação

Q:Como posso buscar agenda disponivel
A:Voce pode buscar agenda disponivel via aplicativo filtrando a cidade, especialidade, médico ou data. Se você ligar ou entrar em contato via Whatsapp, basta informar seus dados de beneficiario e serão informadas as agendas disponiveis

Q:Como posso visualizar vagas disponiveis
A:Você pode usar o aplicativo para verificar as vagas disponiveis. Se você ligar ou entrar em contato via Whatsapp basta informar os dados para saber das vagas

Q:Como posso reservar ou fazer um agendamento
A:Pelo aplicativo basta selecionar dia, horario e profissional de preferencia. Se voce ligar ou entrar em contato via Whatsapp, basta informar seus dados e sera informada uma agenda para voce escolher
e assim ter seu atendimento confirmado

Q:Como posso confirmar agendamento
A:Pelo aplicativo voce pode confirmar os dados ao fim do agendamento da consulta. Se voce ligar ou entrar em contato via whatsapp, ao fim do atendimento seu atendimento será confirmado

Q:Como posso atualizar meus dados
A:Pode entrar em contato pelo whatsapp e soliticar uma atualização cadastral

Q:Quais informações devo enviar para atualizar meus dados
A:As informações faltantes ou desatualizadas devem ser enviadas

Q:Como posso enviar a documentação comprobatoria
A:Pelo chat do whatsapp você pode fazer o envio dos documentos

Q:Como o atendimento pode atualizar os dados:
A:Pode ser feita a atualização dos dados via CRM

Q:Como  o atendimento pode informar o beneficiario que os dados estão incorretos:
A:Via Whatsapp

Q:Como posso relatar uma cobrança que considero indevida?
A:Você pode entrar em contato com nosso atendimento para iniciar o processo de verificação da cobrança.

Q:Quais informações preciso fornecer para contestar uma cobrança?
A:Para registrar seu atendimento, você precisa nos informar os dados do boleto e qual procedimento você considera incorreto.

Q:Receberei um número de protocolo para minha reclamação?
A:Sim, ao registrar a solicitação, nosso atendente irá fornecer um número de protocolo para seu acompanhamento, seja verbalmente ou por texto.

Q:O que acontece depois que eu faço a reclamação?
A:Após o registro, sua solicitação é enviada para análise interna do setor de contas médicas. É necessário aguardar o retorno desta análise para prosseguir.

Q:E se a análise mostrar que a cobrança está correta?
A:Caso a cobrança seja devida, nosso atendimento enviará a você o comprovante da realização do procedimento por e-mail ou outro meio de contato.

Q:O que acontece se for confirmado que a cobrança foi errada?
A:Se a cobrança for confirmada como indevida, nosso atendimento entrará em contato para verificar com você a melhor forma de aplicar o desconto.

Q:Como posso receber o valor da cobrança indevida?
A:Você pode escolher entre ter o desconto aplicado no boleto atual ou receber um crédito para uma cobrança futura.

Q:Como faço para ter o desconto na minha fatura atual?
A:Se você optar pelo desconto no boleto atual, nosso atendimento informará diretamente ao setor financeiro para que a correção seja feita.

Q:Como faço para usar o valor como crédito no futuro?
A:Para receber o valor como crédito em uma fatura futura, o procedimento é solicitar presencialmente na tesouraria.

Q:Como posso fazer minha solicitação/cadastro?
A:Este é um processo que deve ser realizado exclusivamente de forma presencial em uma de nossas unidades de atendimento.

Q:Quais documentos eu preciso levar para o atendimento?
A:É necessário apresentar seu RG e CPF, além de um comprovante de matrícula da faculdade e um comprovante de frequência.

Q:O que acontece quando eu chegar para o atendimento?
A:Nossa equipe irá solicitar seus documentos, tirar uma cópia, registrar o atendimento em nosso sistema e, ao final, entregar a você uma via física do protocolo.

Q:O meu cadastro é finalizado na hora?
A:Não. Após o registro inicial, sua documentação é encaminhada internamente para o setor de cadastro para ser validada.

Q:O que acontece se houver algum problema com meus documentos?
A:Se o setor de cadastro identificar que algum documento está incorreto ou faltando, ele notificará nossa equipe de atendimento para que o contato seja feito com você.

Q:Como serei informado se precisar corrigir minha documentação?
A:Caso haja necessidade de correção, nossa equipe de atendimento entrará em contato com você por telefone ou por nosso canal de comunicação para solicitar os documentos corretos.

Q:Como posso emitir a segunda via do meu boleto?
A:Você pode emitir a segunda via do boleto diretamente em nossa plataforma, seja pelo site ou pelo aplicativo.

Q:Qual o passo a passo para imprimir meu boleto pela plataforma?
A:Primeiro, acesse o site ou aplicativo e faça seu login. Em seguida, navegue até o menu financeiro. Depois, selecione o mês (competência) do boleto que você deseja e clique na opção para impressão da 2ª via.

Q:Onde encontro meus boletos e faturas?
A:Todos os seus boletos ficam disponíveis na área do "menu financeiro" do nosso site ou aplicativo.

Q:Como seleciono o boleto do mês que eu quero pagar?
A:Dentro do menu financeiro, basta selecionar a competência (o mês de referência) que você deseja para visualizar e imprimir o boleto correspondente.

Q:Como posso solicitar autorização para um exame ou procedimento médico?
A:Você pode iniciar a solicitação de autorização entrando em contato via Whatsapp ou presencialmente em uma de nossas unidades.

Q:Quais informações preciso enviar para solicitar uma autorização?
A:É necessário fornecer seu nome completo, data de nascimento e enviar uma foto do pedido médico.

Q:O que acontece depois que eu envio os documentos?
A:Nossa equipe de atendimento irá registrar sua solicitação em nosso sistema. Se o procedimento exigir uma análise mais aprofundada (auditoria), você será informado.

Q:Qual o prazo para ter a autorização liberada?
A:Se o seu pedido for para auditoria, o prazo de retorno é de 3 dias úteis para procedimentos comuns e 10 dias úteis para casos que envolvem OPME (Órteses, Próteses e Materiais Especiais).

Q:Como posso acompanhar o andamento da minha solicitação?
A:Você pode monitorar o status da sua guia de autorização pelo nosso aplicativo, por ligação ou via Whatsapp.

Q:Como sei se o procedimento foi autorizado?
A:Ao final da análise, nossa equipe informará o resultado. Se for aprovado, você receberá o número do protocolo e o número da guia de autorização.

Q:O que acontece se meu pedido de autorização for negado?
A:Caso o procedimento não tenha cobertura e seja negado, nossa equipe de atendimento entrará em contato para informar o número do protocolo e o motivo da negativa.

Q:Como posso solicitar a troca de titularidade do plano?
A:A troca de titularidade é um processo realizado exclusivamente de forma presencial em uma de nossas unidades.

Q:Quem precisa comparecer ao local para fazer a troca?
A:É necessária a presença tanto do titular atual quanto do futuro titular. Em caso de falecimento do titular atual, apenas o novo responsável precisa comparecer com a documentação necessária.

Q:Quais documentos são necessários para a troca de titularidade?
A:É preciso apresentar RG, CPF e comprovante de endereço do novo titular. Se o titular atual for falecido, é necessário trazer também a certidão de óbito.

Q:Qual o procedimento feito no atendimento presencial?
A:No local, nossa equipe irá imprimir um documento padrão que deverá ser assinado. Em seguida, faremos cópias dos documentos, registraremos o atendimento e forneceremos a você uma via física do protocolo.

Q:A troca de titular é concluída na mesma hora?
A:Não. Após o atendimento inicial, sua documentação é encaminhada internamente para o setor de cadastro para ser validada. Este processo é feito uma vez ao dia.

Q:Como serei informado se houver algum problema com os documentos?
A:Caso o setor de cadastro identifique alguma pendência ou erro, nossa equipe de atendimento entrará em contato com você por telefone ou outro canal de comunicação para solicitar a correção.
""";

		try {
			// cria a pasta "base" se não existir
			File dir = new File("base");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			FileWriter writer = new FileWriter("base/knowledge_base.txt");
			writer.write(conteudo);
			writer.close();

			System.out.println("Arquivo knowledge_base.txt criado/substituído em base/ com sucesso!");
		} catch (IOException e) {
			System.out.println("Erro ao criar o arquivo: " + e.getMessage());
		}

		SpringApplication.run(ApiApplication.class, args);
	}

}
