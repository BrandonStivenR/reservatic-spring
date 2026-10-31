package com.reservatic.reservaticspring.repository;

import com.reservatic.reservaticspring.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    /**
     * Indica si ya hay una reserva activa en la misma sala y fecha cuyo horario
     * se cruce con [inicio, fin). Evita la duplicidad de cupos.
     * idExcluir es el id de la reserva que se está editando (0 si es nueva).
     */
    @Query("SELECT COUNT(r) > 0 FROM Reserva r "
         + "WHERE r.sala.idSala = :idSala AND r.fechaReserva = :fecha "
         + "AND r.estado <> 'Cancelada' "
         + "AND r.horaInicio < :fin AND r.horaFin > :inicio "
         + "AND r.idReserva <> :idExcluir")
    boolean existeCruce(@Param("idSala") Integer idSala,
                        @Param("fecha") LocalDate fecha,
                        @Param("inicio") LocalTime inicio,
                        @Param("fin") LocalTime fin,
                        @Param("idExcluir") Integer idExcluir);
}