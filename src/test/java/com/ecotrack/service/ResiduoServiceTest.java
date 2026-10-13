package com.ecotrack.service;

import com.ecotrack.dto.ResiduoRequest;
import com.ecotrack.entity.Residuo;
import com.ecotrack.entity.StatusColeta;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.ResiduoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResiduoServiceTest {

    @Mock
    private ResiduoRepository repository;

    @InjectMocks
    private ResiduoService service;

    @Test
    void deveCriarResiduoComStatusPadraoECalcularCo2() {
        ResiduoRequest request = new ResiduoRequest("Plástico", 25.0, null, null);
        when(repository.save(any(Residuo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Residuo resultado = service.criar(request);

        assertThat(resultado.getTipoResiduo()).isEqualTo("Plástico");
        assertThat(resultado.getPesoKg()).isEqualTo(25.0);
        assertThat(resultado.getStatusColeta()).isEqualTo(StatusColeta.PENDENTE);
        assertThat(resultado.getImpactoCo2Kg()).isEqualTo(45.0);
        assertThat(resultado.getDataRegistro()).isEqualTo(LocalDate.now());
        verify(repository).save(any(Residuo.class));
    }

    @Test
    void deveAtualizarResiduoERecalcularCo2() {
        Residuo existente = Residuo.builder()
                .id(1L)
                .tipoResiduo("Papel")
                .pesoKg(10.0)
                .dataRegistro(LocalDate.of(2026, 1, 10))
                .statusColeta(StatusColeta.PENDENTE)
                .impactoCo2Kg(18.0)
                .build();
        ResiduoRequest request = new ResiduoRequest("Vidro", 12.5, null, StatusColeta.COLETADA);

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Residuo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Residuo resultado = service.atualizar(1L, request);

        assertThat(resultado.getTipoResiduo()).isEqualTo("Vidro");
        assertThat(resultado.getImpactoCo2Kg()).isEqualTo(22.5);
        assertThat(resultado.getStatusColeta()).isEqualTo(StatusColeta.COLETADA);
        assertThat(resultado.getDataRegistro()).isEqualTo(LocalDate.of(2026, 1, 10));
    }

    @Test
    void deveLancarExcecaoQuandoResiduoNaoExistir() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resíduo não encontrado");
    }
}
