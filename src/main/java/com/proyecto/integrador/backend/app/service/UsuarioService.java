package com.proyecto.integrador.backend.app.service;

import java.util.List;

import com.proyecto.integrador.backend.app.entity.Usuario;

public interface UsuarioService {

    public abstract List<Usuario> findAll();

}
