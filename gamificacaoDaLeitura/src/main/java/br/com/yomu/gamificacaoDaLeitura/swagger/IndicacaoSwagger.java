package br.com.yomu.gamificacaoDaLeitura.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Modelos de documentação (OpenAPI/Swagger) para o recurso Indicacao.
 * Cada record abaixo representa o schema usado por um endpoint do IndicacaoController.
 * Comentários acima de cada seção explicam qual endpoint deve usar qual modelo.
 */
public class IndicacaoSwagger {

    // =====================================
    // MODELOS PARA CRIAÇÃO DE INDICAÇÃO
    // Endpoint: POST /api/indicacoes
    // O front envia IDs (UUID) de remetente, destinatário e livro + mensagem opcional.
    // =====================================
    @Schema(name = "IndicacaoCreate", description = "Payload para criar uma indicação")
    public record IndicacaoCreate(
            @Schema(description = "UUID do usuário remetente", example = "123e4567-e89b-12d3-a456-426614174000", required = true) UUID remetenteId,
            @Schema(description = "UUID do usuário destinatário", example = "223e4567-e89b-12d3-a456-426614174001", required = true) UUID destinatarioId,
            @Schema(description = "UUID do livro indicado", example = "323e4567-e89b-12d3-a456-426614174002", required = true) UUID livroId,
            @Schema(description = "Mensagem opcional da indicação", example = "Acho que você vai gostar desse livro!", required = false) String mensagem
    ) {}

    // =====================================
    // MODELO DE RESPOSTA COMPLETA (DETALHE)
    // Endpoint: GET /api/indicacoes/{id}  e  POST /api/indicacoes (resposta criada)
    // Retorna a indicação com informações essenciais sobre remetente, destinatário e livro.
    // =====================================
    @Schema(name = "IndicacaoResponse", description = "Detalhes de uma indicação")
    public record IndicacaoResponse(
            @Schema(description = "UUID da indicação", example = "423e4567-e89b-12d3-a456-426614174003") UUID id,
            @Schema(description = "Resumo do remetente") UsuarioResumo remetente,
            @Schema(description = "Resumo do destinatário") UsuarioResumo destinatario,
            @Schema(description = "Resumo do livro indicado") LivroResumo livro,
            @Schema(description = "Mensagem enviada pelo remetente", example = "Lê que é top!") String mensagem,
            @Schema(description = "Flag indicando se já foi lida", example = "false") Boolean lida,
            @Schema(description = "Data de criação da indicação (server)") LocalDateTime createdAt
    ) {}

    // =====================================
    // MODELOS PARA LISTAGENS
    // Endpoint: GET /api/indicacoes/destinatario/{destinatarioId}
    // Endpoint: GET /api/indicacoes/destinatario/{destinatarioId}/nao-lidas
    // Endpoint: GET /api/indicacoes/remetente/{remetenteId}
    // Esses endpoints retornam listas de IndicacaoResponse ou listas resumidas (p/ performance)
    // =====================================
    @Schema(name = "IndicacaoSummary", description = "Resumo compacto de uma indicação para listagens")
    public record IndicacaoSummary(
            @Schema(description = "UUID da indicação", example = "523e4567-e89b-12d3-a456-426614174004") UUID id,
            @Schema(description = "Resumo do remetente") UsuarioResumo remetente,
            @Schema(description = "Resumo do livro indicado") LivroResumo livro,
            @Schema(description = "Mensagem curta", example = "Leitura recomendada") String mensagem,
            @Schema(description = "Flag lida", example = "false") Boolean lida,
            @Schema(description = "Data de criação") LocalDateTime createdAt
    ) {}

    // =====================================
    // MODELOS PARA AÇÕES (MARCAR COMO LIDA / MARCAR TODAS COMO LIDAS / DELETAR)
    // Endpoints: PATCH /api/indicacoes/{id}/marcar-lida
    //            PATCH /api/indicacoes/destinatario/{destinatarioId}/marcar-todas-lidas
    //            DELETE /api/indicacoes/{id}
    // Esses endpoints não possuem body na requisição (exceto códigos de caminho) e retornam 204 No Content.
    // =====================================

    // =====================================
    // MODELOS AUXILIARES (Resumos para Usuario e Livro usados nas responses)
    // Esses resumos evitam expor toda a entidade Usuario/Livro nas respostas.
    // =====================================
    @Schema(name = "UsuarioResumo", description = "Resumo público mínimo de um usuário (usado em indicações)")
    public record UsuarioResumo(
            @Schema(description = "UUID do usuário", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
            @Schema(description = "Nome de usuário", example = "joao_silva") String nomeUsuario,
            @Schema(description = "URL da foto de perfil (opcional)", example = "https://example.com/foto.jpg") String fotoPerfil
    ) {}

    @Schema(name = "LivroResumo", description = "Resumo público mínimo de um livro (usado em indicações)")
    public record LivroResumo(
            @Schema(description = "UUID do livro", example = "323e4567-e89b-12d3-a456-426614174002") UUID id,
            @Schema(description = "Título do livro", example = "O Senhor dos Anéis") String titulo,
            @Schema(description = "Autor do livro", example = "J.R.R. Tolkien") String autor
    ) {}

}
