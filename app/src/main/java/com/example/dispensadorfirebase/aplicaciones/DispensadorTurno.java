package com.example.dispensadorfirebase.aplicaciones;

import static com.example.dispensadorfirebase.app.variables.EapiFecha;
import static com.example.dispensadorfirebase.app.variables.EcargarDatos;
import static com.example.dispensadorfirebase.app.variables.EcargarDatosLogos;
import static com.example.dispensadorfirebase.app.variables.Edispositivo;
import static com.example.dispensadorfirebase.app.variables.Efecha;
import static com.example.dispensadorfirebase.app.variables.EfechaDispensador;
import static com.example.dispensadorfirebase.app.variables.Ehistorico;
import static com.example.dispensadorfirebase.app.variables.Eimpresora;
import static com.example.dispensadorfirebase.app.variables.EimpresoraBulk;
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
import static com.example.dispensadorfirebase.app.variables.PREF_ID;
import static com.example.dispensadorfirebase.app.variables.PREF_IDLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_LOGOLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_LOGOLOCALIMPRE;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBRELOCALSELECCIONADO;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBREUBICACIONDISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_NUMEROLOCALSELECCIONADO;
import static com.example.dispensadorfirebase.app.variables.ROOTINTERNO;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterDispensador;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.ClaseHistorico;
import com.example.dispensadorfirebase.clase.SectorHistorico;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.BuildConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.printer.sdk.utils.XLog;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DispensadorTurno extends AppCompatActivity {

    private String idSectorTemporal = "NO";
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
    private static String LOGOLOCALIMPRE = "NO";
    private boolean enviandoDatos = false;
    private boolean firebaseListenerCargado = false;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private boolean permisosimpresora = false;
    private boolean impresoraactiva = false;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn = null;
    private UsbEndpoint usbEndpointOut = null;
    private Context context = this;
    private AlertDialog Adialog;
    static final int MENSAJERESULT = 0;
    MediaPlayer click, click2;
    Bitmap starLogoImage = null;
    ConstraintLayout constrain;
    ActionBar actionBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView txt_nombrelocal;
    Boolean habilitar_boton_imprimir = true;
    private Button btnsubir;
    private UsbManager usbManager;
    int numeroactual;

    AdapterDispensador adapter;
    ArrayList<SectorLocal> list;
    ArrayList<SectoresElegidos> listtemp = new ArrayList<>();
    private SectorDB db;
    private SharedPreferences pref;
    private Button configurarnuevamente;
    private ImageView logo;
    private String iddispositivo;
    private StorageReference mstorage;
    private AlertDialog dialogErrorPrinter;
    private RequestQueue requestQueue;
    private String fechaGeneraL = "null";
    private String horaGeneral = "null";
    private boolean fechaCorrecta = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensador_turno_recicler);
        requestQueue = Volley.newRequestQueue(this);

        txt_nombrelocal = findViewById(R.id.txtlocalid);
        configurarnuevamente = findViewById(R.id.btn_salir);
        configurarnuevamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botonregresar();

            }
        });

        logo = findViewById(R.id.imviewlogolocal);
        list = new ArrayList<>();
        constrain = findViewById(R.id.constrain);
        click = MediaPlayer.create(DispensadorTurno.this, R.raw.fin);
        click2 = MediaPlayer.create(DispensadorTurno.this, R.raw.ckickk);
        actionBar = getSupportActionBar();
        context = getApplicationContext();

        leerSectoresLocales();
        adapter = new AdapterDispensador(listtemp.size());
        adapter.setOnNoteSelectedListener(new AdapterDispensador.OnNoteSelectedListener() {
            @Override
            public void onClick(SectorLocal note) {

                try {
                    idSectorTemporal = note.getIdsector();

                    if (isNetDisponible()) {
                        if (fechaCorrecta) {
                            if (impresoraactiva) {

                                int estadoImpresora = newGetCurrentStatus();

                                if (estadoImpresora == 0) {

                                    setProgressDialog();
                                    click2.start();
                                    GuardarFirebaseTransaccion(note);

                                } else if (estadoImpresora == -1) {
                                    XLog.i("TAG", "escribir err");
                                    dialogErrorPrintet("No Impresora Write");
                                    usb();
                                } else if (estadoImpresora == -2) {
                                    XLog.i("TAG", "Sin papel");
                                    dialogErrorPrintet("Sin Papel");
                                    if (!idSectorTemporal.equals("NO")){
                                        llamarsupervisorError();
                                    }
                                } else if (estadoImpresora == -3) {
                                    XLog.i("TAG", "se esta acabando");
                                   // dialogErrorPrintet("Poco Papel");
                                } else if (estadoImpresora == -4) {
                                    XLog.i("TAG", "abrir cubierta");
                                    dialogErrorPrintet("Tapa Abierta");
                                    if (!idSectorTemporal.equals("NO")){
                                        llamarsupervisorError();
                                    }
                                } else if (estadoImpresora == -5) {
                                    XLog.i("TAG", "enviar datos err");
                                    dialogErrorPrintet("No Impresora Load");
                                    usb();
                                }

                            } else {

                                usb();
                            }
                        } else {
                            dialogErrorPrintet("Fecha Desactualziada");
                            validarFechayHora();
                        }
                    } else {
                        dialogErrorPrintet("Sin Internet");
                    }

                } catch (Exception e) {
                    registrarError(Edispositivo);
                    dialogErrorPrintet("Reiniciar");
                }

            }

        });


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerviewprincipal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        cargarConfiguracion();

    }



    @Override
    protected void onStop() {
        super.onStop();

        if (Adialog!=null){
            Adialog.dismiss();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        validarFechayHora();
        usb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Firebaselistener != null) {
            if (firebaseListenerCargado){
                databaseReference.child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES).removeEventListener(Firebaselistener);
            }
        }

        close();
    }

    private void dialogErrorPrintet(String mensaje) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DispensadorTurno.this);
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

        /*
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.myDialog);
            builder.setCancelable(false);
            builder.setMessage("ERROR: " + mensaje)
                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            dialogErrorPrinter = builder.create();
            dialogErrorPrinter.show();
                });

*/
    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();
        return (actNetInfo != null && actNetInfo.isConnected());
    }

    private int escribirimpresora(byte[] printData) {
        int ret = -1;
        try {
            if (connection == null) {
                ret = -1;
            } else {
                int result = connection.bulkTransfer(usbEndpointOut, printData, printData.length, 1000);
                if (result < 0) {
                    ret = -3;
                } else {
                    ret = printData.length;
                }
            }

        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }


    private int leerimpresora(byte[] printData) {
        int ret = -1;
        try {
            if (connection == null) {
                ret = -1;
            } else {
                int result = connection.bulkTransfer(usbEndpointIn, printData, printData.length, 1000);
                if (result < 0) {
                    ret = -3;
                } else {
                    ret = result;

                }
            }
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }


    public int newGetCurrentStatus() {

        byte uncapData = this.getDatas(2);
        //  XLog.d("PrinterInstance", "TAPA" + uncapData);

        if (uncapData == -1) {
            return -1;
        } else if (uncapData == -2) {

            return -5;
        } else if ((uncapData & 4) != 0) {

            return -4;
        } else {
            byte paperData = this.getDatas(4);
            if (paperData == -1) {
                return -1;
            } else if (paperData == -2) {
                return -5;
            } else if ((paperData & 96) == 96) {
                return -2;
            } else if ((paperData & 12) == 12) {
                return -3;
            } else {
                return 0;
            }

        }
    }


    public byte getDatas(int statusType) {

        int readLength = -1;
        // byte[] retStatus = null;
        byte[] command = new byte[]{16, 4, 0};

        try {
            switch (statusType) {
                case 2:
                    command[2] = 2;
                    break;
                case 3:
                    command[2] = 3;
                    break;
                case 4:
                    command[2] = 4;
            }

            for (int m = 0; m < 3; ++m) {
                int sendLength = escribirimpresora(command);
                if (sendLength <= 0) {
                    if (m == 2) {
                        return -2;
                    }
                    Thread.sleep(50L);
                } else {
                    for (int i = 0; i < 10; ++i) {
                        byte[] buffer = new byte[1024];
                        readLength = leerimpresora(buffer);
                        if (readLength > 0) {
                            byte[] retStatus = new byte[readLength];
                            System.arraycopy(buffer, 0, retStatus, 0, readLength);
                            return retStatus[readLength - 1];
                        }

                        Thread.sleep(50L);
                    }

                    if (readLength <= 0) {
                        if (m == 2) {
                            return -1;
                        }
                    }
                }
            }
        } catch (InterruptedException var9) {
            var9.printStackTrace();
            XLog.e("PrinterInstance", "ERROR B" + var9.getMessage());
            return -1;
        }
        return -1;
    }


    private boolean validaryguardar(String pass) {
        boolean v = false;
        if (pass.equals(ROOTINTERNO)) {
            v = true;
        }

        return v;
    }

    public String getVersionName(){
        return BuildConfig.VERSION_NAME;
    }

    private void cargarConfiguracion() {

            pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
            ESTADOCONFIGURACION= pref.getString(PREF_COMPLETADO, "NO");
            DISPOSITIVO = pref.getString(PREF_DISPOSITIVO, "NO");
            NOMBREUBICACIONDISPOSITIVO = pref.getString(PREF_NOMBREUBICACIONDISPOSITIVO, "NO");
            CLIENTE = pref.getString(PREF_CLIENTE, "NO");
            NOMBRELOCALSELECCIONADO = pref.getString(PREF_NOMBRELOCALSELECCIONADO, "NO");
            IDLOCAL = pref.getString(PREF_IDLOCAL, "NO");
            LOGOLOCAL = pref.getString(PREF_LOGOLOCAL, "NO");
            LOGOLOCALIMPRE = pref.getString(PREF_LOGOLOCALIMPRE, "NO");
            iddispositivo = pref.getString(PREF_ID, "NO");
            ANYDESK = pref.getString(PREF_ANYDESK, "NO");
            NUMEROLOCAL = pref.getString(PREF_NUMEROLOCALSELECCIONADO, "NO");
            txt_nombrelocal.setText(String.format("Local: %s Version: %s Dispositivo: %s" , NOMBRELOCALSELECCIONADO + " "+ NUMEROLOCAL, getVersionName() , DISPOSITIVO));
            if (!CLIENTE.equals("NO") && !IDLOCAL.equals("NO") && ESTADOCONFIGURACION.equals("SI")) {
                inicializarFirebase();
                if (!LOGOLOCAL.equals("NO") && !LOGOLOCALIMPRE.equals("NO")) {
                    cargarLogo(LOGOLOCAL, LOGOLOCALIMPRE);
                } else {
                    registrarError(EcargarDatosLogos);
                }

            }else{
                registrarError(EcargarDatos);
                regresarConfiguracion();
            }
    }

    private void regresarConfiguracion() {

        pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_COMPLETADO, "NO");
        editor.putString(PREF_IDLOCAL, "NO");
        editor.apply();

        Intent intent = new Intent(DispensadorTurno.this, InicioOpcionLocal.class);
        startActivity(intent);

        DispensadorTurno.this.finish();

    }

    void validarFechayHora() {

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

    private void llamarsupervisorError(){

        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("llamarsupervisor", 2);
        databaseReference.child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES).child(idSectorTemporal)
                .updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e("ERROR", "al actualziar");
                        }
                    }
                });
    }

    private void GuardarFirebaseTransaccion(SectorLocal datos){

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatcorta = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat horaFormatcorta = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String fechaCompletaLocal = dateFormat.format(date);
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

                        } else {
                            tabla.setUltimaFecha(fechaCortaLocal);
                        }


                        tabla.sumarDispensdor();

                        if (tabla.getCantidadEspera() > tabla.getLimite()) {
                            tabla.setNotificacion(1);
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
                                if (fechaCorrecta) {
                                    registrarHistoricoDispensadorFirebase(tabla, fechaCortaLocal, horaCortaLocal);
                                }
                                byte[] escpos = PrepararDocumento(tabla, fechaCompletaLocal);
                                if  (!Imprimir(escpos)){
                                    impresoraactiva = false;
                                }
                            }
                        } else {
                            registrarError(Ehistorico + " " + tabla.getNumeroDispensador());
                        }
                        Adialog.dismiss();
                    }
                });

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

        if (!idSectorTemporal.equals("NO")){
            llamarsupervisorError();
        }


    }

    private void registrarHistoricoDispensadorFirebase(SectorLocal sector, String fecha, String hora) {

        String nombre = (fecha.replace("/", "-")).trim();
        int variable = sector.getVariableNumero();

        if (sector.getUltimoNumeroDispensador() == 99) {
            if (variable > 1) {
                variable = sector.getVariableNumero() - 1;
            }
        }

        String idReporte = sector.getIdsector() + "-" + sector.getUltimoNumeroDispensador() + "-" + variable;

        SectorHistorico datos = new SectorHistorico();
        datos.setIdSector(sector.getIdsector());
        datos.setNombreSector(sector.getNombreSector());
        datos.setIdDispositivo(iddispositivo);
        datos.setNombreDispositivo(NOMBREUBICACIONDISPOSITIVO);
        datos.setNumeroDispensado(sector.getUltimoNumeroDispensador());
        datos.setFecha_entrega(fecha);
        datos.setHora_entrega(hora);
        datos.setFecha_atencion("");
        datos.setHora_atencion("");
        datos.setIdLocal(IDLOCAL);
        datos.setLimite_superado(sector.getCantidadEspera());
        databaseReference.child(NOMBRETABLAREPORTE).child(IDLOCAL).child(nombre).child(idReporte).setValue(datos);

    }


    private void leerSectoresLocales() {

        try {
            db = new SectorDB(this);
            listtemp = db.loadSector();

        } catch (Exception e) {
            registrarError(EcargarDatos);
            regresarConfiguracion();
        }

    }


    private void botonregresar() {

        // load the dialog_promt_user.xml layout and inflate to view
        LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
        View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DispensadorTurno.this);
        alertDialogBuilder.setView(promptUserView);
        final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.username);
        alertDialogBuilder.setTitle("Usuario Administrador: ");

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // and display the username on main activity layout

                if (!userAnswer.equals("") && userAnswer.getText().length() > 0) {

                    if (validaryguardar(userAnswer.getText().toString())) {

                        SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(PREF_COMPLETADO, "NO");
                        editor.apply();

                        Intent intent = new Intent(DispensadorTurno.this, InicioOpcionLocal.class);
                        startActivity(intent);

                        DispensadorTurno.this.finish();

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
        //que no haga nada porque se usa una app modo kiosco
    }

    private void cargarLogo(String logolocal, String logoimpre) {

        try {

            Uri templogolocal = Uri.parse(logolocal);
            Uri templogolocalimpre = Uri.parse(logoimpre);

            Glide.with(getApplicationContext()).load(templogolocal).into(logo);
            Glide.with(getApplicationContext()).load(templogolocalimpre).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                    if (bitmap != null) {
                        starLogoImage = bitmap;
                    } else {
                        starLogoImage = null;
                    }
                }
            });

        } catch (Exception e) {
            dialogErrorPrintet("Logos Incorrectos");
            registrarError(EcargarDatosLogos);


            starLogoImage = null;
        }


    }


    /*
    //codigo para conseguir el realpath
    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getApplicationContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
*/

    private ValueEventListener Firebaselistener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            list.clear();
            for (DataSnapshot objSnaptshot : snapshot.getChildren()) {

                SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                if ((sectores != null ? sectores.getEstado() : 0) == 1) {

                    for (SectoresElegidos sec : listtemp) {

                        String idsector = IDLOCAL + sectores.getIdsector();

                        if (sec.getIdSectorFirebase().equals(idsector)) {
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
            Toast.makeText(DispensadorTurno.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
            Adialog.dismiss();
        }
    };



    private void cargarListenerFirebase() {

        setProgressDialog();
        firebaseListenerCargado = true;
        databaseReference.child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES).addValueEventListener(Firebaselistener);
    }

    public void actualizarReciclerView() {

        adapter.setNotes(list);
        adapter.notifyDataSetChanged();

    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        mstorage = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE);
        cargarListenerFirebase();

    }


    //código
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


    private Boolean Imprimir(byte[] printData){
        Boolean ret = false;
        try {
            if (connection!=null){
                int result = connection.bulkTransfer(usbEndpointOut, printData, printData.length, 1000);
                if (result != -1) {
                    ret = true;
                } else {
                    ret = false;
                }
            }
        } catch (Exception e) {
            ret = false;
            registrarError(EimpresoraBulk);

        }
        return ret;
    }



    private byte[] PrepararDocumento(SectorLocal datos,String fechaCompleta) {

        int anchoPapel = 570;

        Charset encoding = Charset.forName("UTF8");

        String nombre = datos.getNombreSector();

        byte[] nombresector1 = new byte[0];

        byte[] nombresector2 = new byte[0];

        boolean nombredoble = false;

        String[] partes;
        if (nombre.contains(" / ")) {
            partes = nombre.split(" / ");
            nombresector1= (partes[0].getBytes(encoding));
            nombresector2= (partes[1].getBytes(encoding));
            nombredoble= true;
        } else {
            nombresector1= nombre.getBytes(encoding);
            nombredoble = false;
        }

        byte[] nombreproducto= "Su Turno es: ".getBytes(encoding);
        byte[] numeroimprimir = (" "+datos.getUltimoNumeroDispensador()).getBytes();
        //Bitmap starLogoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.logodiscopeque);
        ICommandBuilder builder = StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPos);
        builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);

        builder.beginDocument();

        if (starLogoImage!=null){
            builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);
            builder.appendBitmapWithAbsolutePosition(starLogoImage,false,50);
            builder.appendLineFeed();
        }
        //*********************************
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);
        builder.appendMultiple(2, 2);

        if (nombredoble){
            builder.appendAbsolutePosition(nombresector1,40);
            builder.appendLineFeed();
            builder.appendAbsolutePosition(nombresector2,40);
            builder.appendLineFeed();
        }else{
            builder.appendAbsolutePosition(nombresector1,40);
            builder.appendLineFeed();
        }

        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);
        builder.appendMultiple(1, 1);
        builder.appendAbsolutePosition(nombreproducto,20);
        builder.appendLineFeed();
        builder.appendLineSpace(50);
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);
        builder.appendMultiple(10, 10);
        builder.appendAbsolutePosition(numeroimprimir,10);
        builder.appendLineFeed();
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);

        builder.appendMultiple(0, 0);
        builder.appendAbsolutePosition(("Fecha: " + fechaCompleta).getBytes(),20);
        builder.appendLineFeed();
        //**********************
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();

        return  builder.getCommands();

    }


    private void usb() {

        close();

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.e("Dispositivos", device.getDeviceName() + " + " + device.getVendorId() + " + " + device.getProductId());

            if (device.getVendorId() == 1155  && device.getProductId() == 22304) {

                if (usbManager.hasPermission(device)) {
                    permisosimpresora = true;
                    conectarImpresora(device);
                } else {
                    permisosimpresora = false;
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                    DispensadorTurno.this.registerReceiver(usbReceiver, filter);
                    usbManager.requestPermission(device, mPermissionIntent);
                }
            }
        }
    }

    private void conectarImpresora(UsbDevice device) {
        try {

            UsbInterface usbInterface = device.getInterface(0);
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                UsbEndpoint end = usbInterface.getEndpoint(i);
                if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                    usbEndpointIn = end;
                } else {
                    usbEndpointOut = end;
                }
            }
            connection = usbManager.openDevice(device);

            if (connection != null && connection.claimInterface(usbInterface, true)) {
                impresoraactiva = true;

            }else{
                impresoraactiva = false;
                dialogErrorPrintet("No Impresora Reiniciar");
            }

        } catch (Exception var2) {
            impresoraactiva = false;
            dialogErrorPrintet("No Impresora Reiniciar");
            registrarError(Eimpresora);

        }

    }

    void close(){

        try {
            if (this.connection != null) {
                this.connection.releaseInterface(this.usbInterface);
                this.connection.close();
                this.connection = null;
            }

        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }


    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    DispensadorTurno.this.unregisterReceiver(usbReceiver);
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(DispensadorTurno.this, "Permiso aceptado", Toast.LENGTH_SHORT).show();
                        permisosimpresora = true;
                        conectarImpresora(device);
                    } else {
                        Toast.makeText(DispensadorTurno.this, "Permiso no aceptado, OBLIGATORIO", Toast.LENGTH_LONG).show();
                        permisosimpresora = false;
                        usb();
                    }

                }
            }
        }
    };


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
        builder.setCancelable(true);
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

/*
    //buscar archivos internos
    private String leerHistorico(String fecha) {

        //Crear Clase de Registro
        Boolean exist = false;

        String nombreArchivo = (fecha.replace("/","-")+".txt").trim();


        String[] archivos = fileList();


        if (existe(archivos, nombreArchivo)){
            exist = true;
        }

        if (exist){
            return nombreArchivo;
        }else{
            return nombreArchivo = null;
        }

    }
*/
/*
//registrar datos en un archivo txt
    private void registrarHistorico(SectorLocal sector,String fecha,String hora) {

        //Crear Clase de Registro


        String nombre = (fecha.replace("/","-")+".txt").trim();
        SectorHistorico datos = new SectorHistorico();

       // datos.setCliente(CLIENTE);
      //  datos.setLocal(NOMBRELOCALSELECCIONADO);
       // datos.setId(iddispositivo);
        //datos.setNombreDispositivo(iddispositivo);
       // datos.setSector(sector.getNombreSector());
      //  datos.setTicket(sector.getUltimoNumeroDispensador());
       // datos.setFecha_entrega(fecha);
      //  datos.setHora_entrega(hora);
       // datos.setFecha_atencion("");
        //datos.setHora_atencion("");


        String[] archivosEncontrados = context.getFilesDir().list();

        Gson gson = new Gson();

        //valdiar que exista

        if (existe(archivosEncontrados, nombre)){

                try{
                    InputStreamReader archivo = new InputStreamReader(openFileInput(nombre));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    String todo = "";
                    while (linea != null) {
                        todo = todo + linea + "\n";
                        linea = br.readLine();
                    }

                    br.close();
                    archivo.close();
                    ClaseHistorico historico = gson.fromJson(todo, ClaseHistorico.class);
                    List<SectorHistorico> tickets = historico.getHistorico();
                    tickets.add(datos);
                    historico.setHistorico(tickets);
                    String JSONn = gson.toJson(historico);
                    grabar(JSONn,nombre);

                    Log.e("Json grabado ",JSONn);


                }catch (Exception e){
                    // notificacion importante realizar
                }

        }else{

            List<SectorHistorico> tickets = new ArrayList<>();
            tickets.add(datos);
            ClaseHistorico historico = new ClaseHistorico(DISPOSITIVO, tickets);
            String JSON = gson.toJson(historico);
            grabar(JSON,nombre);

        }

    }

*/
    /*
    private boolean existe(String[] archivos, String archbusca) {

        Boolean a = false;
        for (int f = 0; f < archivos.length; f++){
            Log.e("Base Archivos: ",archivos[f]);
            if (archbusca.equals(archivos[f])){
                a = true;
                break;
            }

        }

        return a;
    }

    public void grabar(String v,String direccion_nombre) {
        try {

            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(direccion_nombre, Activity.MODE_PRIVATE));
            archivo.write(v);
            archivo.flush();
            archivo.close();

        } catch (IOException e) {
            Log.e("error",e.toString());
        }

        Toast t = Toast.makeText(this, "Los datos fueron grabados",Toast.LENGTH_SHORT);
        t.show();
    }
*/
}