package com.example.loanbot.service;

public class ManagerAuthService {

    private final String managerLogin;
    private final String managerPassword;

    public ManagerAuthService(String managerLogin, String managerPassword) {
        this.managerLogin = managerLogin;
        this.managerPassword = managerPassword;
    }

    public boolean authenticate(String login, String password) {
        return managerLogin != null
                && managerPassword != null
                && managerLogin.equals(login)
                && managerPassword.equals(password);
    }
}
