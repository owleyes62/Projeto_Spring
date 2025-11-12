package br.com.yomu.gamificacaoDaLeitura.model;

import br.com.yomu.gamificacaoDaLeitura.model.enums.StatusAmizade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "amizades", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id_1", "usuario_id_2"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Amizade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id_1", nullable = false)
    @JsonIgnore
    private Usuario usuarioId1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id_2", nullable = false)
    @JsonIgnore
    private Usuario usuarioId2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAmizade status = StatusAmizade.PENDENTE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    private LocalDateTime dataAceite;

    @PreUpdate
    @PrePersist
    public void validar() {
        if (usuarioId1.getId().equals(usuarioId2.getId())) {
            throw new IllegalArgumentException("Usuário não pode adicionar a si mesmo");
        }
    }
}