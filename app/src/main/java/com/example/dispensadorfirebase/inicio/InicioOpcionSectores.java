package com.example.dispensadorfirebase.inicio;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLASECTORES;
import static com.example.dispensadorfirebase.app.variables.PREF_ANYDESK;
import static com.example.dispensadorfirebase.app.variables.PREF_CLIENTE;
import static com.example.dispensadorfirebase.app.variables.PREF_COMPLETADO;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURACIONDMR;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURAR;
import static com.example.dispensadorfirebase.app.variables.PREF_DISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_ID;
import static com.example.dispensadorfirebase.app.variables.PREF_IDLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBRELOCALSELECCIONADO;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBREUBICACIONDISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_UID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterSectorLocal;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.example.dispensadorfirebase.aplicaciones.supervisor.Supervisor_Principal;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.Sectores;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InicioOpcionSectores extends AppCompatActivity {


    private static String NOMBREUBICACIONDISPOSITIVO = "NO";
    private static String NOMBRELOCALSELECCIONADO = "NO";
    private static String IDDISPOSITIVO = "NO";
    private static String ANYDESK = "NO";
    private static String ESTADOCONFIGURACION = "NO" ;
    private static String DISPOSITIVO = "NO";
    private static String CLIENTE = "NO";
    private static String IDLOCAL = "NO";
    private static String LOGOLOCAL="NO";

    private boolean listenerFirebaseCargado = false;

    ArrayList<Sectores> listnombresectores;
    ArrayList<SectorLocal> listsectoreslocal;
    Button RegistroSectores,btnAsignar;
    AdapterSectorLocal adapter;
    AlertDialog Adialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferencesectores;
    private SectorDB db;
    private Button configurar;
    TextView cantidadsectoreselegidos;
    private int cantidadelegida = 0;
    private int cantidadmaxima = 0;
    private TextView maximoSectores;
    ActionBar actionBar;
    private TextView localseleccionado, dispositivoseleccionado;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_opcion_sectores);

        configurar = findViewById(R.id.btnGuardarConfig);
        localseleccionado = findViewById(R.id.txtlocal);
        dispositivoseleccionado= findViewById(R.id.txtdispositivo);
        maximoSectores= findViewById(R.id.txtmaximosectores);
        cantidadsectoreselegidos= findViewById(R.id.txtcantelegidos);
        listnombresectores = new ArrayList<>();
        listsectoreslocal = new ArrayList<>();


        adapter = new AdapterSectorLocal();
        ocultarbarra();
        eliminarSectoresElegidos();

        configurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cantidadmaxima>= cantidadelegida){
                    Intent intent = null;
                    if (DISPOSITIVO.equals("DISPLAY 21PLG")){
                        intent  = new Intent(InicioOpcionSectores.this, DisplayGrande.class);
                    }else if (DISPOSITIVO.equals("TABLET 10PLG")){
                         intent = new Intent(InicioOpcionSectores.this, TabletDispensador.class);
                    }
                    else if (DISPOSITIVO.equals("DISPENSADOR")){
                         intent = new Intent(InicioOpcionSectores.this, DispensadorTurno.class);

                    } else if (DISPOSITIVO.equals("SUPERVISOR")){

                         intent = new Intent(InicioOpcionSectores.this, Supervisor_Principal.class);
                    }
                    pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(PREF_COMPLETADO, "SI");
                    editor.apply();
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    InicioOpcionSectores.this.finish();

                }else{
                    Toast.makeText(InicioOpcionSectores.this, "Debe Elegir menos Sectores para Este Dispositivo", Toast.LENGTH_LONG).show();
                }

            }
        });
        adapter.setOnDetailListener(new AdapterSectorLocal.OnNoteDetailListener() {
            @Override
            public void onDetail(SectorLocal note) {

                //crear base datos local

                SectoresElegidos sectorelegido = new SectoresElegidos();
                sectorelegido.setIdSectorFirebase(IDLOCAL+note.getIdsector());
                sectorelegido.setUltimonumero(note.getNumeroatendiendo());
                sectorelegido.setHabilitarnoti(0);
                registrarSectorElegido(sectorelegido);
                mostrarBaseLocalSectoresElegidos();

                //aqui uso el habilitador para guardar en el dispositivo los sectores que va a utilizar

               // databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRELOCALSELECCIONADO).child("SECTORES").child(note.getNombreSector()).setValue(note);

            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerelegirsector);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        cargarConfiguracion();



        limitesectores();

    }

    private void log(String msg){
        Log.e("ENTRADAS",msg);
    }
    private void cargarConfiguracion() {

        pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        CLIENTE= pref.getString(PREF_CLIENTE, "NO");
        DISPOSITIVO = pref.getString(PREF_DISPOSITIVO, "NO");
        NOMBRELOCALSELECCIONADO = pref.getString(PREF_NOMBRELOCALSELECCIONADO, "NO");
        IDLOCAL = pref.getString(PREF_IDLOCAL, "NO");
        ESTADOCONFIGURACION = pref.getString(PREF_COMPLETADO,"NO");

        localseleccionado.setText(NOMBRELOCALSELECCIONADO);
        dispositivoseleccionado.setText(DISPOSITIVO);

        if (CLIENTE.equals("NO") || DISPOSITIVO.equals("NO") || IDLOCAL.equals("NO")){
            SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREF_COMPLETADO, "NO");

            editor.apply();
            finish();
        }else{
            log("Sectores abrio");
            inicializarFirebase();
        }

    }

    private void ocultarbarra() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    private void limitesectores() {


        if (DISPOSITIVO.equals("DISPLAY 21PLG")){
            cantidadmaxima = 3;
            maximoSectores.setText("3");

        } else if (DISPOSITIVO.equals("DISPLAY 15PLG")){
            cantidadmaxima = 1;
            maximoSectores.setText("1");

        }else if (DISPOSITIVO.equals("TABLET 10PLG")){
            cantidadmaxima = 1;
            maximoSectores.setText("1");

        }
        else if (DISPOSITIVO.equals("DISPENSADOR")){
            cantidadmaxima = 3;
            maximoSectores.setText("3");

        } else if (DISPOSITIVO.equals("SUPERVISOR")){
            cantidadmaxima = 6;
            maximoSectores.setText("6");

        }

    }


    private void cargarListenerFirebase() {

        setProgressDialog();
        listenerFirebaseCargado = true;
        this.databaseReferencesectores.addListenerForSingleValueEvent(Firebaselistener);

    }

    public boolean registrarSectorElegido(SectoresElegidos sectorElegido) {

        try {
            db = new SectorDB(this);

            if (db.validar(sectorElegido.getIdSectorFirebase())){
                db.eliminarSector(sectorElegido.getIdSectorFirebase());
                cantidadelegida--;
            }else{
                cantidadelegida++;
                db.insertarSector(sectorElegido);
            }
            cantidadsectoreselegidos.setText("" + cantidadelegida);

            return true;

        } catch (Exception e) {
            Log.e("error", "mensaje registro o eliminar");
            return false;
        }

    }
    public boolean eliminarSectoresElegidos() {

        try {
            db = new SectorDB(this);
                db.eliminarAll();

            return true;

        } catch (Exception e) {
            Log.e("error", "mensaje eliminar all");
            return false;
        }

    }


    void mostrarBaseLocalSectoresElegidos(){

        try {
            db = new SectorDB(this);
            ArrayList<SectoresElegidos> list = db.loadSector();
            for (SectoresElegidos sectores : list) {

                Log.i("---> Base de datos: ", sectores.toString());

            }

        } catch (Exception e) {
            Log.e("error", "mensaje mostrar bse local");
        }
    }

    public void actualizarReciclerView() {

        cantidadsectoreselegidos.setText("" + cantidadelegida);
        adapter.setNotes(listsectoreslocal);
        adapter.notifyDataSetChanged();
    }


    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(ll);

        Adialog = builder.create();
        Adialog.show();


        Window window = Adialog.getWindow();

        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Adialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            Adialog.getWindow().setAttributes(layoutParams);
        }



    }

    private ValueEventListener Firebaselistener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            listsectoreslocal.clear();
            eliminarSectoresElegidos();
            cantidadelegida=0;

            for (DataSnapshot objSnaptshot : snapshot.getChildren()){

                SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                if (sectores!=null){
                    if (sectores.getEstado()==1){
                        SectoresElegidos sectorElegido = new SectoresElegidos();
                        String idsector = IDLOCAL+sectores.getIdsector();
                        sectorElegido.setIdSectorFirebase(idsector);
                        int ultimonumero = sectores.getNumeroatendiendo();
                        sectorElegido.setUltimonumero(ultimonumero);
                        sectorElegido.setHabilitarnoti(0);
                        registrarSectorElegido(sectorElegido);
                        listsectoreslocal.add(sectores);
                    }
                }
            }

            Adialog.dismiss();
            actualizarReciclerView();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(InicioOpcionSectores.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
            Adialog.dismiss();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        if (Adialog!=null){
            Adialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Firebaselistener != null) {
            if (listenerFirebaseCargado){
                databaseReferencesectores.removeEventListener(Firebaselistener);
            }
        }
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencesectores = firebaseDatabase.getReference(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES);
        cargarListenerFirebase();
    }



}