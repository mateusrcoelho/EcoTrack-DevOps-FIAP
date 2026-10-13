package com.ecotrack.service;

import com.ecotrack.dto.IndicadoresResponse;
import com.ecotrack.entity.RelatorioAmbiental;
import com.ecotrack.entity.Residuo;
import com.ecotrack.repository.ColetaRepository;
import com.ecotrack.repository.RelatorioAmbientalRepository;
import com.ecotrack.repository.ResiduoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndicadorServiceTest {

    @Mock
    private ResiduoRepository residuoRepository;

    @Mock
    private ColetaRepository coletaRepository;

    @Mock
    private RelatorioAmbientalRepository relatorioRepository;

    @InjectMocks
    private IndicadorService service;

    @Test
    void deveConsolidarIndicadoresEsg() {
        Residuo r1 = Residuo.builder().pesoKg(10.0).impactoCo2Kg(18.0).build();
        Residuo r2 = Residuo.builder().pesoKg(20.5).impactoCo2Kg(36.9).build();
        when(residuoRepository.findAll()).thenReturn(List.of(r1, r2));
        when(coletaRepository.count()).thenReturn(3L);

        IndicadoresResponse resultado = service.indicadores();

        assertThat(resultado.quantidadeResiduos()).isEqualTo(2);
        assertThat(resultado.totalPesoKg()).isEqualTo(30.5);
        assertThat(resultado.totalCo2EvitadoKg()).isEqualTo(54.9);
        assertThat(resultado.coletasRegistradas()).isEqualTo(3);
    }

    @Test
    void deveGerarRelatorioComOsIndicadoresAtuais() {
        Residuo residuo = Residuo.builder().pesoKg(15.0).impactoCo2Kg(27.0).build();
        when(residuoRepository.findAll()).thenReturn(List.of(residuo));
        when(coletaRepository.count()).thenReturn(1L);
        when(relatorioRepository.save(any(RelatorioAmbiental.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RelatorioAmbiental resultado = service.gerarRelatorio();

        assertThat(resultado.getTotalResiduosKg()).isEqualTo(15.0);
        assertThat(resultado.getTotalCo2EvitadoKg()).isEqualTo(27.0);
        assertThat(resultado.getDataGeracao()).isNotNull();
        verify(relatorioRepository).save(any(RelatorioAmbiental.class));
    }
}
