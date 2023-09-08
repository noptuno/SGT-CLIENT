package com.example.dispensadorfirebase.aplicaciones.supervisor;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLAUSUARIO;
import static com.example.dispensadorfirebase.app.variables.PREF_ANYDESK;
import static com.example.dispensadorfirebase.app.variables.PREF_CLIENTE;
import static com.example.dispensadorfirebase.app.variables.PREF_COMPLETADO;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURACIONDMR;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURAR;
import static com.example.dispensadorfirebase.app.variables.PREF_DISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_ESTADOINICIOSESION;
import static com.example.dispensadorfirebase.app.variables.PREF_IDLOCAL;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBREUBICACIONDISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_UID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.clase.Usuario;
import com.example.dispensadorfirebase.inicio.InicioOpcionDispositivo;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class InicioSesion extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Button btnconfirmar,btnsalir, notificaciontest;
    private EditText edtcorreo,edtpassword;
    private AlertDialog Adialog;
    private FirebaseAuth mAuth;
    private SharedPreferences pref;
    private TextView txtversion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

      txtversion = findViewById(R.id.txtversion);
      btnconfirmar = findViewById(R.id.btnConfirmar);
      btnsalir= findViewById(R.id.btnSalir);
      edtcorreo= findViewById(R.id.edtcorreo);
      edtpassword= findViewById(R.id.edtpassword);
      versionCode();

      btnconfirmar.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              String correo = edtcorreo.getText().toString();
              String pass = edtpassword.getText().toString();

              if (correo.length()>0 && pass.length()>0){

                  btnconfirmar.setEnabled(false);
                  setProgressDialog();
                  validarUsuario(correo,pass);

              }else{

                  Toast.makeText(InicioSesion.this,"Faltan datos",Toast.LENGTH_SHORT).show();
              }


          }
      });

      btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            finish();

            }
        });

        abriraplicacion();

    }

    String ESTADOCONFIGURAR = "NO";
    String CLIENTE = "NO";
    String IDLOCAL = "NO";
    String DISPOSITIVO = "SUPERVISOR";
    String ESTADOINICIOSESION = "NO";


    private void abriraplicacion() {

        pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        CLIENTE = pref.getString(PREF_CLIENTE, "NO");
        ESTADOCONFIGURAR = pref.getString(PREF_COMPLETADO, "NO");
        ESTADOINICIOSESION = pref.getString(PREF_ESTADOINICIOSESION, "NO");
        IDLOCAL = pref.getString(PREF_IDLOCAL, "NO");
        DISPOSITIVO = "SUPERVISOR";


        if (ESTADOINICIOSESION.equals("SI") && !CLIENTE.equals("NO")){

            Intent intent = new Intent(InicioSesion.this, InicioOpcionLocal.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();

        }else{

            inicializarFirebase();
        }

    }

    private void versionCode() {
        PackageInfo pinfo;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txtversion.setText("Version: "+pinfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void validarUsuario(String correo,String pass) {

            mAuth.signInWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(InicioSesion.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLAUSUARIO).child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Log.e("firebase", "Error getting data", task.getException());
                                            Toast.makeText(InicioSesion.this,"Error en la red",Toast.LENGTH_SHORT).show();
                                            btnconfirmar.setEnabled(true);
                                        }
                                        else
                                        {

                                            Map<String, Object> datos = (HashMap<String, Object>) task.getResult().getValue();

                                            if (datos.get("empresa")!=null){
                                                Intent intent = new Intent(InicioSesion.this, InicioOpcionLocal.class);
                                                startActivity(intent);

                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = pref.edit();
                                                editor.putString(PREF_DISPOSITIVO, "SUPERVISOR");
                                                editor.putString(PREF_ESTADOINICIOSESION, "SI");
                                                editor.putString(PREF_CLIENTE, datos.get("empresa").toString());
                                                editor.apply();
                                                btnconfirmar.setEnabled(true);
                                            }else{
                                                Toast.makeText(InicioSesion.this,"Usuario no coincide con cliente",Toast.LENGTH_SHORT).show();
                                                btnconfirmar.setEnabled(true);
                                            }


                                            btnconfirmar.setEnabled(true);
                                        }
                                    }
                                });
                                Adialog.dismiss();

                            }
                        }
                    });
    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
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
        //TODO revisar
        //Adialog.show();
        Adialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {



            }
        });

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