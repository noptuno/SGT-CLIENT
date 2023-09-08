package com.example.dispensadorfirebase.aplicaciones.supervisor;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLASECTORES;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentServiceSupervisor extends IntentService {

    public static final String ACTION_PROGRESO = "net.sgoliver.intent.action.PROGRESO";
    public static final String ACTION_FIN = "net.sgoliver.intent.action.FIN";
    private Boolean firebaseListenerCargado = false;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SectorLocal sectorlocal;

    String IDLOCAL=null;
    String CLIENTE=null;

    private SectorDB db;

    private final static String CHANNEL_ID = "NOTIFICACION";

    public MyIntentServiceSupervisor() {
        super("MyIntentServiceSupervisor");

    }


    @Override
    protected void onHandleIntent(Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();

                IDLOCAL = intent.getExtras().getString("ID","NO");
                CLIENTE = intent.getExtras().getString("CLI","NO");
                 db = new SectorDB(getApplicationContext());
                intent.getDataString();

                setPendingIntent();
                createNotificationChannel();

                if (com.example.dispensadorfirebase.aplicaciones.supervisor.Constants.ACTION_RUN_ISERVICE.equals(action)) {

                    if (!IDLOCAL.equals("NO") && !CLIENTE.equals("NO")){
                        iniciarFirebaseListener();
                    }else{
                        Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
                        sendBroadcast(localIntent);
                    }


                }
        }
    }

    public final static int NOTIFICACION_ID = 0;
    private PendingIntent pendingIntent;



    private ValueEventListener Firebaselistener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            Log.e("A", "ENTRO al listener");

            ArrayList<SectorLocal> list2 = new ArrayList<>();

            int cont = 0;

            for (DataSnapshot objSnaptshot : snapshot.getChildren()){

                SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                Log.e("B", "FOR: " + cont++ + " "+ sectores.getNombreSector().toString());

                if (sectores.getEstado()==1){

                    String idsector = IDLOCAL+sectores.getIdsector();
                    SectoresElegidos sec = db.validarSector(idsector);

                    if (sec!=null){

                        Log.e("C", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() + " valido");

                        if (sectores.getLlamarsupervisor() == 2) {

                            Log.e("D", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() + " llamar supervisor es 2");

                            createNotification(sectores.getNombreSector().toString() + " ATENDER PROBLEMA");

                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("llamarsupervisor", 0);
                            databaseReference.child(sectores.getIdsector())
                                    .updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                Log.e("ERROR", "al actualziar llamarasupervisor");
                                            }
                                        }
                                    });

                            Log.e("F", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() + " notificar y vuelvo a = 0");

                        }

                        if (sectores.getLlamarsupervisor() == 1){

                            Log.e("G", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() + " llamar supervisor es 1");

                            createNotification(sectores.getNombreSector().toString() + "SOLICITAN SUPERVISOR");

                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("llamarsupervisor", 0);
                            databaseReference.child(sectores.getIdsector())
                                    .updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                Log.e("ERROR", "al actualziar llamarsupervisor");
                                            }
                                        }
                                    });

                            Log.e("H", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() + " notificar y vuelvo a = 0");

                        }

                        if (sectores.getNotificacion()==1 && sectores.getNotificaciondeshabilitar()==0){

                            Log.e("I", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() + " notificaicion es 1 y deshabilitar = 0");

                            if(sec.getUltimonumero()!= sectores.getUltimoNumeroDispensador()){

                                Log.e("J", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() + "  turno diferentes ");

                                sec.setUltimonumero(sectores.getUltimoNumeroDispensador());
                                db.updateSector(sec);
                                createNotification(sectores.getNombreSector().toString() + " "+ sectores.getCantidadEspera());
                                Log.e("J", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() + " notificar y actualziar turno ");

                            }else{

                                Log.e("K", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() + " turno iguales no notificar");

                            }

                        }else{

                            Log.e("L", sectores.getNombreSector() + " es " +IDLOCAL+sectores.getIdsector() +  " notificacion es " + sectores.getNotificacion()+ " y deshabilitar "+ sectores.getNotificaciondeshabilitar());

                        }

                        list2.add(sectores);

                    }
                }
            }

                Intent localIntent = new Intent(Constants.ACTION_RUN_ISERVICE).putExtra(Constants.EXTRA_PROGRESS,list2);
                sendBroadcast(localIntent);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public boolean registrarSectorElegido(SectoresElegidos sectorElegido) {

        try {
            if (db.validar(sectorElegido.getIdSectorFirebase())){
                db.updateSector(sectorElegido);
            }

            return true;

        } catch (Exception e) {
            Log.e("error", "mensaje registro o eliminar");
            return false;
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

    private void iniciarFirebaseListener() {
        Log.e("SUPER", "INICIAR SERVICE");
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDLOCAL).child(NOMBRETABLASECTORES);
        firebaseListenerCargado = true;
        databaseReference.addValueEventListener(Firebaselistener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SUPER", "DESTRUIR SERVICE");
    }
}