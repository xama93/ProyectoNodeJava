package models;

import java.util.Date;
import javax.persistence.*;
import play.data.validation.Constraints;
import play.data.format.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public String id;
    @Constraints.Required
    public String login;
    public String password;
    public String nombre;
    public String apellidos;
    public String eMail;
    @Formats.DateTime(pattern="dd-MM-yyyy")
    public Date fechaNacimiento;

    // Un constructor vacío necesario para JPA
    public Usuario() {}

    // El constructor principal con los campos obligatorios
    public Usuario(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Sustituye por null todas las cadenas vacías que pueda tener
    // un usuario en sus atributos
    public void nulificaAtributos() {
        if (nombre != null && nombre.isEmpty()) nombre = null;
        if (apellidos != null && apellidos.isEmpty()) apellidos = null;
        if (eMail != null && eMail.isEmpty()) eMail = null;
    }

    public String toString() {
        String fechaStr = null;
        if (fechaNacimiento != null) {
            SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
            fechaStr = formateador.format(fechaNacimiento);
        }
        return String.format("Usuario id: %s login: %s passworld: %s nombre: %s " +
                      "apellidos: %s eMail: %s fechaNacimiento: %s",
                      id, login, password, nombre, apellidos, eMail, fechaStr);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + ((login == null) ? 0 : login.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (getClass() != obj.getClass()) return false;
        Usuario other = (Usuario) obj;
        // Si tenemos los ID, comparamos por ID
        if (id != null && other.id != null)
            return (id == other.id);
        // sino comparamos por campos obligatorios
        else {
            if (login == null) {
                if (other.login != null) return false;
            } else if (!login.equals(other.login)) return false;
            if (password == null) {
                if (other.password != null) return false;
            } else if (!password.equals(other.password)) return false;
        }
        return true;
    }
}
