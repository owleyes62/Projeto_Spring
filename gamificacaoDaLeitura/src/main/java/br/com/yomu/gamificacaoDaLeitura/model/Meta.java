package br.com.yomu.gamificacaoDaLeitura.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.yomu.gamificacaoDaLeitura.model.enums.TipoMeta;
import br.com.yomu.gamificacaoDaLeitura.model.enums.UnidadeMeta;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "metas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMeta tipoMeta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMeta unidadeMeta;

    @Min(value = 1, message = "Quantidade alvo deve ser maior que zero")
    @Column(nullable = false)
    private Integer quantidadeAlvo;

    @Min(value = 0, message = "Quantidade atual não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidadeAtual = 0;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Column(nullable = false)
    private Boolean concluida = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void calcularDataFim() {
        if (this.dataInicio != null && this.tipoMeta != null) {
            this.dataFim = this.dataInicio.plusDays(this.tipoMeta.getDuracaoDias());
        }
    }

    public void atualizarProgresso(Integer quantidade) {
        this.quantidadeAtual += quantidade;
        if (this.quantidadeAtual >= this.quantidadeAlvo) {
            this.concluida = true;
        }
    }
}