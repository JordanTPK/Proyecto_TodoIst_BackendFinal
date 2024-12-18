package com.proyecto.integrador.backend.app.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.integrador.backend.app.entity.ComentarioTarea;
import com.proyecto.integrador.backend.app.entity.Tarea;
import com.proyecto.integrador.backend.app.entity.Usuario;
import com.proyecto.integrador.backend.app.repository.ComentarioTareaRepository;
import com.proyecto.integrador.backend.app.repository.TareaRepository;
import com.proyecto.integrador.backend.app.repository.UsuarioRepository;

@Service
public class ComentarioTareaServiceImpl implements ComentarioTareaService {

	@Autowired
	private ComentarioTareaRepository comentarioTareaRepository;

	@Autowired
	private TareaRepository tareaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public ComentarioTarea createComentario(ComentarioTarea comentario, int usuarioId) {
	    if (comentario == null) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario no puede ser nulo");
	    }

	    System.out.println("Comentario recibido: " + comentario);
	    System.out.println("ID de la tarea: " + (comentario.getTarea() != null ? comentario.getTarea().getId() : "Nulo"));

	    if (comentario.getTarea() == null || comentario.getTarea().getId() <= 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La tarea no puede ser nula o no tiene un ID válido");
	    }

	    // Validar tarea
	    Tarea tarea = tareaRepository.findById(comentario.getTarea().getId())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La tarea especificada no existe"));

	    Usuario usuario = getAuthenticatedUsuario();

	    if (!isUserAuthorized(tarea, usuario)) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La tarea no pertenece al usuario");
	    }

	    comentario.setTarea(tarea);
	    comentario.setUsuario(usuario);
	    comentario.setCreadoEn(new Date());

	    return comentarioTareaRepository.save(comentario);
	}

	@Override
	public List<ComentarioTarea> getComentariosByTareaId(int tareaId) {
		Tarea tarea = tareaRepository.findById(tareaId).orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

		return tarea.getComentarios();
	}

	private Usuario getAuthenticatedUsuario() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		return usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
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

}
