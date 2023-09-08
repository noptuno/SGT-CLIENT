package com.example.dispensadorfirebase.clase;

public class SectoresElegidos {

    Integer idSector;
    String idSectorFirebase;
    int favorito;
    int ultimonumero;
    int habilitarnoti;

    public int getHabilitarnoti() {
        return habilitarnoti;
    }

    public void setHabilitarnoti(int habilitarnoti) {
        this.habilitarnoti = habilitarnoti;
    }

    public SectoresElegidos() {
    }

    public SectoresElegidos(Integer idSector, String idSectorFirebase, int favorito, int ultimonumero, int habilitarnoti) {
        this.idSector = idSector;
        this.idSectorFirebase = idSectorFirebase;
        this.favorito = favorito;
        this.ultimonumero = ultimonumero;
        this.habilitarnoti = habilitarnoti;
    }


    public int getUltimonumero() {
        return ultimonumero;
    }

    public void setUltimonumero(int ultimonumero) {
        this.ultimonumero = ultimonumero;
    }

    public int getFavorito() {
        return favorito;
    }

    public void setFavorito(int favorito) {
        this.favorito = favorito;
    }

    public Integer getIdSector() {
        return idSector;
    }

    public void setIdSector(Integer idSector) {
        this.idSector = idSector;
    }

    public String getIdSectorFirebase() {
        return idSectorFirebase;
    }

    public void setIdSectorFirebase(String idSectorFirebase) {
        this.idSectorFirebase = idSectorFirebase;
    }

    public String toString() {
        return "SectoresElegidos{" +
                "idSector=" +         idSector +
                ", NombreSector='" +  idSectorFirebase + '\'' +
                ", ultimonumer='" +  ultimonumero + '\'' +
                ", habilitarnoti='" +  habilitarnoti + '\'' +
                '}';
    }


}
