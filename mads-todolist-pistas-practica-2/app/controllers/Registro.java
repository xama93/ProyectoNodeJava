package controllers;

import play.data.validation.Constraints;

public class Registro {
    @Constraints.Required
    public String login;
    @Constraints.Required
    public String password;
    @Constraints.Required
    public String confirmacion;
}
