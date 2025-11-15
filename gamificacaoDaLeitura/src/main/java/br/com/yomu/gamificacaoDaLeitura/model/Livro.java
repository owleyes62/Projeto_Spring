package br.com.yomu.gamificacaoDaLeitura.model;

import br.com.yomu.gamificacaoDaLeitura.model.enums.TipoRegistro;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
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
@Table(name = "livros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa um livro cadastrado pelo usuário no sistema Yomu")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "Identificador único do livro", example = "60cf600f-748e-49e8-8aae-534e6186770b")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @Schema(description = "Usuário dono do livro (oculto no Swagger)", hidden = true)
    @JsonIgnore
    private Usuario usuario;

    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false)
    @Schema(description = "Título do livro", example = "O Senhor dos Anéis")
    private String titulo;

    @NotBlank(message = "Autor é obrigatório")
    @Column(nullable = false)
    @Schema(description = "Nome do autor", example = "J. R. R. Tolkien")
    private String autor;

    @Min(value = 0)
    @Schema(description = "Quantidade de páginas (necessário se tipoRegistro = PAGINA)", example = "1200")
    private Integer numeroPaginas;

    @Min(value = 0)
    @Schema(description = "Quantidade de capítulos (necessário se tipoRegistro = CAPITULO)", example = "50")
    private Integer numeroCapitulos;

    @Schema(description = "URL da imagem de capa", example = "https://images.pexels.com/photos/22969/pexels-photo.jpg")
    private String capa;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descrição ou sinopse completa do livro", example = "Uma fantasia épica sobre a jornada para destruir o Um Anel.")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Tipo de progressão usada para registrar leitura", example = "PAGINA")
    private TipoRegistro tipoRegistro;

    @Column(nullable = false)
    @Schema(description = "Indica se o livro já foi finalizado", example = "false")
    private Boolean finalizado = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Schema(description = "Data de criação", example = "2025-11-01T10:15:30")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Schema(description = "Data da última atualização", example = "2025-11-10T15:22:10")
    private LocalDateTime updatedAt;

    // Relacionamentos ocultos (evita loops)
    @JsonIgnore
    @OneToMany(mappedBy = "livro")
    private List<Progresso> progressos;

    @JsonIgnore
    @OneToMany(mappedBy = "livro")
    private List<Indicacao> indicacoes;
}
