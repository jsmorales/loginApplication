package com.example.johanmorales.loginapplication.Models;


import java.io.Serializable;

public class Respuesta implements Serializable {

    private Boolean succes;
    private String message;
    private Resultado result;


    public Boolean getSucces() {
        return succes;
    }

    public void setSucces(Boolean succes) {
        this.succes = succes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Resultado getResult() {
        return result;
    }

    public void setResult(Resultado result) {
        this.result = result;
    }

}
