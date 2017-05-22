package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.*;

import models.*;
import services.*;

import views.html.*;

import javax.inject.*;

public class LoginController extends Controller {

    @Inject FormFactory formFactory;

    public Result formularioRegistro() {
        return ok(formRegistro.render(formFactory.form(Registro.class),""));
    }

    @Transactional
    public Result registroUsuario() {
        Form<Registro> registroForm = formFactory.form(Registro.class).bindFromRequest();
        if (registroForm.hasErrors()) {
            return badRequest(formRegistro.render(registroForm, "Hay errores en el formulario"));
        }
        Registro registro = registroForm.get();
        if (!registro.password.equals(registro.confirmacion)) {
            return badRequest(formRegistro.render(registroForm, "No coincide la contraseña y la confirmación"));
        }
        Usuario existente = UsuariosService.findUsuarioPorLogin(registro.login);
        if (existente == null) {
            LoginService.registraNuevoUsuario(registro.login, registro.password);
            return ok(saludo.render(registro.login + ". Bienvenido"));
        } else if (existente.password == null) {
            LoginService.inicializaPasswordUsuario(registro.login, registro.password);
            return ok(saludo.render(registro.login + ". Contraseña inicializada."));
        } else // existente.password != null
            return badRequest(formRegistro.render(registroForm, "El login ya existe"));
    }

    public Result formularioLogin() {
        return ok(formLogin.render(formFactory.form(Login.class), ""));
    }

    @Transactional(readOnly = true)
    public Result login() {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(formLogin.render(loginForm, "Hay errores en el formulario"));
        }
        Login login = loginForm.get();
        try {
            Usuario usuario = LoginService.login(login.login, login.password);
            return ok(saludo.render(usuario.login + " - " + usuario.nombre));
        } catch (LoginException ex) {
            return badRequest(formLogin.render(loginForm, ex.getMessage()));
        }
    }
}
