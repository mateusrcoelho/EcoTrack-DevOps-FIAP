package com.ecotrack.dto;

import com.ecotrack.entity.StatusColeta;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ResiduoRequest(
        @NotBlank String tipoResiduo,
        @Positive Double pesoKg,
        LocalDate dataRegistro,
        StatusColeta statusColeta
) {}
