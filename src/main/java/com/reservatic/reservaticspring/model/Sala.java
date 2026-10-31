package com.reservatic.reservaticspring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entidad mapeada a la tabla salas. */
@Entity
@Table(name = "salas")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Integer idSala;

    @Column(name = "nombre_sala")
    private String nombreSala;

    public Integer getIdSala() { return idSala; }
    public void setIdSala(Integer idSala) { this.idSala = idSala; }
    public String getNombreSala() { return nombreSala; }
    public void setNombreSala(String nombreSala) { this.nombreSala = nombreSala; }
}