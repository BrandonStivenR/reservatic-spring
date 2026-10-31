package com.reservatic.reservaticspring.repository;

import com.reservatic.reservaticspring.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data genera automáticamente las consultas básicas. */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}