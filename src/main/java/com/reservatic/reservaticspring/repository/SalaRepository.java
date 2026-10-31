package com.reservatic.reservaticspring.repository;

import com.reservatic.reservaticspring.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Integer> {
}