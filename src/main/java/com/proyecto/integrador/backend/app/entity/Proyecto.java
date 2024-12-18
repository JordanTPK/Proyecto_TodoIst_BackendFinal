package com.proyecto.integrador.backend.app.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "proyectos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "tareas", "proyectos"})
public class Proyecto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String titulo;
    
    // @JsonBackReference(value = "usuario-proyectos")
    // @JsonIgnoreProperties({"proyectos", "hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // @JsonManagedReference(value = "proyecto-tareas")
    // @JsonIgnoreProperties({"proyectos", "hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tarea> tareas;
    
    @NotNull(message = "no debe estar vacía")
	@Column(name = "creado_en")
	@Temporal(TemporalType.DATE)
	private Date creadoEn;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "proyectos_usuarios_invitados",
        joinColumns = @JoinColumn(name = "proyecto_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> usuariosInvitados = new ArrayList<>();

    
    public List<Usuario> getUsuariosInvitados() {
		return usuariosInvitados;
	}

	public void setUsuariosInvitados(List<Usuario> usuariosInvitados) {
		this.usuariosInvitados = usuariosInvitados;
	}

	public Date getCreadoEn() {
		return creadoEn;
	}

	public void setCreadoEn(Date creadoEn) {
		this.creadoEn = creadoEn;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
    }
}
