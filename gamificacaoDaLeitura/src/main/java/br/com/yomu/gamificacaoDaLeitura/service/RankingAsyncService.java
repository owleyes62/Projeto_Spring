package br.com.yomu.gamificacaoDaLeitura.service;

import br.com.yomu.gamificacaoDaLeitura.model.Ranking;
import br.com.yomu.gamificacaoDaLeitura.model.enums.PeriodoRanking;
import br.com.yomu.gamificacaoDaLeitura.model.enums.TipoRanking;
import br.com.yomu.gamificacaoDaLeitura.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingAsyncService {
    
    private final RankingService rankingService;
    private final RankingRepository rankingRepository;
    
    /**
     * Escuta o evento APÓS o commit da transação de progresso.
     * Roda em nova transação separada.
     */
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("rankingTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW) // ✅ Nova transação
    public void onProgressoRegistrado(ProgressoRegistradoEvent event) {
        log.info("🚀 Evento recebido! Iniciando atualização de rankings para usuário: {}", event.getUsuarioId());
        
        try {
            // Atualizar rankings gerais (com validação de tempo)
            atualizarRankingSeNecessario(TipoRanking.GERAL, PeriodoRanking.SEMANAL);
            atualizarRankingSeNecessario(TipoRanking.GERAL, PeriodoRanking.MENSAL);
            atualizarRankingSeNecessario(TipoRanking.GERAL, PeriodoRanking.ANUAL);
            atualizarRankingSeNecessario(TipoRanking.GERAL, PeriodoRanking.TOTAL);
            
            // Atualizar ranking de amigos
            log.info("📊 Atualizando rankings de amigos...");
            rankingService.calcularERankingAmigos(event.getUsuarioId(), PeriodoRanking.SEMANAL);
            rankingService.calcularERankingAmigos(event.getUsuarioId(), PeriodoRanking.MENSAL);
            
            log.info("✅ Rankings atualizados com sucesso!");
            
        } catch (Exception e) {
            log.error("❌ Erro ao atualizar rankings: {}", e.getMessage(), e);
        }
    }
    
    private void atualizarRankingSeNecessario(TipoRanking tipo, PeriodoRanking periodo) {
        if (deveAtualizarRanking(tipo, periodo)) {
            log.info("📊 Atualizando ranking {} - {}", tipo, periodo);
            rankingService.calcularERankingGeral(periodo);
        } else {
            log.debug("⏭️  Pulando atualização de ranking {} - {} (atualizado recentemente)", tipo, periodo);
        }
    }
    
    private boolean deveAtualizarRanking(TipoRanking tipo, PeriodoRanking periodo) {
        try {
            Ranking ranking = rankingRepository.findByTipoRankingAndPeriodoRanking(tipo, periodo)
                    .orElse(null);
            
            if (ranking == null) {
                return true;
            }
            
            LocalDateTime limiteAtualizacao = LocalDateTime.now().minusMinutes(5);
            return ranking.getUpdatedAt().isBefore(limiteAtualizacao);
            
        } catch (Exception e) {
            log.warn("⚠️  Erro ao verificar atualização de ranking: {}", e.getMessage());
            return true;
        }
    }
}