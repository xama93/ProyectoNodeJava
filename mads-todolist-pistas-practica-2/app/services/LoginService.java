package services;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

import models.*;


public class LoginService {

    public static Usuario login(String login, String password) {
        Usuario usuario = UsuarioDAO.findUsuarioPorLogin(login);
        if (usuario == null)
            throw new LoginException("No existe usuario");
        if (!password.equals(usuario.password))
            throw new LoginException("Contraseña incorrecta");
        return usuario;
    }

    public static Usuario inicializaPasswordUsuario(String login, String password) {
        Usuario usuario = UsuarioDAO.findUsuarioPorLogin(login);
        if (usuario == null)
            throw new LoginException("Imposible inicializar password: no existe login");
        if (usuario.password != null)
            throw new LoginException("Imposible inicializar password: ya existe password");
        usuario.password = password;
        return UsuarioDAO.update(usuario);
    }

    public static Usuario registraNuevoUsuario(String login, String password) {
        Usuario usuarioIgualLogin = UsuarioDAO.findUsuarioPorLogin(login);
        if (usuarioIgualLogin != null)
            throw new LoginException("Imposible registrar un usuario: login ya existe");
        Usuario usuario = new Usuario(login, password);
        return UsuariosService.grabaUsuario(usuario);
    }
}
