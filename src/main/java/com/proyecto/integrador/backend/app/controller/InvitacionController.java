package com.proyecto.integrador.backend.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.integrador.backend.app.entity.Invitacion;
import com.proyecto.integrador.backend.app.service.ProyectoService;

import java.util.List;

@RestController
@RequestMapping("/invitaciones")
public class InvitacionController {

    @Autowired
    private ProyectoService proyectoService;

    @PostMapping("/{proyectoId}/invitar/{destinatarioId}")
    public ResponseEntity<Invitacion> invitarUsuario(@PathVariable int proyectoId, @PathVariable String invitadoUsername) {
        Invitacion invitacion = proyectoService.invitarUsuario(proyectoId, invitadoUsername);
        return ResponseEntity.ok(invitacion);
    }
 
    @PostMapping("/{invitacionId}/aceptar")
    public ResponseEntity<?> aceptarInvitacion(@PathVariable int invitacionId) {
        proyectoService.aceptarInvitacion(invitacionId);
        return ResponseEntity.ok("Invitación aceptada con éxito");
    }
 
    @GetMapping("/proyectos-invitados/{usuarioId}")
    public ResponseEntity<List<?>> obtenerProyectosInvitados(@PathVariable int usuarioId) {
        return ResponseEntity.ok(proyectoService.findProyectosInvitados(usuarioId));
    }
}