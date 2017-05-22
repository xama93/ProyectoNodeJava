package controllers;

import play.data.validation.Constraints;

public class Login {
    @Constraints.Required
    public String login;
    @Constraints.Required
    public String password;
}
