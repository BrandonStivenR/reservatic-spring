package com.reservatic.reservaticspring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/** Entidad mapeada a la tabla reservas. Une un usuario con una sala en una fecha y franja horaria. */
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    // Quien hace la reserva (llave foránea id_usuario)
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Sala reservada (llave foránea id_sala)
    @ManyToOne
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;

    @Column(name = "ficha_programa", nullable = false)
    private String fichaPrograma;

    @Column(name = "fecha_reserva", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaReserva;

    @Column(name = "hora_inicio", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaFin;

    // Valores válidos en la BD: Confirmada, Cancelada, Pendiente
    @Column(name = "estado", nullable = false)
    private String estado = "Confirmada";

    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }
    public String getFichaPrograma() { return fichaPrograma; }
    public void setFichaPrograma(String fichaPrograma) { this.fichaPrograma = fichaPrograma; }
    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}