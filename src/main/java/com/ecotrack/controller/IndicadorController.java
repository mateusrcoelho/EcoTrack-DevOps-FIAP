package com.ecotrack.controller;

import com.ecotrack.dto.IndicadoresResponse;
import com.ecotrack.entity.RelatorioAmbiental;
import com.ecotrack.service.IndicadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class IndicadorController {
    private final IndicadorService service;

    @GetMapping("/indicadores")
    public ResponseEntity<IndicadoresResponse> indicadores() { return ResponseEntity.ok(service.indicadores()); }

    @PostMapping("/relatorios/ambiental")
    public ResponseEntity<RelatorioAmbiental> gerarRelatorio() {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarRelatorio());
    }
}
