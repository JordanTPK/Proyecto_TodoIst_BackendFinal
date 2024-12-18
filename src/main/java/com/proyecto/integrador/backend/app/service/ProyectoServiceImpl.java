package com.proyecto.integrador.backend.app.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.proyecto.integrador.backend.app.entity.Invitacion;
import com.proyecto.integrador.backend.app.entity.Proyecto;
import com.proyecto.integrador.backend.app.entity.Tarea;
import com.proyecto.integrador.backend.app.entity.Usuario;
import com.proyecto.integrador.backend.app.exception.dto.ProyectoAlreadyExistsException;
import com.proyecto.integrador.backend.app.repository.InvitacionRepository;
import com.proyecto.integrador.backend.app.repository.ProyectoRepository;
import com.proyecto.integrador.backend.app.repository.UsuarioRepository;

@Service
public class ProyectoServiceImpl implements ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InvitacionRepository invitacionRepository;

    private static final String PROJECT_NOT_FOUND = "Proyecto no encontrado";
    private static final String USER_NOT_FOUND = "Usuario no encontrado";

    @Override
    public List<Proyecto> findAll() {
        return proyectoRepository.findAll();
    }

    @Override
    public Optional<Proyecto> findById(int id) {
        return proyectoRepository.findById(id);
    }

    @Override
    public List<Proyecto> findAllByIdUser(int usuarioId) {
        return proyectoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Proyecto create(Proyecto proyecto, int usuarioId) {
        Usuario usuario = getAuthenticatedUsuario();

        proyectoRepository.findByTitulo(proyecto.getTitulo()).ifPresent(existing -> {
            throw new ProyectoAlreadyExistsException("Ya existe un proyecto con el titulo: " + proyecto.getTitulo());
        });

        proyecto.setUsuario(usuario);
        return proyectoRepository.save(proyecto);
    }

    @Override
    public Invitacion invitarUsuario(int proyectoId, String invitadoUsername) {
        // Obtener el proyecto por ID
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Obtener el usuario invitado por su nombre de usuario
        Usuario usuarioInvitado = usuarioRepository.findByUsername(invitadoUsername)
                .orElseThrow(() -> new RuntimeException("Usuario invitado no encontrado"));

        // Verificar si el usuario ya ha sido invitado al proyecto
        Optional<Invitacion> invitacionExistente = invitacionRepository.findByProyectoIdAndDestinatarioId(proyectoId, usuarioInvitado.getId());
        if (invitacionExistente.isPresent()) {
            throw new RuntimeException("El usuario ya ha sido invitado a este proyecto");
        }

        // Obtener el usuario autenticado como remitente
        Usuario usuarioRemitente = getAuthenticatedUsuario();

        // Crear y guardar la nueva invitación
        Invitacion nuevaInvitacion = new Invitacion();
        nuevaInvitacion.setProyecto(proyecto);
        nuevaInvitacion.setDestinatario(usuarioInvitado);
        nuevaInvitacion.setRemitente(usuarioRemitente); // Aquí se asigna el remitente
        nuevaInvitacion.setAceptada(false); // La invitación se crea como no aceptada por defecto

        return invitacionRepository.save(nuevaInvitacion);
    }

    @Override
    public List<Proyecto> findProyectosInvitados(int usuarioId) {
        return invitacionRepository.findByDestinatarioIdAndAceptadaFalse(usuarioId)
                .stream()
                .map(Invitacion::getProyecto)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void aceptarInvitacion(int invitacionId) {
        Invitacion invitacion = invitacionRepository.findById(invitacionId)
                .orElseThrow(() -> new RuntimeException("Invitación no encontrada"));
        invitacion.setAceptada(true);
        invitacionRepository.save(invitacion);
    }
    
    @Override
    public List<Proyecto> findProyectosAceptadosPorUsuario(int usuarioId) {
        List<Invitacion> invitacionesAceptadas = invitacionRepository.findByDestinatarioIdAndAceptadaTrue(usuarioId);
        return invitacionesAceptadas.stream()
                .map(Invitacion::getProyecto) // Obtener los proyectos de cada invitación
                .distinct() // Evitar duplicados
                .collect(Collectors.toList());
    }
    
    @Override
    public Proyecto update(int id, Proyecto proyecto) {
        return proyectoRepository.findById(id)
                .map(existingProyecto -> {
                    existingProyecto.setTitulo(proyecto.getTitulo());
                    return proyectoRepository.save(existingProyecto);
                })
                .orElseThrow(() -> new RuntimeException(PROJECT_NOT_FOUND));
    }

    @Override
    public Optional<Proyecto> deleteById(int id) {
        Optional<Proyecto> existingProyecto = proyectoRepository.findById(id);
        existingProyecto.ifPresent(proyecto -> proyectoRepository.deleteById(id));
        return existingProyecto;
    }

  
    private Usuario getAuthenticatedUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
    }

	@Override
	public List<Proyecto> findAllByUserIdOrInvitedUserId(int usuarioId) {
		return proyectoRepository.findProyectosByUsuarioIdOrInvitadoId(usuarioId);
	}
	

	 
   
    
}
