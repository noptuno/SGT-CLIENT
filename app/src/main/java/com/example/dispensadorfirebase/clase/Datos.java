package com.example.dispensadorfirebase.clase;

import java.io.Serializable;

public class Datos implements Serializable {

            int numeroatendiendo;
            int ultimoNumeroDispensador;
            int cantidadEspera;
            int limite;
            int numeroDispensador;
            String NombreSector;
            String ColorSector;
            int notificacion ;
            int notificaciondeshabilitar;

            int llamarsupervisor;

    public int getLlamarsupervisor() {
        return llamarsupervisor;
    }

    public void setLlamarsupervisor(int llamarsupervisor) {
        this.llamarsupervisor = llamarsupervisor;
    }

    public Datos(int numeroatendiendo, int ultimoNumeroDispensador, int cantidadEspera, int limite, int numeroDispensador, String NombreSector, String colorSector, int notificacion, int notificaciondeshabilitar, int llamarsupervisor) {
        this.numeroatendiendo = numeroatendiendo;
        this.ultimoNumeroDispensador = ultimoNumeroDispensador;
        this.cantidadEspera = cantidadEspera;
        this.limite = limite;
        this.numeroDispensador = numeroDispensador;
        this.NombreSector = NombreSector;
        this.ColorSector = colorSector;
        this.notificacion = notificacion;
        this.notificaciondeshabilitar = notificaciondeshabilitar;
        this.llamarsupervisor = llamarsupervisor;
    }

    public int getNotificaciondeshabilitar() {
        return notificaciondeshabilitar;
    }

    public void setNotificaciondeshabilitar(int notificaciondeshabilitar) {
        this.notificaciondeshabilitar = notificaciondeshabilitar;
    }

    public int getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(int notificacion) {
        this.notificacion = notificacion;
    }

    public String getColorSector() {
        return ColorSector;
    }

    public void setColorSector(String colorSector) {
        ColorSector = colorSector;
    }

    public int getNumeroatendiendo() {
        return numeroatendiendo;
    }

    public void setNumeroatendiendo(int numeroatendiendo) {
        this.numeroatendiendo = numeroatendiendo;
    }

    public int getUltimoNumeroDispensador() {
        return ultimoNumeroDispensador;
    }

    public void setUltimoNumeroDispensador(int ultimoNumeroDispensador) {
        this.ultimoNumeroDispensador = ultimoNumeroDispensador;
    }

    public int getNumeroDispensador() {
        return numeroDispensador;
    }

    public void setNumeroDispensador(int numeroDispensador) {
        this.numeroDispensador = numeroDispensador;
    }

    public String getNombreSector() {
        return NombreSector;
    }

    public void setNombreSector(String nombreSector) {
        NombreSector = nombreSector;
    }

    public Datos() {
    }

    public boolean sumar(){

        boolean a = false;

        if (cantidadEspera>0){

            if (numeroatendiendo==99){

                this.numeroatendiendo = 1;
                this.cantidadEspera--;

            }else {
                this.numeroatendiendo++;
                this.cantidadEspera--;

            }

            a = true;
        }



        return a;




    }

    public void sumarDispensdor(){


        if (numeroDispensador == 99){

            this.cantidadEspera++;
            this.numeroDispensador = 1;
            this.ultimoNumeroDispensador = 99;

        }else{
            this.cantidadEspera++;
            this.numeroDispensador++;
            this.ultimoNumeroDispensador = numeroDispensador-1;

        }




    }


    public boolean restar(){


        boolean a = false;

        if (cantidadEspera<ultimoNumeroDispensador){

            if (numeroatendiendo<2){

                this.numeroatendiendo = 99;
                this.cantidadEspera++;

            }else {
                this.numeroatendiendo--;
                this.cantidadEspera++;

            }

            a = true;
        }


        return a;


    }
    public void reset(){

        this.numeroatendiendo=0;
        this.cantidadEspera=0;
        this.ultimoNumeroDispensador = 0;
        this.numeroDispensador=1;
        this.notificacion=0;
        this.notificaciondeshabilitar=0;
    }


    public int getCantidadEspera() {
        return cantidadEspera;
    }

    public void setCantidadEspera(int cantidadEspera) {
        this.cantidadEspera = cantidadEspera;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }
}
