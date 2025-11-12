package br.com.yomu.gamificacaoDaLeitura.model;

import br.com.yomu.gamificacaoDaLeitura.model.enums.TipoRegistro;
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
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "Autor é obrigatório")
    @Column(nullable = false)
    private String autor;

    @Min(value = 0, message = "Número de páginas não pode ser negativo")
    private Integer numeroPaginas;

    @Min(value = 0, message = "Número de capítulos não pode ser negativo")
    private Integer numeroCapitulos;

    private String capa;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRegistro tipoRegistro;

    @Column(nullable = false)
    private Boolean finalizado = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relacionamentos
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Progresso> progressos;

    @OneToMany(mappedBy = "livro")
    @JsonIgnore
    private List<Indicacao> indicacoes;
}