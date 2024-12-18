package com.proyecto.integrador.backend.app.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ComentarioTareaDTO {
    @NotEmpty(message = "El comentario no puede estar vacío")
    private String comentario;

    @NotNull(message = "El ID de la tarea no puede ser nulo")
    private int tareaId;

    // Getters y setters
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public int getTareaId() {
		return tareaId;
	}

	public void setTareaId(int tareaId) {
		this.tareaId = tareaId;
	}

   
    
}
