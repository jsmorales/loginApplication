package com.example.johanmorales.loginapplication.Models;

import java.io.Serializable;

public class Resultado implements Serializable {

    private String token;
    private Employee employee;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

}
