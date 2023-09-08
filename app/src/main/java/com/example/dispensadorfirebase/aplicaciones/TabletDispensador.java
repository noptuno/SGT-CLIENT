package com.example.dispensadorfirebase.aplicaciones;

import static com.example.dispensadorfirebase.app.variables.EapiFecha;
import static com.example.dispensadorfirebase.app.variables.EcargarDatos;
import static com.example.dispensadorfirebase.app.variables.EcargarDatosLogos;
import static com.example.dispensadorfirebase.app.variables.Efecha;
import static com.example.dispensadorfirebase.app.variables.EfechaDispensador;
import static com.example.dispensadorfirebase.app.variables.EfechaTablet;
import static com.example.dispensadorfirebase.app.variables.Ehistorico;
import static com.example.dispensadorfirebase.app.variables.Eresettablet;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLAERROR;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLAREPORTE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLASECTORES;
import static com.example.dispensadorfirebase.app.variables.PREF_ANYDESK;
import static com.example.dispensadorfirebase.app.variables.PREF_CLIENTE;
import static com.example.dispensadorfirebase.app.variables.PREF_COMPLETADO;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURAR;
import static com.example.dispensadorfirebase.app.variables.PREF_DISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_IDLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_LOGOLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBRELOCALSELECCIONADO;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBREUBICACIONDISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_NUMEROLOCALSELECCIONADO;
import static com.example.dispensadorfirebase.app.variables.ROOTINTERNO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.dispensadorfirebase.BuildConfig;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.aplicaciones.supervisor.Supervisor_Principal;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.Datos;
import com.example.dispensadorfirebase.clase.SectorHistorico;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TabletDispensador extends AppCompatActivity {
    private boolean listenerFirebaseCargado = false;


    private static String NOMBREUBICACIONDISPOSITIVO = "NO";
    private static String NOMBRELOCALSELECCIONADO = "NO";
    private static String IDDISPOSITIVO = "NO";
    private static String ANYDESK = "NO";
    private static String NUMEROLOCAL = "NO";
    private static String ESTADOCONFIGURACION = "NO" ;
    private static String DISPOSITIVO = "NO";
    private static String CLIENTE = "NO";
    private static String IDLOCAL = "NO";
    private static String LOGOLOCAL="NO";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextView txtnumeroactual, txtcantidadespera, txtsector,txtlocalseleccionado;
    private MediaPlayer click, click2;
    int baselimite;
    private Button btnsupervisor;
    int limiteretroceder = 5;
    int retrocesos = 0;

    private SectorDB db = new SectorDB(this);
    private Button sumar,restar,reset;
    private SectorLocal datos = new SectorLocal();
    private SharedPreferences pref;
    private ArrayList<SectoresElegidos> listtemp = new ArrayList<>();;
    private Button configurarnuevamente;
    private Boolean fechaCorrecta = true;
    private ImageView logolocal;
    private AlertDialog dialogErrorPrinter;
    private ProgressBar load;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_dispensador);

        requestQueue = Volley.newRequestQueue(this);

        load = findViewById(R.id.progressBar2);
        txtlocalseleccionado = findViewById(R.id.txtlocaltablet);
        txtnumeroactual = findViewById(R.id.txtNumero_Actual);
        txtcantidadespera= findViewById(R.id.txtscantidad);
        txtsector = findViewById(R.id.txttnombresector);
        sumar = findViewById(R.id.btnSuma);
        restar = findViewById(R.id.btnResta);
        reset = findViewById(R.id.btnReset);
        btnsupervisor = findViewById(R.id.btnllamarsupervisor);
        click = MediaPlayer.create(TabletDispensador.this, R.raw.fin);
        click2 = MediaPlayer.create(TabletDispensador.this, R.raw.ckickk);
        logolocal = findViewById(R.id.logolocaltablet);

        configurarnuevamente = findViewById(R.id.btn_configurar3);
        configurarnuevamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botonregresar();

            }
        });

        leerInicioSectores();

        btnsupervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetDisponible()){
                    if (fechaCorrecta){
                        supervisor();
                    }else{
                        dialogErrorPrintet("Fecha Desactualizada");
                        validarFechayHora();
                        llamarsupervisorError();
                    }

                }else{
                    dialogErrorPrintet("No hay Red Internet");
                }

                }
        });

        sumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetDisponible()){
                   if (fechaCorrecta){
                       sumar();
                    }else{
                       dialogErrorPrintet("Fecha Desactualizada");
                       llamarsupervisorError();
                       validarFechayHora();
                   }

                }else{
                    dialogErrorPrintet("No hay Red Internet");
                }

            }
        });

        restar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetDisponible()){

                    if (fechaCorrecta){

                        if (retrocesos < limiteretroceder){

                         restar();

                        }else{

                            Toast.makeText(TabletDispensador.this, "El limite es de 10 turnos para retroceder", Toast.LENGTH_LONG).show();

                        }

                    }else{
                        dialogErrorPrintet("Fecha Desactualizada");
                        llamarsupervisorError();
                        validarFechayHora();
                    }

                }else{
                    dialogErrorPrintet("Sin internet");
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetDisponible()){

                    if (fechaCorrecta){

                        reset();

                    }else{

                        dialogErrorPrintet("Fecha Desactualziada");
                        llamarsupervisorError();
                        validarFechayHora();

                    }

                }else{

                    dialogErrorPrintet("No hay Red Internet");

                }

            }
        });
        validarFechayHora();
        Log.e("INICIADO", "OnCreated");

        cargarConfiguracion();

    }
    private String idSectorTemporal = "NO";

    private void llamarsupervisorError(){

        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("llamarsupervisor", 2);

        databaseReference.child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES).child(datos.getIdsector())
                .updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e("ERROR", "al actualziar");
                        }
                    }
                });
    }

    private void dialogErrorPrintet(String mensaje){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TabletDispensador.this);
        View mView = getLayoutInflater().inflate(R.layout.alerdialogerror, null);
        final TextView mPassword = mView.findViewById(R.id.txtmensajeerror);
        Button mLogin = mView.findViewById(R.id.btnReintentar);
        mPassword.setText(mensaje);
        mBuilder.setView(mView);
        final AlertDialog dialogg = mBuilder.create();
        dialogg.show();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogg.dismiss();

            }
        });

    }

    void validarFechayHora(){

        StringRequest request = new StringRequest(
                com.android.volley.Request.Method.GET,
                "https://apidmr.azurewebsites.net/api/v1/datetime/CAA35482-6B71-4F57-BE93-5E0436C481B4",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject json = new JSONObject(response);
                            String fechajson = json.getString("fecha");
                            String horajson = json.getString("hora");

                            SimpleDateFormat dateFormatcorta = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            SimpleDateFormat horaFormatcorta = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            Date date = new Date();

                            String fechaCortaLocal = dateFormatcorta.format(date);

                            String horaLOCAL = horaFormatcorta.format(date);
                            String horajsonsinsegundos = horajson.substring(0, 5);

                            if (fechajson.equals(fechaCortaLocal) && horajsonsinsegundos.equals(horaLOCAL)) {
                                fechaCorrecta = true;
                                Log.e("fechajson: ", "" + fechajson + " = " + fechaCortaLocal);
                                Log.e("horajsonsinsegundos: ", "" + horaLOCAL + " = " + horaLOCAL);

                            } else {
                                fechaCorrecta = false;
                                Log.e("fechajson: ", "" + fechajson + " = " + fechaCortaLocal);
                                Log.e("horajsonsinsegundos: ", "" + horaLOCAL + " = " + horaLOCAL);
                                registrarError(Efecha);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!isNetDisponible()){
                            registrarError(EapiFecha);
                        }

                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    private void registrarError(String msg) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String fechaCompletaLocal = dateFormat.format(date);
        String idError = fechaCompletaLocal.replace("/","-").trim();

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

    private void cargarLogo(String LinkLogo) {

        Uri fondo = Uri.parse(LinkLogo);
        Glide.with(getApplicationContext()).load(fondo).into(logolocal);

    }


    private void botonregresar() {

        // load the dialog_promt_user.xml layout and inflate to view
        LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
        View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TabletDispensador.this);

        alertDialogBuilder.setView(promptUserView);

        final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.username);

        alertDialogBuilder.setTitle("Usuario Administrador: ");

        // prompt for username
        alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // and display the username on main activity layout

                if (!userAnswer.equals("") && userAnswer.getText().length()>0){

                    if (validaryguardar(userAnswer.getText().toString())){
                        SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(PREF_COMPLETADO, "NO");
                        editor.apply();

                        Intent intent= new Intent(TabletDispensador.this, InicioOpcionLocal.class);
                        startActivity(intent);
                        TabletDispensador.this.finish();

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
       // super.onBackPressed();

    }


    private boolean validaryguardar(String pass){
        boolean v = false;
        if (pass.equals(ROOTINTERNO)){
            v = true;
        }

        return v;
    }

    private ValueEventListener Firebaselistener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            for (DataSnapshot objSnaptshot : snapshot.getChildren()){
                SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                if ((sectores != null ? sectores.getEstado() : 0) ==1){

                    String idsector = IDLOCAL + sectores.getIdsector();

                        for (SectoresElegidos sec : listtemp) {
                            if (sec.getIdSectorFirebase().equals(idsector)){


                                datos = sectores;
                                break;
                            }
                        }
                }
            }
            Actualizar();
            delay();


        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            //Toast.makeText(TabletDispensador.this, "Hubo con los datos", Toast.LENGTH_LONG).show();
            delay();
        }
    };

    void cargarListenerFirebase(){

        Log.e("INICIADO", "lISTENER");
        setProgressDialog();
        listenerFirebaseCargado = true;
        databaseReference.child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES).addValueEventListener(Firebaselistener);

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

    void supervisor(){

        click2.start();
        setProgressDialog();

        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("llamarsupervisor", 1);

        databaseReference.child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES).child(datos.getIdsector())
                .updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e("ERROR", "al actualizar");
                        }
                        delay();
                    }
                });
    }


    void sumar(){

    if (datos.getCantidadEspera()>0){
         click2.start();
         setProgressDialog();
         GuardarFirebaseTransaccion(1);
         }else{
        Toast.makeText(TabletDispensador.this, "No hay Clientes para atender", Toast.LENGTH_LONG).show();
        }

    }

    void restar(){

            click2.start();
            setProgressDialog();
            GuardarFirebaseTransaccion(2);

    }

    void reset(){

        permisos();

    }



    private void permisos() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TabletDispensador.this);
        View mView = getLayoutInflater().inflate(R.layout.alerdiaglog, null);
        final EditText mPassword = (EditText) mView.findViewById(R.id.etPassword);
        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);

        mBuilder.setView(mView);
        final AlertDialog dialogg = mBuilder.create();
        dialogg.show();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPassword.getText().toString().isEmpty()) {

                    String PASSWORD = ROOTINTERNO;
                    String PASSWORDROOT = "dmrmilrollos";

                    if (mPassword.getText().toString().equals(PASSWORD) || mPassword.getText().toString().equals(PASSWORDROOT) ){

                        click2.start();
                        setProgressDialog();
                        GuardarFirebaseTransaccion(3);
                        dialogg.dismiss();

                    }else{
                        Toast.makeText(TabletDispensador.this,"Acceso Denegado", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    mPassword.setError("Faltan Datos");
                    mPassword.requestFocus();

                }
            }
        });
    }


    void delay(){

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                btnsupervisor.setEnabled(true);
                sumar.setEnabled(true);
                restar.setEnabled(true);
                reset.setEnabled(true);
                load.setVisibility(View.INVISIBLE);
            }


        }, 1000);

    }


    private void GuardarFirebaseTransaccion(int sum){

        SimpleDateFormat dateFormatcorta = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat horaFormatcorta = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        String fechaCortaLocal = dateFormatcorta.format(date);
        String horaCortaLocal = horaFormatcorta.format(date);

        databaseReference.child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES).child(datos.getIdsector())
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        SectorLocal tabla = mutableData.getValue(SectorLocal.class);
                        if (tabla == null) {
                            return Transaction.success(mutableData);
                        }

                        String ultimaFecha = tabla.getUltimaFecha();
                            if (!ultimaFecha.equals("")) {
                                if (!ultimaFecha.equals(fechaCortaLocal)) {
                                    if (fechaCorrecta) {
                                        tabla.reset();
                                        tabla.setVariableNumeroTablet(1);
                                        tabla.setVariableNumero(1);
                                        tabla.setUltimaFecha(fechaCortaLocal);
                                    } else {
                                        registrarError(EfechaDispensador);
                                    }
                                }
                            }
                            if (tabla.getCantidadEspera() > tabla.getLimite()) {
                                tabla.setNotificacion(1);
                            }else{
                                tabla.setNotificacion(0);
                                tabla.setNotificaciondeshabilitar(0);
                            }

                        if (sum == 1){
                            if (tabla.sumarTablet()){
                                retrocesos--;
                            }

                        }else if (sum == 2){
                           if (tabla.restar()){
                               retrocesos++;
                           }

                        }else{
                            tabla.reset();
                            retrocesos = 0;

                        }

                        mutableData.setValue(tabla);
                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean committed,
                                           DataSnapshot currentData) {

                        SectorLocal tabla = currentData.getValue(SectorLocal.class);
                        if (committed) {
                            if (tabla != null) {
                                if (sum == 1){
                                    if (fechaCorrecta) {
                                        actualziarHistoricoDispensadorFirebase(tabla, fechaCortaLocal, horaCortaLocal);
                                    }
                                }
                            }
                        }else {
                            registrarError(Ehistorico + " " + tabla.getNumeroDispensador());
                        }

                        delay();
                    }
                });
    }



    private void actualziarHistoricoDispensadorFirebase(SectorLocal sector,String fechatcorta,String hora){


        String idhistorico = (fechatcorta.replace("/","-")).trim();

        int variable = sector.getVariableNumeroTablet();

        /*
        if (sector.getNumeroatendiendo() == 99) {
            if (variable > 1) {
                variable = sector.getVariableNumero() - 1;
            }
        }
        */

        String idReporte = sector.getIdsector() + "-" + sector.getNumeroatendiendo() + "-" + variable;
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("idLocal", IDLOCAL);
        hopperUpdates.put("idSector", sector.getIdsector());
        hopperUpdates.put("nombreSector", sector.getNombreSector());
        hopperUpdates.put("numeroDispensado", sector.getNumeroatendiendo());
        hopperUpdates.put("fecha_atencion", fechatcorta);
        hopperUpdates.put("hora_atencion", hora);
        databaseReference.child(NOMBRETABLAREPORTE).child(IDLOCAL).child(idhistorico).child(idReporte)
        .updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    registrarError(Ehistorico);
                }
            }
        });


    }

    void Actualizar(){
        txtnumeroactual.setText(""+datos.getNumeroatendiendo());
        txtcantidadespera.setText(""+datos.getCantidadEspera());
        baselimite = datos.getLimite();
        txtsector.setText(datos.getNombreSector());

    }

    private void inicializarFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE);
        cargarListenerFirebase();
    }


    public void setProgressDialog() {

        btnsupervisor.setEnabled(false);
        sumar.setEnabled(false);
        restar.setEnabled(false);
        reset.setEnabled(false);
        load.setVisibility(View.VISIBLE);
    }

    public String getVersionName(){
        return BuildConfig.VERSION_NAME;
    }

    private void cargarConfiguracion() {

            pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
            DISPOSITIVO = pref.getString(PREF_DISPOSITIVO, "NO");
            CLIENTE= pref.getString(PREF_CLIENTE,"NO");
            NOMBRELOCALSELECCIONADO = pref.getString(PREF_NOMBRELOCALSELECCIONADO, "NO");
            IDLOCAL = pref.getString(PREF_IDLOCAL, "NO");
            LOGOLOCAL = pref.getString(PREF_LOGOLOCAL,"NO");
            ESTADOCONFIGURACION = pref.getString(PREF_COMPLETADO,"NO");
            NOMBREUBICACIONDISPOSITIVO= pref.getString(PREF_NOMBREUBICACIONDISPOSITIVO,"NO");
            ANYDESK= pref.getString(PREF_ANYDESK,"NO");
            NUMEROLOCAL = pref.getString(PREF_NUMEROLOCALSELECCIONADO, "NO");
            txtlocalseleccionado.setText(String.format("Local: %s Version: %s Dispositivo: %s" , NOMBRELOCALSELECCIONADO + " "+ NUMEROLOCAL, getVersionName() , DISPOSITIVO));
        if (!CLIENTE.equals("NO") && !IDLOCAL.equals("NO") && ESTADOCONFIGURACION.equals("SI")){

                inicializarFirebase();

                if (!LOGOLOCAL.equals("NO")){
                   cargarLogo(LOGOLOCAL);
                }else{
                    registrarError(EcargarDatosLogos);
                }
            }else{
                registrarError(EcargarDatos);
                regresarConfiguracion();
            }
    }

    private void regresarConfiguracion(){

        Log.e("REGRESADO", "OnBack");
        SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_COMPLETADO, "NO");
        editor.putString(PREF_IDLOCAL, "NO");
        editor.apply();
        Intent intent= new Intent(TabletDispensador.this, InicioOpcionLocal.class);
        startActivity(intent);
        TabletDispensador.this.finish();

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

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        delay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("onDestroy", "destruido");
        if (Firebaselistener != null) {
            if (listenerFirebaseCargado){
                databaseReference.child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES).removeEventListener(Firebaselistener);
            }
        }
    }
}