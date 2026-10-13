package com.ecotrack.service;

import com.ecotrack.dto.ColetaRequest;
import com.ecotrack.entity.Coleta;
import com.ecotrack.entity.Residuo;
import com.ecotrack.entity.StatusColeta;
import com.ecotrack.repository.ColetaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColetaServiceTest {

    @Mock
    private ColetaRepository coletaRepository;

    @Mock
    private ResiduoService residuoService;

    @InjectMocks
    private ColetaService service;

    @Test
    void deveCriarColetaComStatusAgendadaQuandoStatusNaoForInformado() {
        Residuo residuo = Residuo.builder().id(1L).tipoResiduo("Metal").pesoKg(8.0).build();
        ColetaRequest request = new ColetaRequest(
                LocalDate.of(2026, 10, 20),
                "Cooperativa Verde",
                null,
                1L
        );

        when(residuoService.buscar(1L)).thenReturn(residuo);
        when(coletaRepository.save(any(Coleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Coleta resultado = service.criar(request);

        assertThat(resultado.getStatus()).isEqualTo(StatusColeta.AGENDADA);
        assertThat(resultado.getResiduo()).isSameAs(residuo);
        assertThat(resultado.getEmpresaResponsavel()).isEqualTo("Cooperativa Verde");
        verify(coletaRepository).save(any(Coleta.class));
    }
}
