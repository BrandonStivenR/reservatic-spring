package com.reservatic.reservaticspring.controller;

import com.reservatic.reservaticspring.model.Reserva;
import com.reservatic.reservaticspring.repository.ReservaRepository;
import com.reservatic.reservaticspring.repository.SalaRepository;
import com.reservatic.reservaticspring.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.time.LocalDate;
import java.util.regex.Pattern;

/** Controlador MVC del módulo de reservas: listar, crear, editar, eliminar y comprobante. */
@Controller
public class ReservaController {

    // Ficha: solo letras, números, espacios, punto y guion (rechaza símbolos como @ # $ % ' ;)
    private static final Pattern PATRON_FICHA =
            Pattern.compile("^[A-Za-z0-9ÁÉÍÓÚÜáéíóúüÑñ .\\-]+$");

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias por constructor (Spring las entrega solo)
    public ReservaController(ReservaRepository reservaRepository,
                             SalaRepository salaRepository,
                             UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // La raíz redirige a la lista de reservas
    @GetMapping("/")
    public String inicio() {
        return "redirect:/reservas";
    }

    // READ: lista todas las reservas
    @GetMapping("/reservas")
    public String listar(Model model) {
        model.addAttribute("reservas", reservaRepository.findAll());
        return "reservas";
    }

    // CREATE (paso 1): muestra el formulario vacío
    @GetMapping("/reservas/nueva")
    public String nueva(Model model) {
        model.addAttribute("reserva", new Reserva());
        cargarListas(model);
        return "formulario";
    }

    // UPDATE (paso 1): carga la reserva en el formulario
    @GetMapping("/reservas/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) {
            return "redirect:/reservas";
        }
        model.addAttribute("reserva", reserva);
        cargarListas(model);
        return "formulario";
    }

    // CREATE / UPDATE (paso 2): valida y guarda
    @PostMapping("/reservas/guardar")
    public String guardar(@ModelAttribute Reserva reserva, Model model) {
        // Se vuelven a cargar usuario y sala desde la BD usando solo su id
        reserva.setUsuario(usuarioRepository.findById(reserva.getUsuario().getIdUsuario()).orElse(null));
        reserva.setSala(salaRepository.findById(reserva.getSala().getIdSala()).orElse(null));

        String error = validar(reserva);

        if (error != null) {
            model.addAttribute("error", error);
            cargarListas(model);
            return "formulario";
        }

        reserva.setFichaPrograma(reserva.getFichaPrograma().trim());
        Reserva guardada = reservaRepository.save(reserva);
        return "redirect:/reservas/comprobante/" + guardada.getIdReserva();
    }

    /** Aplica las reglas de validación. Devuelve el mensaje de error o null si todo está bien. */
    private String validar(Reserva reserva) {
        // Sala y usuario deben existir en la base de datos
        if (reserva.getSala() == null || reserva.getUsuario() == null) {
            return "Seleccione una sala y un usuario válidos.";
        }
        // Ficha: obligatoria, máximo 50 caracteres (límite de la columna) y sin símbolos raros
        String ficha = reserva.getFichaPrograma() == null ? "" : reserva.getFichaPrograma().trim();
        if (ficha.isEmpty()) {
            return "La ficha o programa de formación es obligatoria.";
        }
        if (ficha.length() > 50) {
            return "La ficha no puede superar los 50 caracteres.";
        }
        if (!PATRON_FICHA.matcher(ficha).matches()) {
            return "La ficha solo admite letras, números, espacios, punto y guion.";
        }
        // Fecha: obligatoria y, en reservas nuevas, no puede estar en el pasado
        if (reserva.getFechaReserva() == null) {
            return "La fecha de la reserva es obligatoria.";
        }
        if (reserva.getIdReserva() == null && reserva.getFechaReserva().isBefore(LocalDate.now())) {
            return "No se puede reservar en una fecha pasada.";
        }
        // Horas: obligatorias y la final debe ser posterior a la inicial
        if (reserva.getHoraInicio() == null || reserva.getHoraFin() == null
                || !reserva.getHoraFin().isAfter(reserva.getHoraInicio())) {
            return "La hora de finalización debe ser posterior a la de inicio.";
        }
        // Cruce de horarios (una reserva cancelada no ocupa la sala)
        if (!"Cancelada".equals(reserva.getEstado())
                && reservaRepository.existeCruce(reserva.getSala().getIdSala(), reserva.getFechaReserva(),
                        reserva.getHoraInicio(), reserva.getHoraFin(),
                        reserva.getIdReserva() == null ? 0 : reserva.getIdReserva())) {
            return "La sala ya está reservada en ese horario. Elige otra franja u otra sala.";
        }
        return null;
    }

    // DELETE: elimina la reserva
    @PostMapping("/reservas/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        reservaRepository.deleteById(id);
        return "redirect:/reservas";
    }

    // Comprobante de la reserva
    @GetMapping("/reservas/comprobante/{id}")
    public String comprobante(@PathVariable Integer id, Model model) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) {
            return "redirect:/reservas";
        }
        model.addAttribute("reserva", reserva);
        return "comprobante";
    }

    // Datos para los selectores del formulario
    private void cargarListas(Model model) {
        model.addAttribute("salas", salaRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
    }
}