package com.example.dispensadorfirebase.aplicaciones;


import static com.example.dispensadorfirebase.app.variables.EcargarDatos;
import static com.example.dispensadorfirebase.app.variables.EcargarDatosLogos;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLAERROR;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLASECTORES;
import static com.example.dispensadorfirebase.app.variables.PREF_ANYDESK;
import static com.example.dispensadorfirebase.app.variables.PREF_CLIENTE;
import static com.example.dispensadorfirebase.app.variables.PREF_COMPLETADO;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURAR;
import static com.example.dispensadorfirebase.app.variables.PREF_DISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_ID;
import static com.example.dispensadorfirebase.app.variables.PREF_IDLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_LOGOLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_LOGOLOCALIMPRE;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBRELOCALSELECCIONADO;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBREUBICACIONDISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_NUMEROLOCALSELECCIONADO;
import static com.example.dispensadorfirebase.app.variables.ROOTINTERNO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dispensadorfirebase.BuildConfig;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterDispensador;
import com.example.dispensadorfirebase.adapter.AdapterDisplayGrande;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DisplayGrande extends AppCompatActivity {

    private static String NOMBREUBICACIONDISPOSITIVO = "NO";
    private static String NOMBRELOCALSELECCIONADO = "NO";
    private static String IDDISPOSITIVO = "NO";
    private static String ANYDESK = "NO";
    private static String ESTADOCONFIGURACION = "NO" ;
    private static String DISPOSITIVO = "NO";
    private static String CLIENTE = "NO";
    private static String IDLOCAL = "NO";
    private static String LOGOLOCAL="NO";
    private static String NUMEROLOCAL="NO";
    MediaPlayer mp;
    int posicion = 0;
    MediaPlayer click, click2;
    ConstraintLayout constrain;
    ActionBar actionBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private AlertDialog Adialog;
    AdapterDisplayGrande adapter;
    ArrayList<SectorLocal> list = new ArrayList<>();;
    ArrayList<SectoresElegidos> listtemp = new ArrayList<>();
    private SectorDB db = new SectorDB(this);
    private SharedPreferences pref;
    private ImageView logolocal;
    private TextView txtversion;
    private Button configurarnuevamente;
    private boolean listenerFirebaseCargado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_grande);
        txtversion = findViewById(R.id.txtversiondisplay);
        actionBar = getSupportActionBar();
        configurarnuevamente = findViewById(R.id.btn_configurar);
        logolocal = findViewById(R.id.logolocaldisplay);
        configurarnuevamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonregresar();
            }
        });
        constrain = findViewById(R.id.constrainLayoutGrande);
        click = MediaPlayer.create(DisplayGrande.this, R.raw.fin);
        click2 = MediaPlayer.create(DisplayGrande.this, R.raw.notidos);

        leerInicioSectores();
        adapter = new AdapterDisplayGrande(listtemp.size());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerviewgrande);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Orientación vertical
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Orientación horizontal
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        recyclerView.setAdapter(adapter);


        cargarConfiguracion();

    }


    private void cargarLogo(String LinkLogo) {

        Uri fondo = Uri.parse(LinkLogo);
        Glide.with(getApplicationContext()).load(fondo).into(logolocal);


    }



    private void botonregresar() {

        // load the dialog_promt_user.xml layout and inflate to view
        LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
        View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DisplayGrande.this);

        alertDialogBuilder.setView(promptUserView);

        final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.username);

        alertDialogBuilder.setTitle("Usuario Administrador: ");


        alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {



                if (!userAnswer.equals("") && userAnswer.getText().length()>0){

                    if (validaryguardar(userAnswer.getText().toString())){

                        SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(PREF_COMPLETADO, "NO");
                        editor.apply();
                        Intent intent= new Intent(DisplayGrande.this, InicioOpcionLocal.class);
                        startActivity(intent);
                        DisplayGrande.this.finish();
                    }

                }

            }
        });

        // all set and time to build and show up!
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        userAnswer.requestFocus();


    }



    @Override
    public void onBackPressed() {

        botonregresar();
    }
    public String getVersionName(){
        return BuildConfig.VERSION_NAME;
    }
    private void cargarConfiguracion() {

            pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
            ESTADOCONFIGURACION = pref.getString(PREF_COMPLETADO, "NO");
            DISPOSITIVO = pref.getString(PREF_DISPOSITIVO, "NO");
            CLIENTE= pref.getString(PREF_CLIENTE,"NO");
            IDLOCAL = pref.getString(PREF_IDLOCAL, "NO");
            LOGOLOCAL = pref.getString(PREF_LOGOLOCAL,"NO");
            NOMBREUBICACIONDISPOSITIVO = pref.getString(PREF_NOMBREUBICACIONDISPOSITIVO, "NO");
            NOMBRELOCALSELECCIONADO = pref.getString(PREF_NOMBRELOCALSELECCIONADO, "NO");
            IDDISPOSITIVO = pref.getString(PREF_ID, "NO");
            ANYDESK = pref.getString(PREF_ANYDESK, "NO");
            NUMEROLOCAL = pref.getString(PREF_NUMEROLOCALSELECCIONADO, "NO");
              txtversion.setText(String.format("Local: %s Version: %s Dispositivo: %s" , NOMBRELOCALSELECCIONADO + " "+ NUMEROLOCAL, getVersionName() , DISPOSITIVO));
            if (!CLIENTE.equals("NO") && !IDLOCAL.equals("NO") && ESTADOCONFIGURACION.equals("SI")){

                inicializarFirebase();
                if (!LOGOLOCAL.equals("NO")){
                    cargarLogo(LOGOLOCAL);
                }
            }else{
                regresarConfiguracion();
            }


    }
/*
    private void validarConfiguracion() {

       SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);

        DISPOSITIVO = pref.getString(PREF_DISPOSITIVO, "NO");
        CLIENTE= pref.getString(PREF_CLIENTE,"NO");
        DISPLAY_NOMBRELOCALSELECCIONADO = pref.getString(PREF_NOMBRELOCALSELECCIONADO, "NO");
        DISPLAY_IDLOCALSELECCIONADO = pref.getString(PREF_IDLOCAL, "NO");
        LOGOLOCAL = pref.getString(PREF_LOGOLOCAL,"NO");
        COMPLETADO = pref.getString(PREF_COMPLETADO,"NO");

        if (COMPLETADO.equals("NO")){
            regresarConfiguracion();
        }else{

            inicializarFirebase();
            if (!LOGOLOCAL.equals("NO")){
                cargarLogo(LOGOLOCAL);
            }
        }

    }
*/
    private boolean validaryguardar(String pass){
        boolean v = false;
        if (pass.equals(ROOTINTERNO)){
            v = true;
        }

        return v;
    }

    private void regresarConfiguracion(){

        SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_COMPLETADO, "NO");
        editor.putString(PREF_IDLOCAL, "NO");
        editor.apply();
        Intent intent= new Intent(DisplayGrande.this, InicioOpcionLocal.class);
        startActivity(intent);
        DisplayGrande.this.finish();

    }

    private void leerInicioSectores() {

        try {
            listtemp = db.loadSector();


        } catch (Exception e) {

            Log.e("Sectores Seleccionados", "ERROR");
            registrarError(EcargarDatos);
            regresarConfiguracion();
        }


    }

    private void registrarError(String msg) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String fechaCompletaLocal = dateFormat.format(date);

        String idError = fechaCompletaLocal.replace("/", "-").trim();

        Map<String, Object> datos = new HashMap<>();
        datos.put("id_Local", IDLOCAL);
        datos.put("nombre_Local", NOMBRELOCALSELECCIONADO);
        datos.put("nombre_Dispositivo", NOMBREUBICACIONDISPOSITIVO);
        datos.put("tipo_Dispositivo", DISPOSITIVO);
        datos.put("id_Anydesk", ANYDESK);
        datos.put("fecha_Error", fechaCompletaLocal);
        datos.put("tipo_Error", msg);
        databaseReference.child(NOMBRETABLAERROR).child(IDLOCAL).child(DISPOSITIVO).child(idError).setValue(datos);

    }

    public void destruir() {
            if (mp != null)
                mp.release();
        }

    public void iniciar() {
            destruir();
            mp = MediaPlayer.create(this, R.raw.notidosaumentadodos);
            mp.start();
            //String op = b1.getText().toString();
            mp.setLooping(false);

        }

    public void detener() {
        if (mp != null) {
            mp.stop();
            posicion = 0;
        }
    }

    private ValueEventListener Firebaselistener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            list.clear();

            for (DataSnapshot objSnaptshot : snapshot.getChildren()){

                SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                if ((sectores != null ? sectores.getEstado() : 0) ==1){

                    for (SectoresElegidos sectorelegido : listtemp) {
                        String idsector = IDLOCAL + sectores.getIdsector();
                        Log.e("Comparando : ", sectorelegido.getIdSectorFirebase() + " = "+ idsector);
                        if (sectorelegido.getIdSectorFirebase().equals(idsector)){
                            int numeroactual = sectorelegido.getUltimonumero();
                            int numeronuevo = sectores.getNumeroatendiendo();

                            if (numeroactual != numeronuevo){
                                sectorelegido.setUltimonumero(numeronuevo);
                                db.updateSector(sectorelegido);
                                //sectores.setColorSector("#FFE80606");
                                iniciar();
                                new Handler().postDelayed(new Runnable() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void run() {
                                        detener();
                                        // sectores.setColorSector(Color);
                                        // adapter.notifyDataSetChanged();
                                    }
                                },3000);
                            }
                            list.add(sectores);
                            break;
                        }

                    }
                }
            }

            Adialog.dismiss();
            actualizarReciclerView();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(DisplayGrande.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
            Adialog.dismiss();
        }
    };

    private void inicializarFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES);
        cargarListenerFirebase();

    }



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
                databaseReference.removeEventListener(Firebaselistener);
            }
        }
    }

    private void cargarListenerFirebase(){

        setProgressDialog();
        listenerFirebaseCargado = true;
        this.databaseReference.addValueEventListener(Firebaselistener);
    }


    public void actualizarReciclerView() {

            adapter.setNotes(list);
            adapter.notifyDataSetChanged();

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
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
        builder.setView(ll);

        Adialog = builder.create();
        Adialog.setCancelable(false);
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

}