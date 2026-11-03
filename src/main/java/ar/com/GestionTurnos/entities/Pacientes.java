package ar.com.GestionTurnos.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="pacientes")
public class Pacientes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    private String apellido;
    private String nombre;
    private String dni;
    private String direccion;
    private String email;
    private String telefono;
    private String observacion;

    @JoinColumn(name= "id_tiposDoc", referencedColumnName = "id",nullable=false)
    @ManyToOne
    private TiposDoc tipoDoc;

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public TiposDoc getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(TiposDoc tipoDoc) {
        this.tipoDoc = tipoDoc;
    }
}
