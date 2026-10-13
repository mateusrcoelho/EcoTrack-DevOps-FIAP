package com.ecotrack.dto;

public record IndicadoresResponse(
        long quantidadeResiduos,
        double totalPesoKg,
        double totalCo2EvitadoKg,
        long coletasRegistradas
) {}
