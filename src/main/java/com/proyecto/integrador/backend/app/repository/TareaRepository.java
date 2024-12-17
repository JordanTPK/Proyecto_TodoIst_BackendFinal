package com.proyecto.integrador.backend.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proyecto.integrador.backend.app.entity.Proyecto;
import com.proyecto.integrador.backend.app.entity.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {
	
	public abstract List<Tarea> findByUsuarioId(int usuarioId);
	Page<Tarea> findByUsuarioId(int usuarioId, Pageable pageable);
	
	//buscar por el titulo y idproyecto
	public Optional<Tarea> findByTituloAndProyecto(String titulo, Proyecto idProyecto);

	public List<Tarea> findByProyectoId(int proyectoId);
	
	//boolean existsByTituloAndUsuarioIdAndIdNot(String titulo, int usuarioId, int id);
	
	boolean existsByTituloAndProyectoIdAndIdNot(String titulo, int proyectoId, int id);

	@Query("SELECT t FROM Tarea t WHERE t.usuario.id = :usuarioId OR :usuarioId IN (SELECT u.id FROM t.usuariosAsignados u)")
    List<Tarea> findByUsuarioIdOrUsuariosAsignados(int usuarioId);

}
