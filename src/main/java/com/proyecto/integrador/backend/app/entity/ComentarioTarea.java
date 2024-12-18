package com.proyecto.integrador.backend.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "comentariosTarea")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "proyecto"})
public class ComentarioTarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(min=2, max = 300, message = "debe tener entre 2 y 300 caracteres.")
    @Column(nullable = false)
    private String comentario;

    @Column(name = "creado_en")
    @Temporal(TemporalType.DATE)
    private Date creadoEn;
    
    // @JsonBackReference(value = "usuario-comentariosTarea")
    // @JsonIgnoreProperties({"comentariosTarea", "hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @JsonBackReference(value = "tareas-comentariosTarea")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id", nullable = false)
    private Tarea tarea;
    
    
    public ComentarioTarea() { 
    }

	@PrePersist
    void prePersist(){
        creadoEn = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    
    public Date getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Date creadoEn) {
        this.creadoEn = creadoEn;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }
    
}
