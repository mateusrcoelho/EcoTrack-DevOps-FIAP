package com.ecotrack.service;

import com.ecotrack.dto.ColetaRequest;
import com.ecotrack.entity.Coleta;
import com.ecotrack.entity.StatusColeta;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.ColetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColetaService {
    private final ColetaRepository coletaRepository;
    private final ResiduoService residuoService;

    public List<Coleta> listar() { return coletaRepository.findAll(); }

    public Coleta buscar(Long id) {
        return coletaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coleta não encontrada"));
    }

    public Coleta criar(ColetaRequest request) {
        Coleta coleta = Coleta.builder()
                .dataColeta(request.dataColeta())
                .empresaResponsavel(request.empresaResponsavel())
                .status(request.status() == null ? StatusColeta.AGENDADA : request.status())
                .residuo(residuoService.buscar(request.residuoId()))
                .build();
        return coletaRepository.save(coleta);
    }

    public Coleta atualizar(Long id, ColetaRequest request) {
        Coleta coleta = buscar(id);
        coleta.setDataColeta(request.dataColeta());
        coleta.setEmpresaResponsavel(request.empresaResponsavel());
        coleta.setStatus(request.status() == null ? coleta.getStatus() : request.status());
        coleta.setResiduo(residuoService.buscar(request.residuoId()));
        return coletaRepository.save(coleta);
    }

    public void deletar(Long id) { coletaRepository.delete(buscar(id)); }
}
