package com.proyecto.integrador.backend.app.controller.dto;

import java.util.List;

public class UserIdRequest {
    
    private List<Integer> id;

    public List<Integer> getId() {
        return id;
    }

    public void setId(List<Integer> id) {
        this.id = id;
    }
}
