package com.proyecto.integrador.backend.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.integrador.backend.app.entity.Proyecto;
import com.proyecto.integrador.backend.app.entity.Tarea;
import com.proyecto.integrador.backend.app.entity.Usuario;
import com.proyecto.integrador.backend.app.repository.ProyectoRepository;
import com.proyecto.integrador.backend.app.repository.TareaRepository;
import com.proyecto.integrador.backend.app.repository.UsuarioRepository;

@Service
public class TareaServiceImpl implements TareaService{

	@Autowired
	private TareaRepository tareaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ProyectoRepository proyectoRepository;
	
	
	
	@Override
	public List<Tarea> findAll() {
		return tareaRepository.findAll();
	}

	@Override
	public List<Tarea> findAllById(int usuarioId) {
		return tareaRepository.findByUsuarioId(usuarioId);
	}

	@Override
    public List<Tarea> findAllByUserIdOrAssignedUserId(int usuarioId) {
        return tareaRepository.findByUsuarioIdOrUsuariosAsignados(usuarioId);
    }

	@Override
	public Optional<Tarea> findById(int id) {
		return tareaRepository.findById(id);
	}

	@Override
	public Tarea create(Tarea tarea, int usuarioId) {
		Usuario usuario = getAuthenticatedUsuario();
		
		
		/*
		// Verificar que el proyecto existe y pertenece al usuario
	    Proyecto proyecto = tarea.getProyecto();
	    if (proyecto == null || proyecto.getId() == 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El proyecto no pertenece al usuario autenticado");
	    }
		*/
		// Validar que el proyecto existe
	    Proyecto proyecto = proyectoRepository.findById(tarea.getProyecto().getId())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El proyecto especificado no existe"));
	    
	    if (proyecto == null || proyecto.getId() == 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "el proyecto no es valido");
	    }
	   
	    // Verificar si el proyecto pertenece al usuario autenticado
	    /*if (proyecto.getUsuario() == null || proyecto.getUsuario().getId() != usuario.getId()) {
	    	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El proyecto no pertenece al usuario autenticado");
	    }*/
	    
	    /*if (!isUserAuthorizedP(proyecto, usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No tienes permiso para crear tareas en este proyecto");
        	}*/
	    
	    // Validar si ya existe una tarea con el mismo título en el proyecto
	    Optional<Tarea> tareaExistente = tareaRepository.findByTituloAndProyecto(tarea.getTitulo(), proyecto);
	    if (tareaExistente.isPresent()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una tarea con el mismo título en este proyecto");
	    }
	   
	    if(tarea.getPrioridad() != null) {
	    	tarea.setEsPrioridad(true);
	    }
	    
	    
		tarea.setUsuario(usuario);
		return tareaRepository.save(tarea);
	}

	@Override
	public Tarea update(int id, Tarea tarea) {
		Usuario usuario = getAuthenticatedUsuario();

		Optional<Tarea> tareaOptional = tareaRepository.findById(id);

		if (tareaOptional.isPresent()) {
			Tarea tareaDb = tareaOptional.orElse(null);

			// verificar si el usuario es propietario de la tarea
			if (!isUserAuthorized(tareaDb, usuario)) {
                throw new RuntimeException("No tienes permiso para actualizar esta tarea");
            }
			
			 // Validaciones para campos vacíos
	        if (tarea.getTitulo() == null || tarea.getTitulo().trim().isEmpty()) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'título' no puede estar vacío");
	        }
	        if (tarea.getDescripcion() == null || tarea.getDescripcion().trim().isEmpty()) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'descripción' no puede estar vacío");
	        }
	        if (tarea.getPrioridad() == null || tarea.getPrioridad().trim().isEmpty()) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'prioridad' no puede estar vacío");
	        }

	        // Validar que el título no sea duplicado (excluyendo la tarea actual)
	        boolean tituloDuplicado = tareaRepository.existsByTituloAndProyectoIdAndIdNot(
	            tarea.getTitulo(),
	            tareaDb.getProyecto().getId(),
	            id
	        );

	        if (tituloDuplicado) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una tarea con el mismo título en este proyecto");
	        }
			
	        tareaDb.setTitulo(tarea.getTitulo());
			tareaDb.setDescripcion(tarea.getDescripcion());
			tareaDb.setFechaFin(tarea.getFechaFin());
			tareaDb.setPrioridad(tarea.getPrioridad());
			tareaDb.setEsPrioridad(tarea.isEsPrioridad());


			return tareaRepository.save(tareaDb);
		}
		
		return null;
	}

	@Override
	public Optional<Tarea> deleteById(int id) {
		Usuario usuario = getAuthenticatedUsuario();

		Optional<Tarea> tareaOptional = tareaRepository.findById(id);

		if (tareaOptional.isPresent()) {
			Tarea tarea = tareaOptional.orElse(null);

			if (!isUserAuthorized(tarea, usuario)) {
                throw new RuntimeException("No tienes permiso para eliminar esta tarea");
            }

			tareaRepository.deleteById(id);
		}
		return tareaOptional;
	}

	@Override
	public List<Tarea> findByProyectoId(int proyectoId) {
		return tareaRepository.findByProyectoId(proyectoId);
	}

	private Usuario getAuthenticatedUsuario() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		return usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	}

	@Override
	public Tarea completed(int id) {
		Usuario usuario = getAuthenticatedUsuario();

		Optional<Tarea> tareaOptional = tareaRepository.findById(id);

		if (tareaOptional.isPresent()) {
			Tarea tarea = tareaOptional.get();

			if (!isUserAuthorized(tarea, usuario)) {
                throw new RuntimeException("No tienes permiso para marcar esta tarea como completada");
            }

			tarea.setCompletado(true);
			return tareaRepository.save(tarea);
		}

		throw new RuntimeException("Tarea no encontrada");
	}

	@Override
    public Tarea assignUsersToTask(int tareaId, List<Integer> userIds) {
        Optional<Tarea> tareaOpt = tareaRepository.findById(tareaId);
        if (!tareaOpt.isPresent()) {
            throw new RuntimeException("Tarea no encontrada");
        }

        Tarea tarea = tareaOpt.get();
        List<Usuario> existingUsuarios = tarea.getUsuariosAsignados();
        List<Usuario> newUsuarios = usuarioRepository.findAllById(userIds);

        for (Usuario usuario : newUsuarios) {
            if (!existingUsuarios.contains(usuario)) {
                existingUsuarios.add(usuario);
            }
        }

        tarea.setUsuariosAsignados(existingUsuarios);
        return tareaRepository.save(tarea);
    }

	private boolean isUserAuthorized(Tarea tarea, Usuario usuario) {
        if (tarea.getUsuario().getId() == usuario.getId()) {
            return true;
        }
        for (Usuario assignedUser : tarea.getUsuariosAsignados()) {
            if (assignedUser.getId() == usuario.getId()) {
                return true;
            }
        }
        return false;
    }
	
	 private boolean isUserAuthorizedP(Proyecto proyecto, Usuario usuario) {
	        if (proyecto.getUsuario().getId() == usuario.getId()) {
	            return true;
	        }
	        for (Usuario assignedUser : proyecto.getUsuariosInvitados()) {
	            if (assignedUser.getId() == usuario.getId()) {
	                return true;
	            }
	        }
	        return false;
	    }

}
