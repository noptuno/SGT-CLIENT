package com.example.dispensadorfirebase.basedatossectoreselegidos;

public class ConstantsDB {
    //General
    public static final String DB_NAME = "SECTORESELEGIDOS10.db";
    public static final int DB_VERSION = 10;



    //TABLAPRODUCTO

    public static final String TABLA_SECTORESELEGIDOS = "sectoreselegidos";
    public static final String SEC_IDSECTOR = "_idsector";
    public static final String SEC_NOMBRE = "nombre";
    public static final String SEC_NUMEROELEGIDO = "numeroelegido";
    public static final String SEC_HABILITARNOTI = "habilitarnoti";



    public static final String TABLA_SECTORESELEGIDOS_SQL =
            "CREATE TABLE  " + TABLA_SECTORESELEGIDOS + "(" +
                    SEC_IDSECTOR+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SEC_NOMBRE+ " TEXT," +
                    SEC_NUMEROELEGIDO+ " INTEGER," +
                    SEC_HABILITARNOTI  + " INTEGER);" ;
}
