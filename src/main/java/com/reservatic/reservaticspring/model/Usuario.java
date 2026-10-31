package com.reservatic.reservaticspring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entidad mapeada a la tabla usuarios (solo lectura en este módulo). */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Column(name = "correo_institucional")
    private String correoInstitucional;

    @Column(name = "rol")
    private String rol;

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreoInstitucional() { return correoInstitucional; }
    public void setCorreoInstitucional(String correoInstitucional) { this.correoInstitucional = correoInstitucional; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}