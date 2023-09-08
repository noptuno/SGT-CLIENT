package com.example.dispensadorfirebase.aplicaciones.supervisor;

import static com.example.dispensadorfirebase.app.variables.EcargarDatos;
import static com.example.dispensadorfirebase.app.variables.EcargarDatosLogos;
import static com.example.dispensadorfirebase.app.variables.Ehistorico;
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
import static com.example.dispensadorfirebase.app.variables.ROOTINTERNO;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispensadorfirebase.BuildConfig;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterDispensador;
import com.example.dispensadorfirebase.adapter.AdapterDisplayGrande;
import com.example.dispensadorfirebase.adapter.AdapterSectorLocal;
import com.example.dispensadorfirebase.adapter.AdapterSupervisorPrincipal;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.Datos;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionDispositivo;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Supervisor_Principal extends AppCompatActivity {

    private Button btDeshabilitar;
    private AlertDialog Adialog;
    ActionBar actionBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private PendingIntent pendingIntent;
    private boolean firebaseListenerCargado = false;
    private static String NOMBRELOCALSELECCIONADO = "NO";
    private static String IDDISPOSITIVO = "NO";
    private static String ESTADOCONFIGURACION = "NO" ;
    private static String DISPOSITIVO = "NO";
    private static String CLIENTE = "NO";
    private static String IDLOCAL = "NO";
    private final static String CHANNEL_ID = "NOTIFICACION";
    public final static int NOTIFICACION_ID = 0;
    private ArrayList<SectorLocal> list = new ArrayList<>();;
    private ArrayList<SectoresElegidos> listtemp = new ArrayList<>();
    private SectorDB db = new SectorDB(this);
    private SharedPreferences pref;
    private AdapterSupervisorPrincipal adapter;
    private Button regresar;
    private TextView txtversionapp,txtlocal,txtnumerolocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_principal);

        txtversionapp = findViewById(R.id.txtversionapp);
        txtlocal = findViewById(R.id.txt_supervisor_nombre_local);

        regresar = findViewById(R.id.btn_salir2);
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // load the dialog_promt_user.xml layout and inflate to view
                Regresar();

            }
        });
        actionBar = getSupportActionBar();
        actionBar.hide();

        adapter = new AdapterSupervisorPrincipal();
        list = new ArrayList<>();

        leerInicioSectores();
        cargarConfiguracion();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclersupervisor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        adapter.setOnDetailListener(new AdapterSupervisorPrincipal.OnNoteDetailListener() {
            @Override
            public void onDetail(SectorLocal note) {

                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("notificaciondeshabilitar", 1);

                SectorDB db = new SectorDB(getApplicationContext());
                String idsector = IDLOCAL+note.getIdsector();

                SectoresElegidos sec = db.validarSector(idsector);

                if (sec!=null){

                    databaseReference.child(note.getIdsector())
                            .updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {

                                    }
                                }
                            });
                }

            }
        });

    }



    public boolean registrarSectorElegido(SectoresElegidos sectorElegido) {

        try {
            db = new SectorDB(this);

            if (db.validar(sectorElegido.getIdSectorFirebase())){
                db.updateSector(sectorElegido);
            }

            return true;

        } catch (Exception e) {
            Log.e("error", "mensaje registro o eliminar");
            return false;
        }

    }

    void iniciarIntent(){

        IntentFilter filter = new IntentFilter(com.example.dispensadorfirebase.aplicaciones.supervisor.Constants.ACTION_RUN_ISERVICE);
        filter.addAction(com.example.dispensadorfirebase.aplicaciones.supervisor.Constants.ACTION_PROGRESS_EXIT);
        Supervisor_Principal.ResponseReceiver receiver = new Supervisor_Principal.ResponseReceiver();
        registerReceiver(receiver,filter);
        Intent intent = new Intent(this, MyIntentServiceSupervisor.class);
        intent.setAction(Constants.ACTION_RUN_ISERVICE);
        intent.putExtra("ID",IDLOCAL);
        intent.putExtra("CLI",CLIENTE);
        startService(intent);

    }


    void Regresar(){

        LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
        View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Supervisor_Principal.this);

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

                        Intent intent= new Intent(Supervisor_Principal.this, InicioOpcionLocal.class);
                        startActivity(intent);
                        Supervisor_Principal.this.finish();

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

        Regresar();
    }

    private class ResponseReceiver extends BroadcastReceiver {

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case Constants.ACTION_RUN_ISERVICE:
                    //list.clear();

                    ArrayList<SectorLocal> listaSectores = (ArrayList<SectorLocal>) intent.getSerializableExtra(Constants.EXTRA_PROGRESS);
                    actualziarSectores(listaSectores);
                    break;

                case Constants.ACTION_PROGRESS_EXIT:
                   regresarConfiguracion();
                    break;
            }
        }
    }


    private boolean validaryguardar(String pass) {

            boolean v = false;
            if (pass.equals(ROOTINTERNO)){
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
        CLIENTE = pref.getString(PREF_CLIENTE, "NO");
        NOMBRELOCALSELECCIONADO = pref.getString(PREF_NOMBRELOCALSELECCIONADO, "NO");
        IDLOCAL = pref.getString(PREF_IDLOCAL, "NO");
        txtversionapp.setText(getVersionName());

        if (!CLIENTE.equals("NO") && !IDLOCAL.equals("NO") && !ESTADOCONFIGURACION.equals("NO")) {
            inicializarFirebase();

            txtlocal.setText(NOMBRELOCALSELECCIONADO);

        }else{
            regresarConfiguracion();
        }

    }


    private void regresarConfiguracion(){

        SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_COMPLETADO, "NO");
        editor.apply();

        Intent intent= new Intent(Supervisor_Principal.this, InicioOpcionLocal.class);
        startActivity(intent);
        Supervisor_Principal.this.finish();


    }


    public void actualizarReciclerView() {
        adapter.setNotes(list);
        adapter.notifyDataSetChanged();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void actualziarSectores(ArrayList<SectorLocal>  lista) {

        Log.e("SUPER", "ACTUALIZAR LIST");
            list.clear();
            list=lista;
        actualizarReciclerView();

    }


    private void leerInicioSectores() {

        try {
            listtemp = db.loadSector();

            if (listtemp.size()==0){
                SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(PREF_COMPLETADO, "NO");
                editor.apply();

                Intent intent= new Intent(Supervisor_Principal.this, InicioOpcionLocal.class);
                startActivity(intent);
                Supervisor_Principal.this.finish();
            }
        } catch (Exception e) {
            Log.e("error", "mensaje mostrar bse local");
            regresarConfiguracion();
        }
    }

    private void inicializarFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES);
        iniciarIntent();
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



    private void setPendingIntent(){
        Intent intent = new Intent(this, Supervisor_Principal.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Supervisor_Principal.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotification(String mensaje){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_sms_black_24dp);
        builder.setContentTitle("Notificacion");
        builder.setContentText(mensaje);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

       builder.setContentIntent(pendingIntent);
       // builder.addAction(R.drawable.ic_sms_black_24dp, "Si", siPendingIntent);
       // builder.addAction(R.drawable.ic_sms_black_24dp, "No", noPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }
}
