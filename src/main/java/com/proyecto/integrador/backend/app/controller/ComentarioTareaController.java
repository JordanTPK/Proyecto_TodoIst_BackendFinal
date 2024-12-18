package com.proyecto.integrador.backend.app.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.integrador.backend.app.controller.dto.ComentarioTareaDTO;
import com.proyecto.integrador.backend.app.entity.ComentarioTarea;
import com.proyecto.integrador.backend.app.entity.Tarea;
import com.proyecto.integrador.backend.app.entity.Usuario;
import com.proyecto.integrador.backend.app.repository.ComentarioTareaRepository;
import com.proyecto.integrador.backend.app.repository.TareaRepository;
import com.proyecto.integrador.backend.app.repository.UsuarioRepository;
import com.proyecto.integrador.backend.app.service.ComentarioTareaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comentariosTarea")
public class ComentarioTareaController {
	
	@Autowired
	private ComentarioTareaService comentarioTareaService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TareaRepository tareaRepository;
	
	
	/*@PostMapping("/tarea/")
    public ResponseEntity<?> createComentario(@Valid @RequestBody ComentarioTarea comentario, BindingResult result) {
		if(result.hasFieldErrors()) {
			return validation(result);
		}
		
    	Usuario usuario = getAuthenticatedUsuario();

        ComentarioTarea nuevoComentario = comentarioTareaService.createComentario(comentario , usuario.getId());
        return ResponseEntity.ok(nuevoComentario);
    }*/
	
	@PostMapping("/tarea/")
	public ResponseEntity<?> crearComentario(@Valid @RequestBody ComentarioTareaDTO comentarioDTO) {
	    try {
	        Tarea tarea = tareaRepository.findById(comentarioDTO.getTareaId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La tarea especificada no existe"));

	        Usuario usuario = getAuthenticatedUsuario();
	        ComentarioTarea comentario = new ComentarioTarea();
	        comentario.setComentario(comentarioDTO.getComentario());
	        comentario.setTarea(tarea);
	        comentario.setUsuario(usuario);
	        comentario.setCreadoEn(new Date());

	        ComentarioTarea nuevoComentario = comentarioTareaService.createComentario(comentario , usuario.getId());
	        return ResponseEntity.ok(nuevoComentario);
	    } catch (ResponseStatusException e) {
	        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
	    }
	}

    @GetMapping("/tarea/{tareaId}")
    public ResponseEntity<List<ComentarioTarea>> getComentariosByTareaId(@PathVariable int tareaId) {
        List<ComentarioTarea> comentarios = comentarioTareaService.getComentariosByTareaId(tareaId);
        return ResponseEntity.ok(comentarios);
    }
    
    /*OBTENER EL USUARIO AUTENTICADO DEL SECURITY CONTEXT*/
    private Usuario getAuthenticatedUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    /*VALIDACION */
    private ResponseEntity<?> validation(BindingResult result) {
		Map<String, String> errors = new HashMap<>();
		
		result.getFieldErrors().forEach(err -> {
			errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
		});
		
		return ResponseEntity.badRequest().body(errors);
	}
}
