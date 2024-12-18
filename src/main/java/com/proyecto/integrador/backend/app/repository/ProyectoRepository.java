package com.proyecto.integrador.backend.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proyecto.integrador.backend.app.entity.Proyecto;
import com.proyecto.integrador.backend.app.entity.Tarea;

public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {
	public abstract Optional<Proyecto> findByTitulo(String titulo);
	public abstract List<Proyecto> findByUsuarioId(int usuarioId);

	@Query("SELECT p FROM Proyecto p " +
		       "WHERE p.usuario.id = :usuarioId " + 
		       "OR :usuarioId IN (SELECT u.id FROM p.usuariosInvitados u)")
		List<Proyecto> findProyectosByUsuarioIdOrInvitadoId(int usuarioId);

}
