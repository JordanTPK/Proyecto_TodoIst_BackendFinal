package com.proyecto.integrador.backend.app.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tareas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "tarea", "usuario", "tareasAsignadas"})
public class Tarea {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotEmpty(message = "no debe estar vacío")
	private String titulo;
	
	@NotEmpty(message = "no debe estar vacía")
	private String descripcion;
	
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
	@NotNull(message = "no debe estar vacía")
	@Column(name = "fecha_fin")
	@Temporal(TemporalType.DATE)
	private Date fechaFin;
	
	private boolean completado = false;
	
	private boolean esPrioridad = false;

	// @NotEmpty(message = "no debe estar vacía")
	// private String estado;

    private String prioridad;

	@Column(name = "creado_en")
	@Temporal(TemporalType.DATE)
	private Date creadoEn;
	
    // @JsonBackReference(value = "usuario-tareas")
	// @JsonIgnoreProperties({"tareas","hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

    // @JsonBackReference(value = "proyecto-tareas")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = true)
    private Proyecto proyecto; 
    
    // @JsonBackReference(value = "tareas-comentariosTarea")
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ComentarioTarea> comentarios = new ArrayList<>();

    // @JsonIgnoreProperties({"tareasAsignadas", "hibernateLazyInitializer", "handler"})
    @ManyToMany
    @JoinTable(
        name = "tarea_usuarios",
        joinColumns = @JoinColumn(name = "tarea_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> usuariosAsignados = new ArrayList<>();   

    public List<Usuario> getUsuariosAsignados() {
        return usuariosAsignados;
    }

    public void setUsuariosAsignados(List<Usuario> usuariosAsignados) {
        this.usuariosAsignados = usuariosAsignados;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    // public String getEstado() {
    //     return estado;
    // }

    // public void setEstado(String estado) {
    //     this.estado = estado;
    // }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
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

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
	}

	public boolean isEsPrioridad() {
		return esPrioridad;
	}

	public void setEsPrioridad(boolean esPrioridad) {
		this.esPrioridad = esPrioridad;
	}
	
	public List<ComentarioTarea> getComentarios() {
	    return comentarios;
	}

	public void setComentarios(List<ComentarioTarea> comentarios) {
	    this.comentarios = comentarios;
	}
    
    
}
