package ar.com.GestionTurnos.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="profesionales")
public class Profesionales implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String apellido;
    private String nombre;
    private String dni;
    private String matricula;
    private String telefono;

    @JoinColumn(name= "id_tipoDoc", referencedColumnName = "id",nullable=false)
    @ManyToOne
    private TiposDoc tipoDoc;

    @JoinColumn(name= "id_especialidad", referencedColumnName = "id",nullable=false)
    @ManyToOne
    private Especialidades especialidad;

    @JoinColumn(name= "id_usuario", referencedColumnName = "id", nullable=true)
    @ManyToOne
    private Usuarios usuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public TiposDoc getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(TiposDoc tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public Especialidades getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidades especialidad) {
        this.especialidad = especialidad;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }
}
