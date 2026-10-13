package com.ecotrack.service;

import com.ecotrack.dto.IndicadoresResponse;
import com.ecotrack.entity.RelatorioAmbiental;
import com.ecotrack.repository.ColetaRepository;
import com.ecotrack.repository.RelatorioAmbientalRepository;
import com.ecotrack.repository.ResiduoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class IndicadorService {
    private final ResiduoRepository residuoRepository;
    private final ColetaRepository coletaRepository;
    private final RelatorioAmbientalRepository relatorioRepository;

    public IndicadoresResponse indicadores() {
        var residuos = residuoRepository.findAll();
        double peso = residuos.stream().mapToDouble(r -> r.getPesoKg() == null ? 0 : r.getPesoKg()).sum();
        double co2 = residuos.stream().mapToDouble(r -> r.getImpactoCo2Kg() == null ? 0 : r.getImpactoCo2Kg()).sum();
        return new IndicadoresResponse(residuos.size(), peso, co2, coletaRepository.count());
    }

    public RelatorioAmbiental gerarRelatorio() {
        IndicadoresResponse i = indicadores();
        return relatorioRepository.save(RelatorioAmbiental.builder()
                .totalResiduosKg(i.totalPesoKg())
                .totalCo2EvitadoKg(i.totalCo2EvitadoKg())
                .dataGeracao(LocalDate.now())
                .build());
    }
}
