package com.proyecto.integrador.backend.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.integrador.backend.app.entity.Invitacion;

public interface InvitacionRepository extends JpaRepository<Invitacion, Integer> {
    List<Invitacion> findByDestinatarioIdAndAceptadaFalse(int destinatarioId);
    List<Invitacion> findByRemitenteIdAndAceptadaTrue(int remitenteId);
    Optional<Invitacion> findByProyectoIdAndDestinatarioId(int proyectoId, int destinatarioId);
    List<Invitacion> findByDestinatarioIdAndAceptadaTrue(int usuarioId);
}