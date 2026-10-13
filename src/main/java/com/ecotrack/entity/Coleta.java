package com.ecotrack.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "TB_COLETA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coleta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATA_COLETA", nullable = false)
    private LocalDate dataColeta;

    @Column(name = "EMPRESA_RESPONSAVEL", nullable = false, length = 140)
    private String empresaResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusColeta status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RESIDUO_ID")
    private Residuo residuo;
}
