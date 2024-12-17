package com.proyecto.integrador.backend.app.service;

import java.util.List;

import com.proyecto.integrador.backend.app.entity.ComentarioTarea;

public interface ComentarioTareaService {

	ComentarioTarea createComentario(ComentarioTarea comentario, int usuarioId);
    List<ComentarioTarea> getComentariosByTareaId(int tareaId);
}
