package com.ecotrack.service;

import com.ecotrack.dto.ResiduoRequest;
import com.ecotrack.entity.Residuo;
import com.ecotrack.entity.StatusColeta;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.ResiduoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResiduoService {
    private final ResiduoRepository repository;

    public List<Residuo> listar() { return repository.findAll(); }

    public Residuo buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resíduo não encontrado"));
    }

    public Residuo criar(ResiduoRequest request) {
        Residuo residuo = Residuo.builder()
                .tipoResiduo(request.tipoResiduo())
                .pesoKg(request.pesoKg())
                .dataRegistro(request.dataRegistro() == null ? LocalDate.now() : request.dataRegistro())
                .statusColeta(request.statusColeta() == null ? StatusColeta.PENDENTE : request.statusColeta())
                .impactoCo2Kg(calcularCo2(request.pesoKg()))
                .build();
        return repository.save(residuo);
    }

    public Residuo atualizar(Long id, ResiduoRequest request) {
        Residuo residuo = buscar(id);
        residuo.setTipoResiduo(request.tipoResiduo());
        residuo.setPesoKg(request.pesoKg());
        residuo.setDataRegistro(request.dataRegistro() == null ? residuo.getDataRegistro() : request.dataRegistro());
        residuo.setStatusColeta(request.statusColeta() == null ? residuo.getStatusColeta() : request.statusColeta());
        residuo.setImpactoCo2Kg(calcularCo2(request.pesoKg()));
        return repository.save(residuo);
    }

    public void deletar(Long id) { repository.delete(buscar(id)); }

    private double calcularCo2(Double pesoKg) { return Math.round((pesoKg * 1.8) * 100.0) / 100.0; }
}
