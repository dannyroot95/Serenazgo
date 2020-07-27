package com.example.serenazgo.modelos;

public class PoliciaBooking {

    String idPolicia;
    String idPatrulla;
    String destino;
    String origen;
    String tiempo;
    String km;
    String estado;
    double origenLat;
    double origenLng;
    double destinoLat;
    double destinoLng;

    public PoliciaBooking(String idPolicia, String idPatrulla, String destino, String origen,
                          String tiempo, String km, String estado, double origenLat,
                          double origenLng, double destinoLat, double destinoLng) {
        this.idPolicia = idPolicia;
        this.idPatrulla = idPatrulla;
        this.destino = destino;
        this.origen = origen;
        this.tiempo = tiempo;
        this.km = km;
        this.estado = estado;
        this.origenLat = origenLat;
        this.origenLng = origenLng;
        this.destinoLat = destinoLat;
        this.destinoLng = destinoLng;
    }

    public String getIdPolicia() {
        return idPolicia;
    }

    public void setIdPolicia(String idPolicia) {
        this.idPolicia = idPolicia;
    }

    public String getIdPatrulla() {
        return idPatrulla;
    }

    public void setIdPatrulla(String idPatrulla) {
        this.idPatrulla = idPatrulla;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getOrigenLat() {
        return origenLat;
    }

    public void setOrigenLat(double origenLat) {
        this.origenLat = origenLat;
    }

    public double getOrigenLng() {
        return origenLng;
    }

    public void setOrigenLng(double origenLng) {
        this.origenLng = origenLng;
    }

    public double getDestinoLat() {
        return destinoLat;
    }

    public void setDestinoLat(double destinoLat) {
        this.destinoLat = destinoLat;
    }

    public double getDestinoLng() {
        return destinoLng;
    }

    public void setDestinoLng(double destinoLng) {
        this.destinoLng = destinoLng;
    }
}
