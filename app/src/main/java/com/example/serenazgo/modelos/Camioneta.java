package com.example.serenazgo.modelos;

public class Camioneta {

    String id;
    String nombre;
    String cip;
    String mail;
    String marca;
    String placa;

    public Camioneta(String id, String nombre, String cip, String marca, String placa, String mail) {
        this.id = id;
        this.nombre = nombre;
        this.cip = cip;
        this.mail = mail;
        this.marca = marca;
        this.placa = placa;
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
