package br.com.yomu.gamificacaoDaLeitura.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidade representando um usuário do sistema de leitura gamificada")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "Identificador único do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @NotBlank(message = "Nome de usuário é obrigatório")
    @Column(nullable = false, unique = true)
    @Schema(description = "Nome de usuário único", example = "joao_silva", required = true)
    private String nomeUsuario;

    @NotBlank(message = "Nome do usuário é obrigatório")
    @Column(nullable = false)
    @Schema(description = "Nome de usuário", example = "João Silva", required = true)
    private String nome;

    @NotBlank(message = "Gênero é obrigatório")
    @Column(nullable = false)
    @Schema(description = "Gênero", example = "masculino", required = true)
    private String genero;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true)
    @Schema(description = "Email do usuário", example = "joao@email.com", required = true)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false)
    @Schema(description = "Senha criptografada", example = "senha123", required = true, accessMode = Schema.AccessMode.WRITE_ONLY)
    private String senha;

    @Min(value = 0, message = "XP total não pode ser negativo")
    @Column(nullable = false)
    @Schema(description = "Total de XP acumulado pelo usuário", example = "2500", minimum = "0")
    private Long xpTotal = 0L;

    @Min(value = 1, message = "Nível mínimo é 1")
    @Column(nullable = false)
    @Schema(description = "Nível atual do usuário baseado no XP", example = "5", minimum = "1")
    private Integer nivelAtual = 1;

    @Column(nullable = false, unique = true)
    @Schema(description = "Código único para adicionar amigos", example = "ABC12345", accessMode = Schema.AccessMode.READ_ONLY)
    private String codigoConvite;

    @Schema(description = "URL da foto de perfil", example = "https://example.com/foto.jpg")
    private String fotoPerfil;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Schema(description = "Data de criação do usuário", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Schema(description = "Data da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    // Relacionamentos (ignorados no Swagger para evitar recursão)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(hidden = true)
    @JsonIgnore
    private List<Livro> livros;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(hidden = true)
    @JsonIgnore
    private List<Progresso> progressos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(hidden = true)
    @JsonIgnore
    private List<Meta> metas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(hidden = true)
    @JsonIgnore
    private List<Notificacao> notificacoes;

    @OneToMany(mappedBy = "usuarioId1")
    @Schema(hidden = true)
    @JsonIgnore
    private List<Amizade> amizadesEnviadas;

    @OneToMany(mappedBy = "usuarioId2")
    @Schema(hidden = true)
    @JsonIgnore
    private List<Amizade> amizadesRecebidas;

    @OneToMany(mappedBy = "remetente")
    @Schema(hidden = true)
    @JsonIgnore
    private List<Indicacao> indicacoesEnviadas;

    @OneToMany(mappedBy = "destinatario")
    @Schema(hidden = true)
    @JsonIgnore
    private List<Indicacao> indicacoesRecebidas;
}