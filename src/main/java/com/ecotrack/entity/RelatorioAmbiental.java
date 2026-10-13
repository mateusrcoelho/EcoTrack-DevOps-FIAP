package com.ecotrack.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "TB_RELATORIO_AMBIENTAL")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RelatorioAmbiental {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TOTAL_RESIDUOS_KG", nullable = false)
    private Double totalResiduosKg;

    @Column(name = "TOTAL_CO2_EVITADO_KG", nullable = false)
    private Double totalCo2EvitadoKg;

    @Column(name = "DATA_GERACAO", nullable = false)
    private LocalDate dataGeracao;
}
