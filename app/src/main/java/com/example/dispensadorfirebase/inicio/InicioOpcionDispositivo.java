package com.example.dispensadorfirebase.inicio;

import static com.example.dispensadorfirebase.app.variables.PREF_ANYDESK;
import static com.example.dispensadorfirebase.app.variables.PREF_CLIENTE;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURACIONDMR;
import static com.example.dispensadorfirebase.app.variables.PREF_CONFIGURAR;
import static com.example.dispensadorfirebase.app.variables.PREF_DISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_ID;
import static com.example.dispensadorfirebase.app.variables.PREF_NOMBREUBICACIONDISPOSITIVO;
import static com.example.dispensadorfirebase.app.variables.PREF_UID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioOpcionDispositivo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static String ESTADOCONFIGURACIONDMR = "NO";
    private static String NOMBREUBICACIONDISPOSITIVO = "NO";
    private static String ANYDESK = "NO";
    private static String CLIENTE = "NO";
    private SharedPreferences pref;
    private FirebaseAuth mAuth;

    Button btnconfirmar;
    Spinner dispositivo;
    String dispositivo_seleccionado= null;
    ActionBar actionBar;
    EditText codigoAnydesk,codigoclientefirebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_opcion_dispositivo);

        btnconfirmar = findViewById(R.id.btnconfirmar);
        dispositivo = findViewById(R.id.spinner_dispositivo);
        codigoAnydesk= findViewById(R.id.edtanydesk);
        codigoclientefirebase= findViewById(R.id.edtcodigocliente);

        mAuth = FirebaseAuth.getInstance();

        ocultarbarra();

        dispositivo.setOnItemSelectedListener(this);
        btnconfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dispositivo_seleccionado.equals("Seleccione")){
                    Toast.makeText(InicioOpcionDispositivo.this, "Elegir dispositivo", Toast.LENGTH_LONG).show();
                }else{

                        if (codigoAnydesk.getText().length()>0 && codigoclientefirebase.getText().length()>0){
                            guardarAvanzar();
                        }else{
                            Toast.makeText(InicioOpcionDispositivo.this, "Faltan Datos", Toast.LENGTH_LONG).show();
                        }

                }

            }
        });

        abriraplicacion();

    }




    private void guardarAvanzar(){

        String ID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_CONFIGURACIONDMR, "SI");
        editor.putString(PREF_UID, mAuth.getUid());
        editor.putString(PREF_CLIENTE,codigoclientefirebase.getText().toString());
        editor.putString(PREF_DISPOSITIVO, dispositivo_seleccionado);
        //editor.putString(PREF_NOMBREUBICACIONDISPOSITIVO,nombredispositivo.getText().toString());
        editor.putString(PREF_ANYDESK,codigoAnydesk.getText().toString());
        editor.putString(PREF_ID,ID);
        editor.apply();
        Intent intent = new Intent(InicioOpcionDispositivo.this, InicioOpcionLocal.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();


    }


    private void log(String msg){

        Log.e("ENTRADAS",msg);

    }

    private void abriraplicacion() {

        pref = getSharedPreferences(PREF_CONFIGURAR, Context.MODE_PRIVATE);
        CLIENTE = pref.getString(PREF_CLIENTE, "NO");
        dispositivo_seleccionado = pref.getString(PREF_DISPOSITIVO, "NO");
        NOMBREUBICACIONDISPOSITIVO = pref.getString(PREF_NOMBREUBICACIONDISPOSITIVO, "NO");
        ANYDESK = pref.getString(PREF_ANYDESK, "NO");
        ESTADOCONFIGURACIONDMR = pref.getString(PREF_CONFIGURACIONDMR, "NO");
        codigoAnydesk.setText(ANYDESK);
        //nombredispositivo.setText(NOMBREUBICACIONDISPOSITIVO);

        if (ESTADOCONFIGURACIONDMR.equals("SI") && !CLIENTE.equals("NO")){

            Intent intent = new Intent(InicioOpcionDispositivo.this, InicioOpcionLocal.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();

        }

    }

    private void ocultarbarra() {

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id)
    {

        String item = parent.getItemAtPosition(pos).toString();
        dispositivo_seleccionado = item;

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {

        dispositivo_seleccionado = "Seleccione";

    }

}