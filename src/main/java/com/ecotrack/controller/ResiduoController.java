package com.ecotrack.controller;

import com.ecotrack.dto.ResiduoRequest;
import com.ecotrack.entity.Residuo;
import com.ecotrack.service.ResiduoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/residuos")
@RequiredArgsConstructor
public class ResiduoController {
    private final ResiduoService service;

    @GetMapping
    public ResponseEntity<List<Residuo>> listar() { return ResponseEntity.ok(service.listar()); }

    @GetMapping("/{id}")
    public ResponseEntity<Residuo> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.buscar(id)); }

    @PostMapping
    public ResponseEntity<Residuo> criar(@RequestBody @Valid ResiduoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Residuo> atualizar(@PathVariable Long id, @RequestBody @Valid ResiduoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
    service.deletar(id);
    return ResponseEntity.ok("Resíduo excluído com sucesso");
    }

}
