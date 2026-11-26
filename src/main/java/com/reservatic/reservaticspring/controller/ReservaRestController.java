package com.reservatic.reservaticspring.controller;

import com.reservatic.reservaticspring.model.Reserva;
import com.reservatic.reservaticspring.model.Sala;
import com.reservatic.reservaticspring.model.Usuario;
import com.reservatic.reservaticspring.repository.ReservaRepository;
import com.reservatic.reservaticspring.repository.SalaRepository;
import com.reservatic.reservaticspring.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

/** API REST del módulo de reservas: entrega y recibe JSON para el front-end en React. */
@RestController
@RequestMapping("/api")
public class ReservaRestController {

    // Mismas reglas que el controlador MVC (ReservaController)
    private static final Pattern PATRON_FICHA =
            Pattern.compile("^[A-Za-z0-9ÁÉÍÓÚÜáéíóúüÑñ .\\-]+$");
    private static final List<String> ESTADOS = List.of("Confirmada", "Cancelada", "Pendiente");

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaRestController(ReservaRepository reservaRepository,
                                 SalaRepository salaRepository,
                                 UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ---- Objetos de transferencia (DTO): definen el JSON que ve React ----
    public record ReservaSalida(Integer id, Integer idSala, String sala, Integer idUsuario,
                                String usuario, String ficha, String fecha,
                                String horaInicio, String horaFin, String estado) {}

    public record ReservaEntrada(Integer idSala, Integer idUsuario, String ficha, String fecha,
                                 String horaInicio, String horaFin, String estado) {}

    public record Opcion(Integer id, String nombre) {}

    public record Mensaje(String mensaje) {}

    // GET /api/reservas: lista todas las reservas
    @GetMapping("/reservas")
    public List<ReservaSalida> listar() {
        return reservaRepository.findAll().stream().map(this::aSalida).toList();
    }

    // GET /api/salas: opciones para el selector de sala
    @GetMapping("/salas")
    public List<Opcion> salas() {
        return salaRepository.findAll().stream()
                .map(s -> new Opcion(s.getIdSala(), s.getNombreSala())).toList();
    }

    // GET /api/usuarios: opciones para el selector de usuario
    @GetMapping("/usuarios")
    public List<Opcion> usuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> new Opcion(u.getIdUsuario(), u.getNombreCompleto())).toList();
    }

    // POST /api/reservas: valida y guarda una reserva nueva
    @PostMapping("/reservas")
    public ResponseEntity<?> crear(@RequestBody ReservaEntrada datos) {
        Sala sala = datos.idSala() == null ? null : salaRepository.findById(datos.idSala()).orElse(null);
        Usuario usuario = datos.idUsuario() == null ? null : usuarioRepository.findById(datos.idUsuario()).orElse(null);
        LocalDate fecha = leerFecha(datos.fecha());
        LocalTime inicio = leerHora(datos.horaInicio());
        LocalTime fin = leerHora(datos.horaFin());
        String ficha = datos.ficha() == null ? "" : datos.ficha().trim();
        String estado = (datos.estado() == null || datos.estado().isBlank()) ? "Confirmada" : datos.estado();

        String error = validar(sala, usuario, ficha, fecha, inicio, fin, estado);
        if (error != null) {
            return ResponseEntity.badRequest().body(new Mensaje(error));
        }

        Reserva reserva = new Reserva();
        reserva.setSala(sala);
        reserva.setUsuario(usuario);
        reserva.setFichaPrograma(ficha);
        reserva.setFechaReserva(fecha);
        reserva.setHoraInicio(inicio);
        reserva.setHoraFin(fin);
        reserva.setEstado(estado);
        return ResponseEntity.status(HttpStatus.CREATED).body(aSalida(reservaRepository.save(reserva)));
    }

    /** Devuelve el mensaje de error o null si todo está bien. */
    private String validar(Sala sala, Usuario usuario, String ficha, LocalDate fecha,
                           LocalTime inicio, LocalTime fin, String estado) {
        if (sala == null || usuario == null) return "Seleccione una sala y un usuario válidos.";
        if (ficha.isEmpty()) return "La ficha o programa de formación es obligatoria.";
        if (ficha.length() > 50) return "La ficha no puede superar los 50 caracteres.";
        if (!PATRON_FICHA.matcher(ficha).matches())
            return "La ficha solo admite letras, números, espacios, punto y guion.";
        if (fecha == null) return "La fecha de la reserva es obligatoria.";
        if (fecha.isBefore(LocalDate.now())) return "No se puede reservar en una fecha pasada.";
        if (inicio == null || fin == null || !fin.isAfter(inicio))
            return "La hora de finalización debe ser posterior a la de inicio.";
        if (!ESTADOS.contains(estado)) return "El estado no es válido.";
        if (!"Cancelada".equals(estado)
                && reservaRepository.existeCruce(sala.getIdSala(), fecha, inicio, fin, 0)) {
            return "La sala ya está reservada en ese horario. Elige otra franja u otra sala.";
        }
        return null;
    }

    // Convierte la entidad al formato que espera React
    private ReservaSalida aSalida(Reserva r) {
        return new ReservaSalida(r.getIdReserva(), r.getSala().getIdSala(), r.getSala().getNombreSala(),
                r.getUsuario().getIdUsuario(), r.getUsuario().getNombreCompleto(), r.getFichaPrograma(),
                r.getFechaReserva().toString(), r.getHoraInicio().toString(),
                r.getHoraFin().toString(), r.getEstado());
    }

    private LocalDate leerFecha(String texto) {
        try { return texto == null ? null : LocalDate.parse(texto); }
        catch (DateTimeParseException e) { return null; }
    }

    private LocalTime leerHora(String texto) {
        try { return texto == null ? null : LocalTime.parse(texto); }
        catch (DateTimeParseException e) { return null; }
    }
}
