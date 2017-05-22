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
import java.util.List;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class UsuariosController extends Controller {

    @Inject FormFactory formFactory;

    @Transactional(readOnly = true)
    public Result listaUsuarios() {
        String aviso = flash("aviso");
        String error = flash("error");
        Logger.debug("Menasaje de aviso: " + aviso);
        Logger.debug("Mensaje de error: " + error);
        List<Usuario> usuarios = UsuariosService.findAllUsuarios();
        return ok(listaUsuarios.render(usuarios, aviso, error));
    }

    public Result formularioNuevoUsuario() {
        return ok(formCreacionUsuario.render(formFactory.form(Usuario.class),""));
    }

    @Transactional
    public Result grabaNuevoUsuario() {
        Form<Usuario> usuarioForm = formFactory.form(Usuario.class).bindFromRequest();
        if (usuarioForm.hasErrors()) {
            return badRequest(formCreacionUsuario.render(usuarioForm, "Hay errores en el formulario"));
        }
        Usuario usuario = usuarioForm.get();
        Logger.debug("Usuario a grabar: " + usuario.toString());

        // Se comprueba que no existe un usuario con el mismo login
        if (UsuariosService.findUsuarioPorLogin(usuario.login) != null) {
            return badRequest(formCreacionUsuario.render(usuarioForm, "Login ya existente"));
        }
        usuario = UsuariosService.grabaUsuario(usuario);
        flash("aviso", "El usuario se ha grabado correctamente");
        return redirect(controllers.routes.UsuariosController.listaUsuarios());
    }

    @Transactional(readOnly = true)
    public Result detalleUsuario(Integer id) {
        Usuario usuario = UsuariosService.findUsuario(id);
        if (usuario == null) {
            return notFound("Usuario no encontrado");
        } else {
            Logger.debug("Encontrado usuario " + usuario.id + ": " + usuario.login);
            return ok(detalleUsuario.render(usuario));
        }
    }


    @Transactional(readOnly = true)
    public Result formularioEditaUsuario(Integer id) {
        Usuario usuario = UsuariosService.findUsuario(id);
        if (usuario == null) {
            return notFound("Usuario no encontrado");
        } else {
            Form<Usuario> usuarioForm = formFactory.form(Usuario.class);
            usuarioForm = usuarioForm.fill(usuario);
            return ok(formModificacionUsuario.render(usuarioForm, ""));
        }
    }

    @Transactional
    public Result grabaUsuarioModificado() {
        Form<Usuario> usuarioForm = formFactory.form(Usuario.class).bindFromRequest();
        if (usuarioForm.hasErrors()) {
            return badRequest(formModificacionUsuario.render(usuarioForm, "Hay errores en el formulario"));
        }
        Usuario usuario = usuarioForm.get();
        Logger.debug("Usuario a grabar: " + usuario.toString());

        // Se comprueba que no existe un usuario con el mismo login
        // y distinto id
        Usuario existente = UsuariosService.findUsuarioPorLogin(usuario.login);
        if (existente != null && existente.id != usuario.id) {
            return badRequest(formModificacionUsuario.render(usuarioForm, "Login ya existente"));
        }

        // Se recupera la contraseña del usuario y se actualiza en el
        // usuario modificado
        Usuario usuarioSinModificar = UsuariosService.findUsuario(usuario.id);
        usuario.password = usuarioSinModificar.password;

        usuario = UsuariosService.modificaUsuario(usuario);
        flash("aviso", "Usuario " + usuario.id + " modificado");
        return redirect(controllers.routes.UsuariosController.listaUsuarios());
    }


    @Transactional
    public Result borraUsuario(Integer id) {
        Logger.debug("Voy a borrar el usuario: " + id);
        UsuariosService.deleteUsuario(id);
        flash("aviso", "Usuario " + id + " borrado correctamente");
        return ok();
    }
}
