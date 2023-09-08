package com.example.dispensadorfirebase.inicio;


import static com.example.dispensadorfirebase.app.variables.EcargarDatos;
import static com.example.dispensadorfirebase.app.variables.EcargarDatosLogos;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;
import static com.example.dispensadorfirebase.app.variables.PREF_CLIENTE;
import static com.example.dispensadorfirebase.app.variables.PREF_COMPLETADO;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURACIONDMR;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURAR;
import static com.example.dispensadorfirebase.app.variables.PREF_DISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_ESTADOINICIOSESION;
import static com.example.dispensadorfirebase.app.variables.PREF_IDLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_LOGOLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_LOGOLOCALIMPRE;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBRELOCALSELECCIONADO;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBREUBICACIONDISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_NUMEROLOCALSELECCIONADO;
import static com.example.dispensadorfirebase.app.variables.ROOTDMR;
import static com.example.dispensadorfirebase.app.variables.ROOTINTERNO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.example.dispensadorfirebase.BuildConfig;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterLocal;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.example.dispensadorfirebase.aplicaciones.supervisor.InicioSesion;
import com.example.dispensadorfirebase.aplicaciones.supervisor.Supervisor_Principal;
import com.example.dispensadorfirebase.clase.Local;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InicioOpcionLocal extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static String NOMBREUBICACIONDISPOSITIVO = "NO";
    private static String NOMBRELOCALSELECCIONADO = "NO";
    private static String IDDISPOSITIVO = "NO";
    private static String ANYDESK = "NO";
    private static String ESTADOCONFIGURACION = "NO" ;
    private static String DISPOSITIVO = "NO";
    private static String CLIENTE = "NO";
    private static String IDLOCAL = "NO";
    private static String LOGOLOCAL="NO";
    ArrayList<Local> list;
    private boolean listenerFirebaseCargado = false;
    AdapterLocal adapter;
    AlertDialog Adialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferencelocales;
    ActionBar actionBar;
    private SharedPreferences pref;
    private EditText nombreEquipo;
    private Button btn_actualziar;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_opcion_local);

        nombreEquipo= findViewById(R.id.edit_nombreEquipo);
        btn_actualziar = findViewById(R.id.btn_actualizar);
        version = findViewById(R.id.local_txt_version);

        btn_actualziar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                volverAcargar();

            }
        });

        list = new ArrayList<>();
        adapter = new AdapterLocal();

        adapter.setOnNoteSelectedListener(new AdapterLocal.OnNoteSelectedListener() {
            @Override
            public void onClick(Local note) {


                Intent intent = new Intent(InicioOpcionLocal.this, InicioOpcionSectores.class);

                SharedPreferences pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(PREF_NOMBRELOCALSELECCIONADO, note.getNombreLocal());
                editor.putString(PREF_NUMEROLOCALSELECCIONADO, ""+note.getNumeroLocal());
                editor.putString(PREF_IDLOCAL,note.getIdLocal());
                editor.putString(PREF_LOGOLOCAL,note.getLogo());
                editor.putString(PREF_LOGOLOCALIMPRE,note.getLogoImpreso());
                editor.putString(PREF_NOMBREUBICACIONDISPOSITIVO,nombreEquipo.getText().toString());
                editor.apply();

                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                if (Firebaselistener!=null){
                    databaseReferencelocales.removeEventListener(Firebaselistener);
                    Log.e("fmr local cerro ", IDLOCAL);
                }

                //  idtablaserie.setText(note.getId());
                // numeroserie.setText(note.getNserie());
            }

        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerElegirLocal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        abriraplicacion();
        log("ENTRADA LOCAL");

    }

    private void volverAcargar() {

        if (Firebaselistener != null) {
            if (listenerFirebaseCargado){
                databaseReferencelocales.removeEventListener(Firebaselistener);
                setProgressDialog();
                listenerFirebaseCargado = true;
                databaseReferencelocales.addListenerForSingleValueEvent(Firebaselistener);
            }
        }

    }

    private ValueEventListener Firebaselistener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            list.clear();

            for (DataSnapshot objSnaptshot : snapshot.getChildren()){
                Local local = objSnaptshot.getValue(Local.class);

                if(local.getEstado()){

                    list.add(local);

                }
            }

            Adialog.dismiss();
            actualizarReciclerView();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
           // Toast.makeText(InicioOpcionLocal.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
            Adialog.dismiss();
        }
    };

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencelocales = firebaseDatabase.getReference(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES);
        cargarListenerFirebase();
    }

    private void cargarListenerFirebase(){
        setProgressDialog();
        listenerFirebaseCargado = true;
        databaseReferencelocales.addListenerForSingleValueEvent(Firebaselistener);
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
                databaseReferencelocales.removeEventListener(Firebaselistener);
            }
        }
    }

    private void log(String msg){
        Log.e("ENTRADAS",msg);
    }

    private void abriraplicacion() {

        pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        ESTADOCONFIGURACION = pref.getString(PREF_COMPLETADO, "NO");
        CLIENTE= pref.getString(PREF_CLIENTE, "NO");
        IDLOCAL = pref.getString(PREF_IDLOCAL, "NO");
        DISPOSITIVO = pref.getString(PREF_DISPOSITIVO, "NO");
        NOMBREUBICACIONDISPOSITIVO = pref.getString(PREF_NOMBREUBICACIONDISPOSITIVO,"NO");
        nombreEquipo.setText(NOMBREUBICACIONDISPOSITIVO);

        if (!CLIENTE.equals("NO") && !IDLOCAL.equals("NO") && ESTADOCONFIGURACION.equals("SI")){

            log("Locales -> dispositivo ");

            Intent intent = null;

            if (DISPOSITIVO.equals("DISPLAY 21PLG")) {

                intent = new Intent(InicioOpcionLocal.this, DisplayGrande.class);

            } else if (DISPOSITIVO.equals("TABLET 10PLG")) {

                intent = new Intent(InicioOpcionLocal.this, TabletDispensador.class);

            } else if (DISPOSITIVO.equals("DISPENSADOR")) {

                intent = new Intent(InicioOpcionLocal.this, DispensadorTurno.class);

            } else if   (DISPOSITIVO.equals("SUPERVISOR")) {

                intent = new Intent(InicioOpcionLocal.this, Supervisor_Principal.class);
            }

            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            InicioOpcionLocal.this.finish();

        }else{

            version.setText("Ver: " + getVersionName() + " Tip: "+ DISPOSITIVO);
            log("Locales -> iniciar listener ");
            inicializarFirebase();
        }
    }

    public String getVersionName(){
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:

                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(this);

                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        actualizarReciclerView();
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

                break;

            case R.id.volver:


        botonregresar();


                break;
        }
    return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {


        botonregresar();
        // super.onBackPressed();


    }


    private void botonregresar() {

            // load the dialog_promt_user.xml layout and inflate to view
            LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
            View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InicioOpcionLocal.this);

            alertDialogBuilder.setView(promptUserView);

            final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.username);

            alertDialogBuilder.setTitle("Usuario Administrador: ");

            // prompt for username
            alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // and display the username on main activity layout

                    if (!userAnswer.equals("") && userAnswer.getText().length()>0){

                        if (validaryguardar(userAnswer.getText().toString())){

                            guardarSharePreferencePrincipal();

                            if (DISPOSITIVO.equals("SUPERVISOR")){
                                Intent intent = new Intent(InicioOpcionLocal.this, InicioSesion.class);
                                pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(PREF_ESTADOINICIOSESION, "NO");
                                editor.putString(PREF_IDLOCAL, "NO");
                                editor.apply();
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                InicioOpcionLocal.this.finish();
                            }else{
                                Intent intent = new Intent(InicioOpcionLocal.this, InicioOpcionDispositivo.class);
                                pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(PREF_CONFIGURACIONDMR, "NO");
                                editor.putString(PREF_IDLOCAL, "NO");
                                editor.apply();
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                InicioOpcionLocal.this.finish();

                            }


                        }

                    }

                }
            });

            // all set and time to build and show up!
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            userAnswer.requestFocus();

    }


    private boolean validaryguardar(String pass){
        boolean v = false;
        if (pass.equals(ROOTDMR)){
            v = true;
        }

        return v;
    }

    private void guardarSharePreferencePrincipal() {


        pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_CONFIGURACIONDMR, "NO");
        editor.putString(PREF_DISPOSITIVO, "NO");
        editor.apply();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buscador, menu);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Local> filteredModelList = filter(list, newText);
        adapter.setNotes(filteredModelList);
        adapter.notifyDataSetChanged();
        return false;
    }

    private List<Local> filter(List<Local> models, String query) {
        query = query.toLowerCase();

        final List<Local> listafiltrada = new ArrayList<>();
        for (Local model : models) {
            final String text = model.getNombreLocal().toLowerCase();
            if (text.contains(query)) {
                listafiltrada.add(model);
            }
        }

        return listafiltrada;
    }


    public void actualizarReciclerView() {

        adapter.setNotes(list);
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






}