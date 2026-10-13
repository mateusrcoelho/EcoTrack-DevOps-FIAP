package com.ecotrack.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "TB_RESIDUO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Residuo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TIPO_RESIDUO", nullable = false, length = 80)
    private String tipoResiduo;

    @Column(name = "PESO_KG", nullable = false)
    private Double pesoKg;

    @Column(name = "DATA_REGISTRO", nullable = false)
    private LocalDate dataRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_COLETA", nullable = false, length = 20)
    private StatusColeta statusColeta;

    @Column(name = "IMPACTO_CO2_KG", nullable = false)
    private Double impactoCo2Kg;
}
