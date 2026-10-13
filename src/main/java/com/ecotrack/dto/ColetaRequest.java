package com.ecotrack.dto;

import com.ecotrack.entity.StatusColeta;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ColetaRequest(
        @NotNull LocalDate dataColeta,
        @NotBlank String empresaResponsavel,
        StatusColeta status,
        @NotNull Long residuoId
) {}
