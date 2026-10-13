package com.ecotrack.controller;

import com.ecotrack.dto.ColetaRequest;
import com.ecotrack.entity.Coleta;
import com.ecotrack.service.ColetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coletas")
@RequiredArgsConstructor
public class ColetaController {
    private final ColetaService service;

    @GetMapping
    public ResponseEntity<List<Coleta>> listar() { return ResponseEntity.ok(service.listar()); }

    @GetMapping("/{id}")
    public ResponseEntity<Coleta> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.buscar(id)); }

    @PostMapping
    public ResponseEntity<Coleta> criar(@RequestBody @Valid ColetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coleta> atualizar(@PathVariable Long id, @RequestBody @Valid ColetaRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
