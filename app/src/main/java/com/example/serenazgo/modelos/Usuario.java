package com.example.serenazgo.modelos;

public class Usuario {

    String id;
    String nombre;
    String cip;
    String mail;

    public Usuario() {
    }

    public Usuario(String id, String nombre, String cip, String mail) {
        this.id = id;
        this.nombre = nombre;
        this.cip = cip;
        this.mail = mail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
