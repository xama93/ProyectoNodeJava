package controllers;

import play.mvc.*;

import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class ApplicationController extends Controller {

  public Result saludo(String nombre) {
      return ok(saludo.render(nombre));
  }
}
