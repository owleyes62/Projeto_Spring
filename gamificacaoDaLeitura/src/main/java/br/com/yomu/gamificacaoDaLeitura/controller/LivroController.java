package br.com.yomu.gamificacaoDaLeitura.controller;

import br.com.yomu.gamificacaoDaLeitura.model.Livro;
import br.com.yomu.gamificacaoDaLeitura.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "Gerenciamento de livros cadastrados pelos usuários")
public class LivroController {

    private final LivroService livroService;

    @PostMapping("/usuario/{usuarioId}")
    @Operation(summary = "Criar um novo livro", description = "Registra um livro para um usuário baseando-se no tipo de registro (página ou capítulo).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Livro criado com sucesso",
            content = @Content(schema = @Schema(implementation = Livro.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Livro> criar(
            @Parameter(description = "ID do usuário dono do livro")
            @PathVariable UUID usuarioId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados completos do livro a ser criado",
                required = true,
                content = @Content(schema = @Schema(implementation = Livro.class))
            )
            @Valid @RequestBody Livro livro) {

        Livro livroCriado = livroService.criar(usuarioId, livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(livroCriado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar livro por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Livro encontrado"),
        @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<Livro> buscarPorId(
            @Parameter(description = "UUID do livro") @PathVariable UUID id) {

        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar livros por usuário")
    public ResponseEntity<List<Livro>> listarPorUsuario(
            @Parameter(description = "UUID do usuário") @PathVariable UUID usuarioId) {

        return ResponseEntity.ok(livroService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/finalizado/{finalizado}")
    @Operation(summary = "Listar livros por status (finalizado ou não)")
    public ResponseEntity<List<Livro>> listarPorStatus(
            @Parameter(description = "ID do usuário") @PathVariable UUID usuarioId,
            @Parameter(description = "true = finalizado, false = em progresso") @PathVariable Boolean finalizado) {

        return ResponseEntity.ok(livroService.listarPorUsuarioEStatus(usuarioId, finalizado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar informações do livro")
    public ResponseEntity<Livro> atualizar(
            @Parameter(description = "ID do livro") @PathVariable UUID id,
            @RequestBody Livro livro) {

        return ResponseEntity.ok(livroService.atualizar(id, livro));
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Marcar livro como finalizado")
    public ResponseEntity<Void> marcarComoFinalizado(
            @Parameter(description = "ID do livro") @PathVariable UUID id) {

        livroService.marcarComoFinalizado(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar livro")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do livro") @PathVariable UUID id) {

        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
